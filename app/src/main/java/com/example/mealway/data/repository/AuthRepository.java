package com.example.mealway.data.repository;

import com.example.mealway.data.callback.AuthCallback;
import com.google.firebase.auth.FirebaseUser;
import android.net.Uri;
import io.reactivex.rxjava3.core.Completable;

public interface AuthRepository {
    boolean isLoggedIn();
    boolean isOnboardingCompleted();
    void setOnboardingCompleted(boolean completed);
    void login(String email, String password, AuthCallback callback);
    void register(String email, String password, String fullName, String phone, AuthCallback callback);
    void signInWithGoogle(String idToken, AuthCallback callback);
    FirebaseUser getCurrentUser();
    Completable signOut();
    void getUserDetails(UserDataCallback callback);
    Completable syncUserData();
    Completable uploadProfileImage(Uri imageUri);
    String getProfileImageUrl();
}