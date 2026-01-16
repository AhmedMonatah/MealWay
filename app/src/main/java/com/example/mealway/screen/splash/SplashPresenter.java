package com.example.mealway.screen.splash;

import android.os.Handler;

public class SplashPresenter {

    private SplashView view;
    private static final long SPLASH_DELAY = 2000;

    public SplashPresenter(SplashView view){
        this.view = view;
    }

    public void startSplash() {
        new Handler().postDelayed(() -> {
            if(view != null){
                view.navigateToLogin();
            }
        }, SPLASH_DELAY);
    }
}