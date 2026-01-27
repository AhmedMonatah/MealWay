package com.example.mealway.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalDataSource {
    private static final String PREF_NAME = "MealWayPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_ONBOARDING_COMPLETED = "OnboardingCompleted";
    private final SharedPreferences sharedPreferences;
    private final Context context;

    public LocalDataSource(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public Context getContext() {
        return context;
    }

    public void saveLoginState(boolean isLoggedIn) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearLoginState() {
        sharedPreferences.edit().remove(KEY_IS_LOGGED_IN).apply();
    }

    public void saveOnboardingState(boolean isCompleted) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, isCompleted).apply();
    }

    public boolean isOnboardingCompleted() {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false);
    }
}
