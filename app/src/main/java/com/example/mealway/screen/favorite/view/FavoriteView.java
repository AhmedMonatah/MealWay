package com.example.mealway.screen.favorite.view;

import com.example.mealway.data.model.Meal;
import java.util.List;

public interface FavoriteView {
    void showFavorites(List<Meal> meals);
    void showMessage(String message);
    void showLoading();
    void hideLoading();
}
