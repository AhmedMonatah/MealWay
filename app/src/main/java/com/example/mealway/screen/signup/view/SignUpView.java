package com.example.mealway.screen.signup.view;

public interface SignUpView {
    void showSuccess();
    void showError(String message);
    void showEmailError(String message);
    void showPasswordError(String message);
    void showNameError();
    void showPhoneError();
    void showLoading();
    void hideLoading();
}
