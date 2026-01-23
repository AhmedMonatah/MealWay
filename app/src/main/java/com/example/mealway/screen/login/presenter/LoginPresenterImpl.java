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
        view.showLoading();
        repository.login(email, password, new com.example.mealway.data.callback.AuthCallback() {
            @Override
            public void onSuccess() {
                view.hideLoading();
                view.showLoginSuccess();
            }

            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.showLoginError(message);
            }
        });
    }

    @Override
    public void loginWithGoogle(String idToken) {
        view.showLoading();
        repository.signInWithGoogle(idToken, new com.example.mealway.data.callback.AuthCallback() {
            @Override
            public void onSuccess() {
                view.hideLoading();
                view.showLoginSuccess();
            }

            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.showLoginError(message);
            }
        });
    }
}
