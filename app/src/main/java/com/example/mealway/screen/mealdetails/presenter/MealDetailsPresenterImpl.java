package com.example.mealway.screen.mealdetails.presenter;

import com.example.mealway.data.callback.NetworkCallback;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.mealdetails.view.MealDetailsView;

import com.example.mealway.data.model.MealAppointment;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class MealDetailsPresenterImpl implements MealDetailsPresenter {

    private final MealDetailsView view;
    private final MealRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public MealDetailsPresenterImpl(MealDetailsView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
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

    @Override
    public void checkFavoriteStatus(String mealId) {
        disposables.add(repository.isFavorite(mealId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        isFavorite -> view.showFavoriteStatus(isFavorite),
                        throwable -> {} // Ignore error for toggle check
                ));
    }

    @Override
    public void addToFavorites(Meal meal) {
        disposables.add(repository.addToFavorites(meal)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.showFavoriteStatus(true);
                            view.showMessage("Added to Favorites");
                        },
                        throwable -> view.showMessage("Failed to add to Favorites: " + throwable.getMessage())
                ));
    }

    @Override
    public void removeFromFavorites(Meal meal) {
        disposables.add(repository.removeFromFavorites(meal)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.showFavoriteStatus(false);
                            view.showMessage("Removed from Favorites");
                        },
                        throwable -> view.showMessage("Failed to remove from Favorites: " + throwable.getMessage())
                ));
    }

    @Override
    public void addAppointment(Meal meal, long timestamp) {
        String appointmentId = meal.getIdMeal() + "_" + timestamp;
        MealAppointment appointment = new MealAppointment(
                appointmentId, 
                meal.getIdMeal(), 
                meal.getStrMeal(), 
                meal.getStrMealThumb(), 
                timestamp
        );

        disposables.add(repository.addAppointment(appointment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> view.showMessage("Meal added to plan"),
                        throwable -> view.showMessage("Failed to add to plan: " + throwable.getMessage())
                ));
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}
