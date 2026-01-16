package com.example.mealway.screen.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment implements LoginView {

    private LoginPresenter presenter;
    private TextInputEditText usernameEditText, passwordEditText;
    private MaterialButton loginButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = view.findViewById(R.id.usernameInput);
        passwordEditText = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);
        TextView registerLink = view.findViewById(R.id.registerLink);
        MaterialButton skipButton = view.findViewById(R.id.skipButton);
        View googleSignInButton = view.findViewById(R.id.googleSignIn);

        presenter = new LoginPresenter(this, new AuthRepositoryImpl(requireContext()));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient =
                GoogleSignIn.getClient(requireActivity(), gso);

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
            String user = usernameEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                Toast.makeText(getContext(),
                        "Please enter username & password",
                        Toast.LENGTH_SHORT).show();
            } else {
                presenter.login(user, pass);
            }
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
        Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_login_to_home);
    }

    @Override
    public void showLoginError(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
