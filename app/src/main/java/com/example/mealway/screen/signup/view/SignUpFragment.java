package com.example.mealway.screen.signup.view;

import android.os.Bundle;
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
    private MaterialButton signUpButton, skipButton;
    private android.widget.ProgressBar progressBar;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        emailEditText = view.findViewById(R.id.emailInput);
        passwordEditText = view.findViewById(R.id.passwordInput);
        fullNameEditText = view.findViewById(R.id.fullNameInput);
        phoneEditText = view.findViewById(R.id.phoneInput);
        signUpButton = view.findViewById(R.id.signUpButton);
        skipButton = view.findViewById(R.id.skipButton);
        progressBar = view.findViewById(R.id.progressBar);

        presenter = new SignUpPresenterImpl(this, new AuthRepositoryImpl(requireContext()));

        signUpButton.setOnClickListener(v -> {
             String email = emailEditText.getText().toString().trim();
             String password = passwordEditText.getText().toString().trim();
             String fullName = fullNameEditText.getText().toString().trim();
             String phone = phoneEditText.getText().toString().trim();
             
             if (!email.isEmpty() && !password.isEmpty()) {
                 progressBar.setVisibility(View.VISIBLE);
                 signUpButton.setEnabled(false);
                 presenter.register(email, password, fullName, phone);
             }
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
            Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
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
}
