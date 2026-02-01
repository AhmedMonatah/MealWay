package com.example.mealway.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mealway.data.model.Meal;
import com.example.mealway.data.model.MealAppointment;
import com.f2prateek.rx.preferences2.RxSharedPreferences;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class LocalDataSource {

    private static final String PREF_NAME = "MealWayPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_ONBOARDING_COMPLETED = "OnboardingCompleted";

    private final Context context;
    private final MealDao mealDao;
    private final RxSharedPreferences rxPrefs;

    public LocalDataSource(Context context) {
        this.context = context;
        SharedPreferences prefs =
                context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.rxPrefs = RxSharedPreferences.create(prefs);
        this.mealDao = AppDatabase.getInstance(context).mealDao();
    }

    public Context getContext() {
        return context;
    }


    public void saveLoginState(boolean isLoggedIn) {
        rxPrefs.getBoolean(KEY_IS_LOGGED_IN).set(isLoggedIn);
    }

    public boolean isLoggedIn() {
        return rxPrefs.getBoolean(KEY_IS_LOGGED_IN, false).get();
    }

    public io.reactivex.Observable<Boolean> observeLoggedIn() {
        return rxPrefs.getBoolean(KEY_IS_LOGGED_IN, false)
                .asObservable();
    }

    public void clearLoginState() {
        rxPrefs.getBoolean(KEY_IS_LOGGED_IN).delete();
    }


    public void saveOnboardingState(boolean isCompleted) {
        rxPrefs.getBoolean(KEY_ONBOARDING_COMPLETED).set(isCompleted);
    }

    public boolean isOnboardingCompleted() {
        return rxPrefs.getBoolean(KEY_ONBOARDING_COMPLETED, false).get();
    }

    public io.reactivex.Observable<Boolean> observeOnboardingCompleted() {
        return rxPrefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
                .asObservable();
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

    public Completable clearAllFavorites() {
        return mealDao.clearAllFavorites();
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

    public Completable clearAllAppointments() {
        return mealDao.clearAllAppointments();
    }
}
