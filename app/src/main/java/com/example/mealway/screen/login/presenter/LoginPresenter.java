package com.example.mealway.screen.login.presenter;

public interface LoginPresenter {
    void login(String email, String password);
    void loginWithGoogle(String idToken);
}
