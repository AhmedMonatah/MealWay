package com.example.mealway.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.mealway.data.callback.AuthCallback;
import com.example.mealway.data.local.AppDatabase;
import com.example.mealway.data.local.LocalDataSource;
import com.example.mealway.data.local.MealDao;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.model.MealAppointment;
import com.example.mealway.data.remote.firebase.FirebaseManager;
import com.example.mealway.utils.ErrorUtils;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AuthRepositoryImpl implements AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final LocalDataSource localDataSource;
    private final MealDao mealDao;
    private final FirebaseManager firebaseManager;
    private final Context context;

    public AuthRepositoryImpl(android.content.Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.localDataSource = new LocalDataSource(context);
        this.mealDao = AppDatabase.getInstance(context).mealDao();
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
                        localDataSource.saveLoginState(true);
                        callback.onSuccess();
                        
                        mealDao.clearAllFavorites()
                                .andThen(mealDao.clearAllAppointments())
                                .subscribeOn(Schedulers.io())
                                .subscribe(() -> syncUserData(null), throwable -> {});
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
                        localDataSource.saveLoginState(true);
                        callback.onSuccess();
                        
                        mealDao.clearAllFavorites()
                                .andThen(mealDao.clearAllAppointments())
                                .subscribeOn(Schedulers.io())
                                .subscribe(() -> syncUserData(null), throwable -> {});
                    } else {
                        callback.onFailure(ErrorUtils.getAuthErrorMessage(context, task.getException()));
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
            callback.onDataFetched("Guest", "N/A", "guest@mealway.com");
            return;
        }

        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onDataFetched(
                                doc.getString("fullName"),
                                doc.getString("phone"),
                                user.getEmail()
                        );
                    } else {
                        callback.onDataFetched(user.getDisplayName(), "Not set", user.getEmail());
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
