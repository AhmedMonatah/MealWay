package com.example.mealway.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.mealway.data.callback.AuthCallback;
import com.example.mealway.data.local.LocalDataSource;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
                        localDataSource.saveLoginState(true);
                        callback.onSuccess();
                        
                        localDataSource.clearAllFavorites()
                                .andThen(localDataSource.clearAllAppointments())
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
                        localDataSource.saveLoginState(true);
                        callback.onSuccess();
                        
                        localDataSource.clearAllFavorites()
                                .andThen(localDataSource.clearAllAppointments())
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
        localDataSource.clearAllFavorites()
                .andThen(localDataSource.clearAllAppointments())
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> { if (callback != null) callback.onSuccess(); },
                        throwable -> { 
                            Log.e("AuthRepo", "Sync error: " + throwable.getMessage());
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
