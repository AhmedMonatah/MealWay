package com.example.mealway.screen.login.view;

public interface LoginView {
    void showLoginSuccess();
    void showLoginError(String msg);
    void showLoading();
    void hideLoading();
    void showEmailError();
    void showPasswordError();
    void navigateToHome();
}
