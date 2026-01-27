package com.example.mealway.data.repository;

import com.example.mealway.data.callback.AuthCallback;
import com.google.firebase.auth.FirebaseUser;
import android.net.Uri;

public interface AuthRepository {
    boolean isLoggedIn();
    boolean isOnboardingCompleted();
    void setOnboardingCompleted(boolean completed);
    void login(String email, String password, AuthCallback callback);
    void register(String email, String password, String fullName, String phone, AuthCallback callback);
    void signInWithGoogle(String idToken, AuthCallback callback);
    FirebaseUser getCurrentUser();
    void signOut();
    void getUserDetails(UserDataCallback callback);
    void syncUserData(AuthCallback callback);


}