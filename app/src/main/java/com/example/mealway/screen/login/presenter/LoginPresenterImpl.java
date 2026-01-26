package com.example.mealway.screen.login.presenter;

import com.example.mealway.data.callback.AuthCallback;
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
        if (email.isEmpty()) {
            view.showEmailError();
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showLoginError("Enter a valid email"); 
            view.showEmailError(); 
            return;
        }

        if (password.isEmpty()) {
            view.showPasswordError();
            return;
        } else if (password.length() < 6) {
            view.showLoginError("Password must be at least 6 characters");
            view.showPasswordError();
            return;
        }

        view.showLoading();
        repository.login(email, password, new AuthCallback() {
            @Override
            public void onSuccess() {
                view.hideLoading();
                view.showLoginSuccess();
                view.navigateToHome();
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
        repository.signInWithGoogle(idToken, new AuthCallback() {
            @Override
            public void onSuccess() {
                view.hideLoading();
                view.showLoginSuccess();
                view.navigateToHome();
            }

            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.showLoginError(message);
            }
        });
    }
}
