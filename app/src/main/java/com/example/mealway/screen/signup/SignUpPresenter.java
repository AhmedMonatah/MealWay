package com.example.mealway.screen.signup;

import com.example.mealway.data.repository.AuthRepository;
import com.example.mealway.data.callback.AuthCallback;

public class SignUpPresenter {

    private SignUpView view;
    private AuthRepository repository;

    public SignUpPresenter(SignUpView view, AuthRepository repository) {
        this.view = view;
        this.repository = repository;
    }

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

    public interface SignUpView {
        void showSuccess();
        void showError(String message);
    }
}
