package com.example.mealway.screen.homeactivity;

import com.example.mealway.data.callback.NetworkCallback;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import java.util.List;

public class HomePresenter {

    private final HomeView view;
    private final MealRepository repository;

    public HomePresenter(HomeView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void getRandomMeal() {
        view.showLoading();
        repository.getRandomMeal(new NetworkCallback<Meal>() {
            @Override
            public void onSuccess(Meal meal) {
                view.hideLoading();
                view.showRandomMeal(meal);
            }

            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    public void getMealsForList() {
        // Fetching meals with chicken_breast ingredient for the horizontal list
        repository.getMealsByIngredient("chicken_breast", new NetworkCallback<List<Meal>>() {
            @Override
            public void onSuccess(List<Meal> meals) {
                view.showHorizontalMealList(meals);
            }

            @Override
            public void onFailure(String message) {
                view.showError(message);
            }
        });
    }
}
