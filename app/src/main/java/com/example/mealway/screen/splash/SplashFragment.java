package com.example.mealway.screen.splash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.mealway.R;
import com.example.mealway.screen.MainActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

public class SplashFragment extends Fragment implements SplashView {

    private SplashPresenter presenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        presenter = new SplashPresenter(this, new com.example.mealway.data.repository.AuthRepositoryImpl(requireContext()));
        presenter.startSplash();

        return view;
    }

    @Override
    public void navigateToLogin() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_splash_to_login, null, new NavOptions.Builder()
                        .setPopUpTo(R.id.splashFragment, true)
                        .build());
    }

    @Override
    public void navigateToHome() {
        // Assuming there is an action_splash_to_home or similar, checking navigation.xml next
        // Ideally we should have action_splash_to_home. If not, we might need to add it or use deep link/global action.
        // For now, let's assume we might need to add it.
        // Wait, standard practice is to have an action.
        // I will verify navigation.xml in the next step to be sure, but for now I will add the method stub or try to navigate.
        // If action doesn't exist, app will crash. I better check navigation.xml first.
        // Actually, viewing navigation.xml previously showed:
        // <fragment id="splashFragment"> <action id="action_splash_to_login" ...> </fragment>
        // It does NOT have action_splash_to_home.
        // So I must add it.
        
        // I'll leave this implementation assuming the ID exists, and then ensuring I fix navigation.xml immediately.
         NavHostFragment.findNavController(this)
                .navigate(R.id.action_splash_to_home, null, new NavOptions.Builder()
                        .setPopUpTo(R.id.splashFragment, true)
                        .build());
    }
}
