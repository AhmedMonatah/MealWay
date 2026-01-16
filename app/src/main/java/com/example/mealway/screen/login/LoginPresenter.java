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
        boolean success = repository.login(email, password);
        if (success) view.showLoginSuccess();
        else view.showLoginError("Invalid credentials");
    }
}
