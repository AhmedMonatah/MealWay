package com.example.mealway.screen.homecontent.presenter;

import android.util.Pair;

import com.example.mealway.R;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.homecontent.view.HomeContentView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeContentPresenterImpl implements HomeContentPresenter {

    private final HomeContentView view;
    private final MealRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public HomeContentPresenterImpl(HomeContentView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getHomeData() {
        view.showLoading();

        disposables.add(
            Single.zip(
                repository.getRandomMeal(),
                repository.getMealsByIngredient("chicken_breast"),
                Pair::new
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                result -> {
                    view.hideLoading();
                    Meal randomMeal = result.first;
                    List<Meal> ingredientMeals = result.second;

                    if (randomMeal != null) {
                        view.showRandomMeal(randomMeal);
                    }
                    if (ingredientMeals != null && !ingredientMeals.isEmpty()) {
                        view.showHorizontalMealList(ingredientMeals);
                    }
                    if (randomMeal == null && (ingredientMeals == null || ingredientMeals.isEmpty())) {
                        view.showError(view.getContext().getString(R.string.error_load_home));
                    }
                },
                throwable -> {
                    view.hideLoading();
                    view.showError(view.getContext().getString(R.string.error_load_home));
                }
            )
        );
    }

    @Override
    public void getRandomMeal() {
        disposables.add(
            repository.getRandomMeal()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    meal -> view.showRandomMeal(meal),
                    throwable -> view.showError(throwable.getMessage())
                )
        );
    }

    @Override
    public void getMealsForList() {
        disposables.add(
            repository.getMealsByIngredient("chicken_breast")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    meals -> view.showHorizontalMealList(meals),
                    throwable -> view.showError(throwable.getMessage())
                )
        );
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}
