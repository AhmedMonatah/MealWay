package com.example.mealway.screen.favorite.presenter;

import com.example.mealway.data.model.Meal;

public interface FavoritePresenter {
    void getFavorites();
    void removeFromFavorites(Meal meal);

    void onDestroy();
}
