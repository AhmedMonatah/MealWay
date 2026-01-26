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
    public void register(String email, String password, String fullName, String phone) {
        boolean hasError = false;

        if (fullName.isEmpty()) {
            view.showNameError();
            hasError = true;
        }

        if (email.isEmpty()) {
            view.showEmailError("Email is required");
            hasError = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showEmailError("Enter a valid email");
            hasError = true;
        }

        if (phone.isEmpty()) {
            view.showPhoneError();
            hasError = true;
        }

        if (password.isEmpty()) {
            view.showPasswordError("Password is required");
            hasError = true;
        } else if (password.length() < 6) {
            view.showPasswordError("Password must be at least 6 characters");
            hasError = true;
        }

        if (hasError) return;

        repository.register(email, password, fullName, phone, new AuthCallback() {
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
