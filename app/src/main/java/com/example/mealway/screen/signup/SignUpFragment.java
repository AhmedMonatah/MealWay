package com.example.mealway.screen.signup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mealway.R;
import com.example.mealway.data.repository.AuthRepositoryImpl;
import com.example.mealway.screen.login.LoginFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpFragment extends Fragment implements SignUpPresenter.SignUpView {

    private SignUpPresenter presenter;
    private TextInputEditText emailEditText, passwordEditText, fullNameEditText, phoneEditText;
    private MaterialButton signUpButton, skipButton;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        emailEditText = view.findViewById(R.id.emailInput);
        passwordEditText = view.findViewById(R.id.passwordInput);
        fullNameEditText = view.findViewById(R.id.fullNameInput);
        phoneEditText = view.findViewById(R.id.phoneInput);
        signUpButton = view.findViewById(R.id.signUpButton);
        skipButton = view.findViewById(R.id.skipButton);
        android.widget.ProgressBar progressBar = view.findViewById(R.id.progressBar);

        presenter = new SignUpPresenter(this, new AuthRepositoryImpl(requireContext()));

        signUpButton.setOnClickListener(v -> {
             progressBar.setVisibility(View.VISIBLE);
             signUpButton.setEnabled(false);
             String email = emailEditText.getText().toString().trim();
             String password = passwordEditText.getText().toString().trim();
             presenter.register(email, password);
        });

        skipButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_signup_to_home)
        );

        return view;
    }

    @Override
    public void showSuccess() {
        if (getView() != null) {
            getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
            getView().findViewById(R.id.signUpButton).setEnabled(true);
        }
        Toast.makeText(getContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).navigate(R.id.action_signup_to_home); 
    }

    @Override
    public void showError(String message) {
        if (getView() != null) {
            getView().findViewById(R.id.progressBar).setVisibility(View.GONE);
            getView().findViewById(R.id.signUpButton).setEnabled(true);
        }
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}