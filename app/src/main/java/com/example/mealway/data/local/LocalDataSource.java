package com.example.mealway.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalDataSource {
    private static final String PREF_NAME = "MealWayPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private final SharedPreferences sharedPreferences;

    public LocalDataSource(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
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
}
