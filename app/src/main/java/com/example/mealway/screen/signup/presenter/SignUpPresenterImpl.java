package com.example.mealway.screen.signup.presenter;

import com.example.mealway.data.callback.AuthCallback;
import com.example.mealway.data.repository.AuthRepository;
import com.example.mealway.screen.signup.view.SignUpView;

public class SignUpPresenterImpl implements SignUpPresenter {

    private final SignUpView view;
    private final AuthRepository repository;

    public SignUpPresenterImpl(SignUpView view, AuthRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void register(String email, String password) {
        if(email.isEmpty() || password.isEmpty()){
            view.showError("Please fill all fields");
            return;
        }

        repository.register(email, password, new AuthCallback() {
            @Override
            public void onSuccess() {
                view.showSuccess();
            }

            @Override
            public void onFailure(String message) {
                view.showError(message);
            }
        });
    }
}
