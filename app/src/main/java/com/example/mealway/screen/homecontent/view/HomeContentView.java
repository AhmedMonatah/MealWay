package com.example.mealway.screen.homecontent.view;

import android.content.Context;
import com.example.mealway.data.model.Meal;
import java.util.List;

public interface HomeContentView {
    void showRandomMeal(Meal meal);
    void showHorizontalMealList(List<Meal> meals);
    void showError(String message);
    void showLoading();
    void hideLoading();
    Context getContext();
}
