package com.example.mealway.screen.signup.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mealway.R;
import com.example.mealway.data.repository.AuthRepositoryImpl;
import com.example.mealway.screen.signup.presenter.SignUpPresenter;
import com.example.mealway.screen.signup.presenter.SignUpPresenterImpl;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpFragment extends Fragment implements SignUpView {

    private SignUpPresenter presenter;
    private TextInputEditText emailEditText, passwordEditText, fullNameEditText, phoneEditText;
    private View fullNameLayout, emailLayout, phoneLayout, passwordLayout;
    private MaterialButton signUpButton, skipButton;
    private android.widget.ProgressBar progressBar;

    public SignUpFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        emailEditText = view.findViewById(R.id.emailInput);
        passwordEditText = view.findViewById(R.id.passwordInput);
        fullNameEditText = view.findViewById(R.id.fullNameInput);
        phoneEditText = view.findViewById(R.id.phoneInput);
        fullNameLayout = view.findViewById(R.id.fullNameLayout);
        emailLayout = view.findViewById(R.id.emailLayout);
        phoneLayout = view.findViewById(R.id.phoneLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        signUpButton = view.findViewById(R.id.signUpButton);
        skipButton = view.findViewById(R.id.skipButton);
        progressBar = view.findViewById(R.id.progressBar);

        presenter = new SignUpPresenterImpl(this, new AuthRepositoryImpl(requireContext()));

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String fullName = fullNameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            // Reset backgrounds
            fullNameLayout.setBackgroundResource(R.drawable.input_field_background);
            emailLayout.setBackgroundResource(R.drawable.input_field_background);
            phoneLayout.setBackgroundResource(R.drawable.input_field_background);
            passwordLayout.setBackgroundResource(R.drawable.input_field_background);

            signUpButton.setEnabled(false);
            presenter.register(email, password, fullName, phone);
        });

        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                fullNameLayout.setBackgroundResource(R.drawable.input_field_background);
                emailLayout.setBackgroundResource(R.drawable.input_field_background);
                phoneLayout.setBackgroundResource(R.drawable.input_field_background);
                passwordLayout.setBackgroundResource(R.drawable.input_field_background);
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        fullNameEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                fullNameLayout.setBackgroundResource(R.drawable.input_field_background);
                signUpButton.setEnabled(true);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailLayout.setBackgroundResource(R.drawable.input_field_background);
                signUpButton.setEnabled(true);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneLayout.setBackgroundResource(R.drawable.input_field_background);
                signUpButton.setEnabled(true);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setBackgroundResource(R.drawable.input_field_background);
                signUpButton.setEnabled(true);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        skipButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_signup_to_home)
        );

        return view;
    }

    @Override
    public void showSuccess() {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
            signUpButton.setEnabled(true);
            if (getContext() != null) {
                Toast.makeText(getContext(), getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
            }
            NavHostFragment.findNavController(this).navigate(R.id.action_signup_to_home);
        }
    }

    @Override
    public void showError(String message) {
        if (isAdded()) {
            progressBar.setVisibility(View.GONE);
            signUpButton.setEnabled(true);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showEmailError(String message) {
        emailLayout.setBackgroundResource(R.drawable.input_field_error_background);
        if (message != null && !message.isEmpty()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showPasswordError(String message) {
        passwordLayout.setBackgroundResource(R.drawable.input_field_error_background);
        if (message != null && !message.isEmpty()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showNameError() {
        fullNameLayout.setBackgroundResource(R.drawable.input_field_error_background);
    }

    @Override
    public void showPhoneError() {
        phoneLayout.setBackgroundResource(R.drawable.input_field_error_background);
    }
}
