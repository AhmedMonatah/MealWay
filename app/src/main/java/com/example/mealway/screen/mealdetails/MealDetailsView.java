package com.example.mealway.screen.mealdetails;

import com.example.mealway.data.model.Meal;

public interface MealDetailsView {
    void showMealDetails(Meal meal);
    void showFavoriteStatus(boolean isFavorite);
    void showMessage(String message);
}
