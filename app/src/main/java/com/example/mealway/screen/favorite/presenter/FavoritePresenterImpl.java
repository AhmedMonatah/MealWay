package com.example.mealway.screen.favorite.presenter;

import com.example.mealway.data.callback.NetworkCallback;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.favorite.view.FavoriteView;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class FavoritePresenterImpl implements FavoritePresenter {

    private final FavoriteView view;
    private final MealRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public FavoritePresenterImpl(FavoriteView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getFavorites() {
        view.showLoading();
        disposables.add(repository.getStoredFavorites()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meals -> {
                            view.hideLoading();
                            view.showFavorites(meals);
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showMessage("Failed to load favorites: " + throwable.getMessage());
                        }
                ));
    }

    @Override
    public void removeFromFavorites(Meal meal) {
        if (!repository.isOnline()) {
            view.showMessage("You cannot remove favorites while offline.");
            return;
        }
        view.showLoading();
        disposables.add(repository.removeFromFavorites(meal)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.hideLoading();
                            view.showMessage("Removed from Favorites"); // Room Observable will auto-update view
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showMessage("Failed to remove: " + throwable.getMessage());
                        }
                ));
    }

    public void onDestroy() {
        disposables.clear();
    }
}
