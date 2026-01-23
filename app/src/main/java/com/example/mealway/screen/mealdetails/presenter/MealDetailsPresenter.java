package com.example.mealway.screen.mealdetails.presenter;

import com.example.mealway.data.model.Meal;

public interface MealDetailsPresenter {
    void getMealDetails(String mealId);
    void checkFavoriteStatus(String mealId);
    void addToFavorites(Meal meal);
    void removeFromFavorites(Meal meal);
    void addAppointment(Meal meal, long timestamp);
    void onFavoriteClicked(Meal meal, boolean isFavorite);
    void onPlanClicked();
    void onVideoClicked(String youtubeUrl);
    void onDestroy();
}
