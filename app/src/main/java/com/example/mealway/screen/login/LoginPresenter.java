package com.example.mealway.screen.login;

import com.example.mealway.data.repository.AuthRepository;


public class LoginPresenter {

    private LoginView view;
    private AuthRepository repository;

    public LoginPresenter(LoginView view, AuthRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void login(String email, String password) {
        repository.login(email, password, new com.example.mealway.data.callback.AuthCallback() {
            @Override
            public void onSuccess() {
                view.showLoginSuccess();
            }

            @Override
            public void onFailure(String message) {
                view.showLoginError(message);
            }
        });
    }

    public void loginWithGoogle(String idToken) {
        repository.signInWithGoogle(idToken, new com.example.mealway.data.callback.AuthCallback() {
            @Override
            public void onSuccess() {
                view.showLoginSuccess();
            }

            @Override
            public void onFailure(String message) {
                view.showLoginError(message);
            }
        });
    }
}
