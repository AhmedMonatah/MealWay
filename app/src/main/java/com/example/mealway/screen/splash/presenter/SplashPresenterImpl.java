package com.example.mealway.screen.splash.presenter;

import android.os.Handler;
import com.example.mealway.screen.splash.view.SplashView;
import com.example.mealway.data.repository.AuthRepository;

public class SplashPresenterImpl implements SplashPresenter {

    private final SplashView view;
    private final AuthRepository repository;
    private static final long SPLASH_DELAY = 3000;

    public SplashPresenterImpl(SplashView view, AuthRepository repository){
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void startSplash() {
        new Handler().postDelayed(() -> {
            if(view != null){
                if (repository.isLoggedIn()) {
                    view.navigateToHome();
                } else {
                    view.navigateToLogin();
                }
            }
        }, SPLASH_DELAY);
    }
}
