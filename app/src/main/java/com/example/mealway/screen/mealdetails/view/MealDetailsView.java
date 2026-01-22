package com.example.mealway.screen.mealdetails.view;

import com.example.mealway.data.model.Meal;

public interface MealDetailsView {
    void showMealDetails(Meal meal);
    void showFavoriteStatus(boolean isFavorite);
    void showMessage(String message);
    void showLoading();
    void hideLoading();
}
