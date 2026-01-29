package com.example.mealway.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.mealway.data.callback.AuthCallback;
import com.example.mealway.data.local.LocalDataSource;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.model.MealAppointment;
import com.example.mealway.data.remote.firebase.FirebaseManager;
import com.example.mealway.utils.AlertUtils;
import com.example.mealway.utils.ErrorUtils;
import com.example.mealway.utils.NetworkMonitor;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AuthRepositoryImpl implements AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final LocalDataSource localDataSource;
    private final FirebaseManager firebaseManager;
    private final Context context;

    public AuthRepositoryImpl(Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.localDataSource = new LocalDataSource(context);
        this.firebaseManager = new FirebaseManager();
    }

    @Override
    public boolean isLoggedIn() {
        return firebaseAuth.getCurrentUser() != null && localDataSource.isLoggedIn();
    }

    @Override
    public boolean isOnboardingCompleted() {
        return localDataSource.isOnboardingCompleted();
    }

    @Override
    public void setOnboardingCompleted(boolean completed) {
        localDataSource.saveOnboardingState(completed);
    }

    @SuppressLint("CheckResult")
    @Override
    public void login(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        syncUserData()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    localDataSource.saveLoginState(true);
                                    callback.onSuccess();
                                }, throwable -> {
                                    localDataSource.saveLoginState(true); 
                                    callback.onSuccess();
                                });
                    } else {
                        callback.onFailure(ErrorUtils.getAuthErrorMessage(context, task.getException()));
                    }
                });
    }

    @Override
    public void register(String email, String password, String fullName, String phone, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        localDataSource.clearAllFavorites()
                                .andThen(localDataSource.clearAllAppointments())
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

                        db.collection("users").document(uid).set(user)
                                .addOnSuccessListener(aVoid -> {
                                    localDataSource.saveLoginState(true);
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> callback.onFailure("Auth succeeded but failed to save profile"));
                    } else {
                        callback.onFailure(ErrorUtils.getAuthErrorMessage(context, task.getException()));
                    }
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void signInWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("email", user.getEmail());
                            userData.put("fullName", user.getDisplayName());
                            userData.put("uid", user.getUid());

                            FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                                    .set(userData, com.google.firebase.firestore.SetOptions.merge());
                        }

                        syncUserData()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    localDataSource.saveLoginState(true);
                                    callback.onSuccess();
                                }, throwable -> {
                                    localDataSource.saveLoginState(true);
                                    callback.onSuccess();
                                });
                    } else {
                        callback.onFailure(ErrorUtils.getAuthErrorMessage(context, task.getException()));
                    }
                });
    }

    @Override
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    @Override
    public Completable signOut() {
        if (!NetworkMonitor.isNetworkAvailable(context)) {
            AlertUtils.showConfirmation(
                    context,
                    "Offline",
                    "Cannot logout while offline.",
                    "Try Again",
                    () -> {
                        signOut().subscribe();
                    }
            );
            return Completable.complete();
        }

        firebaseAuth.signOut();
        localDataSource.saveLoginState(false);
        return localDataSource.clearAllFavorites()
                .andThen(localDataSource.clearAllAppointments())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



    @Override
    public Completable syncUserData() {
        if (getCurrentUser() == null) {
            return Completable.error(new Exception("Not logged in"));
        }

        return localDataSource.clearAllFavorites()
                .andThen(localDataSource.clearAllAppointments())
                .andThen(firebaseManager.getFavorites()
                        .observeOn(Schedulers.io())
                        .flatMap(meals -> {
                            if (meals == null || meals.isEmpty())
                                return Single.just(new ArrayList<Meal>());
                            List<Meal> validMeals = new ArrayList<>();
                            for (Meal m : meals) {
                                if (m != null && m.getIdMeal() != null) {
                                    m.setFavorite(true);
                                    validMeals.add(m);
                                }
                            }
                            if (validMeals.isEmpty()) return Single.just(validMeals);
                            return localDataSource.insertAllFavMeals(validMeals).toSingleDefault(validMeals);
                        })
                        .flatMap(meals -> firebaseManager.getAppointments())
                        .observeOn(Schedulers.io())
                        .flatMap(appointments -> {
                            if (appointments == null || appointments.isEmpty()) return Single.just(new ArrayList<MealAppointment>());
                            List<MealAppointment> validApps = new ArrayList<>();
                            for (MealAppointment a : appointments) {
                                if (a != null && a.getId() != null) {
                                    validApps.add(a);
                                }
                            }
                            if (validApps.isEmpty()) return Single.just(validApps);
                            return localDataSource.insertAllAppointments(validApps).toSingleDefault(validApps);
                        })
                        .ignoreElement());
    }

    @Override
    public void getUserDetails(UserDataCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onDataFetched("Guest", "N/A", "guest@mealway.com", null);
            return;
        }

        FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onDataFetched(
                                doc.getString("fullName") != null ? doc.getString("fullName") : user.getDisplayName(),
                                doc.getString("phone"),
                                user.getEmail(),
                                doc.getString("profileImage")
                        );
                    } else {
                        callback.onDataFetched(
                                user.getDisplayName() != null ? user.getDisplayName() : "User",
                                user.getPhoneNumber(),
                                user.getEmail(),
                                user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null
                        );
                    }
                })
                .addOnFailureListener(e -> {
                    if (!com.example.mealway.utils.NetworkMonitor.isNetworkAvailable(context)) {
                        callback.onDataFetched(user.getDisplayName(), "Offline", user.getEmail(),
                                user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);
                    } else {
                        callback.onError(e.getMessage());
                    }
                });
    }

    @Override
    public Completable uploadProfileImage(Uri imageUri) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            return Completable.error(new Exception("User not logged in"));
        }

        String uid = user.getUid();
        return Completable.create(emitter -> {
            try {
                InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                if (selectedImage == null) {
                    emitter.onError(new Exception("Failed to decode image"));
                    return;
                }

                int maxSize = 300;
                int width = selectedImage.getWidth();
                int height = selectedImage.getHeight();
                float ratio = (float) width / (float) height;
                if (ratio > 1) {
                    width = maxSize;
                    height = (int) (maxSize / ratio);
                } else {
                    height = maxSize;
                    width = (int) (maxSize * ratio);
                }
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(selectedImage, width, height, true);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] b = baos.toByteArray();
                
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                String dataUrl = "data:image/jpeg;base64," + encodedImage;

                Map<String, Object> data = new HashMap<>();
                data.put("profileImage", dataUrl);

                FirebaseFirestore.getInstance().collection("users").document(uid)
                        .set(data, com.google.firebase.firestore.SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            if (!emitter.isDisposed()) {
                                emitter.onComplete();
                            }
                        })
                        .addOnFailureListener(e -> {
                            if (!emitter.isDisposed()) {
                                emitter.onError(e);
                            }
                        });

            } catch (Exception e) {
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
            }
        });
    }


    @Override
    public String getProfileImageUrl() {
        FirebaseUser user = getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            return user.getPhotoUrl().toString();
        }
        return null;
    }
}
