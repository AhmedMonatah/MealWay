package com.example.mealway.screen.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mealway.R;
import com.example.mealway.data.repository.AuthRepositoryImpl;
import com.example.mealway.screen.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment implements LoginView {

    private LoginPresenter presenter;
    private TextInputEditText usernameEditText, passwordEditText;
    private MaterialButton loginButton;
    private TextView registerLink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = view.findViewById(R.id.usernameInput);
        passwordEditText = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);
        registerLink = view.findViewById(R.id.registerLink);

        presenter = new LoginPresenter(this, new AuthRepositoryImpl());

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pass)) {
                Toast.makeText(getContext(), "Please enter username & password", Toast.LENGTH_SHORT).show();
            } else {
                presenter.login(username, pass);
            }
        });

        registerLink.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_login_to_register)
        );

        return view;
    }

    @Override
    public void showLoginSuccess() {
        Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).navigate(R.id.action_login_to_home);
    }

    @Override
    public void showLoginError(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
