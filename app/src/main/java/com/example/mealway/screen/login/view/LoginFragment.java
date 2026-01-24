package com.example.mealway.screen.login.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mealway.R;
import com.example.mealway.data.repository.AuthRepositoryImpl;
import com.example.mealway.screen.login.presenter.LoginPresenter;
import com.example.mealway.screen.login.presenter.LoginPresenterImpl;
import com.example.mealway.utils.GoogleSignInHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment implements LoginView {

    private LoginPresenter presenter;
    private TextInputEditText usernameEditText, passwordEditText;
    private View usernameLayout, passwordLayout;
    private MaterialButton loginButton;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = view.findViewById(R.id.usernameInput);
        passwordEditText = view.findViewById(R.id.passwordInput);
        usernameLayout = view.findViewById(R.id.usernameLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        loginButton = view.findViewById(R.id.loginButton);
        progressBar = view.findViewById(R.id.progressBar);
        TextView registerLink = view.findViewById(R.id.registerLink);
        MaterialButton skipButton = view.findViewById(R.id.skipButton);
        View googleSignInButton = view.findViewById(R.id.googleSignIn);

        presenter = new LoginPresenterImpl(this, new AuthRepositoryImpl(requireContext()));

        GoogleSignInClient googleSignInClient = GoogleSignInHelper.getGoogleSignInClient(requireContext());

        ActivityResultLauncher<android.content.Intent> googleLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                                try {
                                    GoogleSignInAccount account =
                                            GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                                    .getResult(ApiException.class);

                                    if (account != null) {
                                        presenter.loginWithGoogle(account.getIdToken());
                                    }
                                } catch (ApiException e) {
                                    Toast.makeText(getContext(),
                                            "Google Sign-In failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );

        googleSignInButton.setOnClickListener(v ->
                googleLauncher.launch(googleSignInClient.getSignInIntent())
        );

        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            // Reset backgrounds
            usernameLayout.setBackgroundResource(R.drawable.input_field_background);
            passwordLayout.setBackgroundResource(R.drawable.input_field_background);

            boolean hasError = false;

            if (TextUtils.isEmpty(email)) {
                usernameLayout.setBackgroundResource(R.drawable.input_field_error_background);
                hasError = true;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                usernameLayout.setBackgroundResource(R.drawable.input_field_error_background);
                Toast.makeText(getContext(), "Enter a valid email", Toast.LENGTH_SHORT).show();
                hasError = true;
            }

            if (TextUtils.isEmpty(pass)) {
                passwordLayout.setBackgroundResource(R.drawable.input_field_error_background);
                hasError = true;
            } else if (pass.length() < 6) {
                passwordLayout.setBackgroundResource(R.drawable.input_field_error_background);
                Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                hasError = true;
            }

            if (hasError) return;

            presenter.login(email, pass);
        });

        skipButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_login_to_home)
        );

        registerLink.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_login_to_register)
        );

        return view;
    }

    @Override
    public void showLoginSuccess() {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
        }
        if (isAdded()) {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_login_to_home);
        }
    }

    @Override
    public void showLoginError(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (loginButton != null) loginButton.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (loginButton != null) loginButton.setEnabled(true);
    }
}
