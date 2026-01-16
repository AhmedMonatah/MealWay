package com.example.mealway.screen.splash;

import android.os.Handler;

public class SplashPresenter {

    private SplashView view;
    private com.example.mealway.data.repository.AuthRepository repository;
    private static final long SPLASH_DELAY = 3000;

    public SplashPresenter(SplashView view, com.example.mealway.data.repository.AuthRepository repository){
        this.view = view;
        this.repository = repository;
    }

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