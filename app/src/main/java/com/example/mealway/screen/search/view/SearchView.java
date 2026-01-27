package com.example.mealway.screen.search.view;

import android.content.Context;

import com.example.mealway.data.model.Meal;
import java.util.List;

public interface SearchView {
    void showMeals(List<Meal> meals);
    void showError(String message);
    void showLoading();
    void hideLoading();
    void showSuggestions(List<String> suggestions); // Can be used for suggestions or filter options
    void showFilterOptions(List<String> options, String title, int mode);
    Context getContext();
}
