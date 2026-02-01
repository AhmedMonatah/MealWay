package com.example.mealway.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mealway.data.local.AppDatabase;
import com.example.mealway.data.local.MealDao;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.model.MealAppointment;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class LocalDataSource {
    private static final String PREF_NAME = "MealWayPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_ONBOARDING_COMPLETED = "OnboardingCompleted";
    private final SharedPreferences sharedPreferences;
    private final Context context;
    private final MealDao mealDao;

    public LocalDataSource(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mealDao = AppDatabase.getInstance(context).mealDao();
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

    public Observable<Boolean> observeLoggedIn() {
        return createBooleanObservable(KEY_IS_LOGGED_IN, false);
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

    public Observable<Boolean> observeOnboardingCompleted() {
        return createBooleanObservable(KEY_ONBOARDING_COMPLETED, false);
    }

    private Observable<Boolean> createBooleanObservable(String key, boolean defaultValue) {
        return Observable.create(emitter -> {
            SharedPreferences.OnSharedPreferenceChangeListener listener = (prefs, k) -> {
                if (key.equals(k)) {
                    emitter.onNext(prefs.getBoolean(key, defaultValue));
                }
            };
            emitter.onNext(sharedPreferences.getBoolean(key, defaultValue));
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
            emitter.setCancellable(() -> sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener));
        });
    }

    public Observable<List<Meal>> getAllFavMeals() {
        return mealDao.getAllFavMeals();
    }

    public Completable insertFavMeal(Meal meal) {
        return mealDao.insertFavMeal(meal);
    }

    public Completable deleteFavMeal(Meal meal) {
        return mealDao.deleteFavMeal(meal);
    }

    public Single<Boolean> isMealFavorite(String id) {
        return mealDao.isMealFavorite(id);
    }

    public Single<Meal> getFavMealById(String id) {
        return mealDao.getFavMealById(id);
    }

    public Observable<List<MealAppointment>> getAllAppointments() {
        return mealDao.getAllAppointments();
    }

    public Completable insertAppointment(MealAppointment appointment) {
        return mealDao.insertAppointment(appointment);
    }

    public Completable insertAllFavMeals(List<Meal> meals) {
        return mealDao.insertAllFavMeals(meals);
    }

    public Completable insertAllAppointments(List<MealAppointment> appointments) {
        return mealDao.insertAllAppointments(appointments);
    }

    public Completable deleteAppointment(MealAppointment appointment) {
        return mealDao.deleteAppointment(appointment);
    }

    public Completable clearAllFavorites() {
        return mealDao.clearAllFavorites();
    }

    public Completable clearAllAppointments() {
        return mealDao.clearAllAppointments();
    }
}
