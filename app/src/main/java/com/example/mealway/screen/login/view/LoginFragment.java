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

        googleSignInButton.setOnClickListener(v ->
                GoogleSignInHelper.signIn(requireActivity(), new GoogleSignInHelper.CredentialCallback() {
                    @Override
                    public void onSuccess(String idToken) {
                        presenter.loginWithGoogle(idToken);
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(getContext(), getString(R.string.sign_in_failed_prefix, error), Toast.LENGTH_SHORT).show();
                    }
                })
        );

        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            usernameLayout.setBackgroundResource(R.drawable.input_field_background);
            passwordLayout.setBackgroundResource(R.drawable.input_field_background);

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
            Toast.makeText(getContext(), getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void navigateToHome() {
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

    @Override
    public void showEmailError() {
        usernameLayout.setBackgroundResource(R.drawable.input_field_error_background);
    }

    @Override
    public void showPasswordError() {
        passwordLayout.setBackgroundResource(R.drawable.input_field_error_background);
    }
}
