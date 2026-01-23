package com.example.mealway.data.repository;

import com.example.mealway.data.callback.AuthCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.mealway.data.local.AppDatabase;
import com.example.mealway.data.local.LocalDataSource;
import com.example.mealway.data.local.MealDao;
import com.example.mealway.data.remote.firebase.FirebaseManager;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.model.MealAppointment;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class AuthRepositoryImpl implements AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final LocalDataSource localDataSource;
    private final MealDao mealDao;
    private final FirebaseManager firebaseManager;

    public AuthRepositoryImpl(android.content.Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.localDataSource = new LocalDataSource(context);
        this.mealDao = AppDatabase.getInstance(context).mealDao();
        this.firebaseManager = new FirebaseManager();
    }

    @Override
    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null && localDataSource.isLoggedIn();
    }

    @SuppressLint("CheckResult")
    @Override
    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        localDataSource.saveLoginState(true);
                        // Notify success immediately to prevent UI freeze
                        callback.onSuccess();
                        
                        mealDao.clearAllFavorites()
                                .andThen(mealDao.clearAllAppointments())
                                .subscribeOn(Schedulers.io())
                                .subscribe(() -> syncUserData(null), throwable -> {});
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Authentication failed");
                    }
                });
    }

    @Override
    public void register(String email, String password, String fullName, String phone, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear local DB before starting a new session for a new user
                        mealDao.clearAllFavorites()
                                .andThen(mealDao.clearAllAppointments())
                                .subscribeOn(Schedulers.io())
                                .subscribe();
                        
                        String uid = firebaseAuth.getCurrentUser().getUid();
                        
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build();
                        firebaseAuth.getCurrentUser().updateProfile(profileUpdates);

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> user = new HashMap<>();
                        user.put("fullName", fullName);
                        user.put("phone", phone);
                        user.put("email", email);
                        user.put("uid", uid);
                        user.put("photoUrl", null); 

                        db.collection("users").document(uid).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    localDataSource.saveLoginState(true);
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> callback.onFailure("Auth succeeded but failed to save profile"));
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                        callback.onFailure(error);
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void signInWithGoogle(String idToken, AuthCallback callback) {
        com.google.firebase.auth.AuthCredential credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        localDataSource.saveLoginState(true);
                        callback.onSuccess();
                        
                        mealDao.clearAllFavorites()
                                .andThen(mealDao.clearAllAppointments())
                                .subscribeOn(Schedulers.io())
                                .subscribe(() -> syncUserData(null), throwable -> {});
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Google Sign-In failed";
                        callback.onFailure(error);
                    }
                });
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    @SuppressLint("CheckResult")
    @Override
    public void signOut() {
        firebaseAuth.signOut();
        localDataSource.saveLoginState(false);
        // Clear local database to prevent data leaking
        mealDao.clearAllFavorites()
                .andThen(mealDao.clearAllAppointments())
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {}, throwable -> {});
    }

    @SuppressLint("CheckResult")
    @Override
    public void uploadProfileImage(Uri imageUri, AuthCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onFailure("User not logged in");
            return;
        }

        Completable.create(emitter -> {
            try {
                Context context = localDataSource.getContext();
                
                // Downscale image to prevent OOM
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                InputStream is = context.getContentResolver().openInputStream(imageUri);
                BitmapFactory.decodeStream(is, null, options);
                if (is != null) is.close();

                int inSampleSize = 1;
                if (options.outHeight > 1024 || options.outWidth > 1024) {
                    final int halfHeight = options.outHeight / 2;
                    final int halfWidth = options.outWidth / 2;
                    while ((halfHeight / inSampleSize) >= 1024 && (halfWidth / inSampleSize) >= 1024) {
                        inSampleSize *= 2;
                    }
                }
                options.inSampleSize = inSampleSize;
                options.inJustDecodeBounds = false;

                is = context.getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
                if (is != null) is.close();

                if (bitmap == null) throw new Exception("Failed to decode bitmap");
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                byte[] data = baos.toByteArray();
                bitmap.recycle(); // Free memory

                String fileName = user.getUid() + "_" + System.currentTimeMillis() + ".jpg";
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference fileRef = storageRef.child("profile_pics/" + fileName);

                android.util.Log.d("AuthRepo", "Starting upload to: " + fileRef.getPath());

                fileRef.putBytes(data)
                        .addOnSuccessListener(taskSnapshot -> {
                            android.util.Log.d("AuthRepo", "Upload successful, fetching download URL...");
                            fileRef.getDownloadUrl()
                                    .addOnSuccessListener(downloadUri -> {
                                        android.util.Log.d("AuthRepo", "Got download URL: " + downloadUri);
                                        
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(downloadUri)
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnSuccessListener(aVoid -> {
                                                    FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                                                            .update("photoUrl", downloadUri.toString())
                                                            .addOnSuccessListener(val -> {
                                                                android.util.Log.d("AuthRepo", "User profile and Firestore updated successfully");
                                                                if (!emitter.isDisposed()) emitter.onComplete();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                android.util.Log.e("AuthRepo", "Firestore update failed", e);
                                                                if (!emitter.isDisposed()) emitter.onError(e);
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    android.util.Log.e("AuthRepo", "Auth profile update failed", e);
                                                    if (!emitter.isDisposed()) emitter.onError(e);
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        android.util.Log.e("AuthRepo", "Failed to get download URL", e);
                                        if (!emitter.isDisposed()) emitter.onError(e);
                                    });
                        })
                        .addOnFailureListener(e -> {
                            android.util.Log.e("AuthRepo", "Upload task failed", e);
                            if (!emitter.isDisposed()) emitter.onError(e);
                        });
            } catch (Exception e) {
                android.util.Log.e("AuthRepo", "Image processing failed", e);
                if (!emitter.isDisposed()) emitter.onError(e);
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(
              () -> {
                  android.util.Log.d("AuthRepo", "Upload flow completed successfully");
                  callback.onSuccess();
              },
              throwable -> {
                  android.util.Log.e("AuthRepo", "Overall upload flow failed", throwable);
                  callback.onFailure("Upload error: " + throwable.getMessage());
              }
          );
    }

    @SuppressLint("CheckResult")
    @Override
    public void syncUserData(AuthCallback callback) {
        if (getCurrentUser() == null) {
            if (callback != null) callback.onFailure("Not logged in");
            return;
        }

        firebaseManager.getFavorites()
                .observeOn(Schedulers.io())
                .flatMapCompletable(meals -> {
                    if (meals == null || meals.isEmpty()) return Completable.complete();
                    List<Meal> validMeals = new java.util.ArrayList<>();
                    for (Meal m : meals) {
                        if (m != null && m.getIdMeal() != null) {
                            m.setFavorite(true);
                            validMeals.add(m);
                        }
                    }
                    return validMeals.isEmpty() ? Completable.complete() : mealDao.insertAllFavMeals(validMeals);
                })
                .andThen(firebaseManager.getAppointments())
                .observeOn(Schedulers.io())
                .flatMapCompletable(appointments -> {
                    if (appointments == null || appointments.isEmpty()) return Completable.complete();
                    List<MealAppointment> validApps = new java.util.ArrayList<>();
                    for (MealAppointment a : appointments) {
                        if (a != null && a.getId() != null) {
                            validApps.add(a);
                        }
                    }
                    return validApps.isEmpty() ? Completable.complete() : mealDao.insertAllAppointments(validApps);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> { if (callback != null) callback.onSuccess(); },
                        throwable -> { 
                            android.util.Log.e("AuthRepo", "Sync error: " + throwable.getMessage());
                            if (callback != null) callback.onFailure("Sync error: " + throwable.getMessage()); 
                        }
                );
    }

    @Override
    public void getUserDetails(UserDataCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onDataFetched("Guest", "N/A", "guest@mealway.com", null);
            return;
        }

        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onDataFetched(
                                doc.getString("fullName"),
                                doc.getString("phone"),
                                user.getEmail(),
                                user.getPhotoUrl()
                        );
                    } else {
                        callback.onDataFetched(user.getDisplayName(), "Not set", user.getEmail(), user.getPhotoUrl());
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
