package com.example.mealway.screen.splash.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mealway.R;
import com.example.mealway.data.repository.AuthRepositoryImpl;
import com.example.mealway.screen.splash.presenter.SplashPresenter;
import com.example.mealway.screen.splash.presenter.SplashPresenterImpl;

public class SplashFragment extends Fragment implements SplashView {

    private SplashPresenter presenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new SplashPresenterImpl(this, new AuthRepositoryImpl(requireContext()));
        presenter.startSplash();
    }

    @Override
    public void navigateToLogin() {
        if (isAdded()) {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_splash_to_login, null, new NavOptions.Builder()
                            .setPopUpTo(R.id.splashFragment, true)
                            .build());
        }
    }

    @Override
    public void navigateToHome() {
        if (isAdded()) {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_splash_to_home, null, new NavOptions.Builder()
                            .setPopUpTo(R.id.splashFragment, true)
                            .build());
        }
    }
}
