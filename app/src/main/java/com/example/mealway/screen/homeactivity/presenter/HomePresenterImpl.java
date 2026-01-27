package com.example.mealway.screen.homeactivity.presenter;

import com.example.mealway.R;
import com.example.mealway.data.callback.NetworkCallback;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.homeactivity.view.HomeView;

import java.util.List;

public class HomePresenterImpl implements HomePresenter {

    private final HomeView view;
    private final MealRepository repository;

    private Meal randomMealResult;
    private List<Meal> listMealsResult;
    private int completedRequests = 0;

    public HomePresenterImpl(HomeView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getHomeData() {
        completedRequests = 0;
        randomMealResult = null;
        listMealsResult = null;

        view.showLoading();

        repository.getRandomMeal(new NetworkCallback<Meal>() {
            @Override
            public void onSuccess(Meal meal) {
                randomMealResult = meal;
                checkAllRequestsCompleted();
            }

            @Override
            public void onFailure(String message) {
                checkAllRequestsCompleted();
            }
        });

        repository.getMealsByIngredient("chicken_breast", new NetworkCallback<List<Meal>>() {
            @Override
            public void onSuccess(List<Meal> meals) {
                listMealsResult = meals;
                checkAllRequestsCompleted();
            }

            @Override
            public void onFailure(String message) {
                checkAllRequestsCompleted();
            }
        });
    }

    private synchronized void checkAllRequestsCompleted() {
        completedRequests++;
        if (completedRequests == 2) {
            view.hideLoading();
            if (randomMealResult != null) {
                view.showRandomMeal(randomMealResult);
            }
            if (listMealsResult != null) {
                view.showHorizontalMealList(listMealsResult);
            }
            
            if (randomMealResult == null && listMealsResult == null) {
                view.showError(view.getContext().getString(R.string.error_load_home));
            }
        }
    }

    @Override
    public void getRandomMeal() {
        repository.getRandomMeal(new NetworkCallback<Meal>() {
            @Override
            public void onSuccess(Meal meal) {
                view.showRandomMeal(meal);
            }

            @Override
            public void onFailure(String message) {
                view.showError(message);
            }
        });
    }

    @Override
    public void getMealsForList() {
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
