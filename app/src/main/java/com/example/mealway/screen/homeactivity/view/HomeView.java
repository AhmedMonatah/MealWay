package com.example.mealway.screen.homeactivity.view;

import com.example.mealway.data.model.Meal;
import java.util.List;

public interface HomeView {
    void showRandomMeal(Meal meal);
    void showHorizontalMealList(List<Meal> meals);
    void showError(String message);
    void showLoading();
    void hideLoading();
}
