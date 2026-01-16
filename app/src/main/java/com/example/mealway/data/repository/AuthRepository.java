package com.example.mealway.data.repository;

import com.example.mealway.data.callback.AuthCallback;

public interface AuthRepository {
    boolean isLoggedIn();
    void login(String email, String password, AuthCallback callback);
    void register(String email, String password, AuthCallback callback);
    void signInWithGoogle(String idToken, AuthCallback callback);
}