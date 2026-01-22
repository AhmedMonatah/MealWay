package com.example.mealway.screen.login.presenter;

import com.example.mealway.data.repository.AuthRepository;
import com.example.mealway.screen.login.view.LoginView;

public class LoginPresenterImpl implements LoginPresenter {

    private final LoginView view;
    private final AuthRepository repository;

    public LoginPresenterImpl(LoginView view, AuthRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
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

    @Override
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
