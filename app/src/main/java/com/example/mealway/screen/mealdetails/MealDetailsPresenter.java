package com.example.mealway.screen.mealdetails;

import com.example.mealway.data.callback.NetworkCallback;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;

public class MealDetailsPresenter {

    private final MealDetailsView view;
    private final MealRepository repository;

    public MealDetailsPresenter(MealDetailsView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void getMealDetails(String mealId) {
        repository.getMealById(mealId, new NetworkCallback<Meal>() {
            @Override
            public void onSuccess(Meal meal) {
                view.showMealDetails(meal);
            }

            @Override
            public void onFailure(String message) {
                view.showMessage("Failed to load details: " + message);
            }
        });
    }

    public void checkFavoriteStatus(String mealId) {
        repository.isFavorite(mealId, new NetworkCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isFavorite) {
                view.showFavoriteStatus(isFavorite);
            }

            @Override
            public void onFailure(String message) {
                // Ignore error for check
            }
        });
    }

    public void addToFavorites(Meal meal) {
        repository.addToFavorites(meal, () -> {
            view.showFavoriteStatus(true);
            view.showMessage("Added to Favorites");
        }, new NetworkCallback<String>() {
            @Override
            public void onFailure(String message) {
                view.showMessage("Failed to add: " + message);
            }

            @Override
            public void onSuccess(String result) {}
        });
    }

    public void removeFromFavorites(Meal meal) {
        repository.removeFromFavorites(meal, () -> {
            view.showFavoriteStatus(false);
            view.showMessage("Removed from Favorites");
        }, new NetworkCallback<String>() {
            @Override
            public void onFailure(String message) {
                view.showMessage("Failed to remove: " + message);
            }

            @Override
            public void onSuccess(String result) {}
        });
    }
}
