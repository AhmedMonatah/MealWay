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

public class SplashFragment extends Fragment implements SplashView {

    private SplashPresenter presenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        presenter = new SplashPresenter(this);
        presenter.startSplash();

        return view;
    }

    @Override
    public void navigateToLogin() {
        if(getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).loadFragment(new com.example.mealway.screen.login.LoginFragment());
        }
    }
}
