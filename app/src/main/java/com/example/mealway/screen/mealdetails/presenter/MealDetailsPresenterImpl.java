package com.example.mealway.screen.mealdetails.presenter;

import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.mealdetails.view.MealDetailsView;

import com.example.mealway.R;
import androidx.fragment.app.Fragment;
import com.example.mealway.data.model.MealAppointment;

import com.example.mealway.utils.NetworkMonitor;
import com.google.firebase.auth.FirebaseAuth;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
        disposables.add(repository.getMealById(mealId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meal -> view.showMealDetails(meal),
                        throwable -> view.showMessage("Failed to load details: " + throwable.getMessage())
                ));
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
        view.showLoading();
        disposables.add(repository.addToFavorites(meal)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.hideLoading();
                            view.showFavoriteStatus(true);
                            view.showSuccess(R.string.added_to_favorites);
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showError(String.format(view.getClass().getName().contains("Fragment") ? ((Fragment)view).getString(R.string.error_add_favorite) : "Failed to add: %s", throwable.getMessage()));
                        }
                ));
    }

    @Override
    public void removeFromFavorites(Meal meal) {
        view.showLoading();
        disposables.add(repository.removeFromFavorites(meal)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.hideLoading();
                            view.showFavoriteStatus(false);
                            view.showSuccess(R.string.removed_from_favorites);
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showError(String.format(view.getClass().getName().contains("Fragment") ? ((Fragment)view).getString(R.string.error_remove_favorite) : "Failed to remove: %s", throwable.getMessage()));
                        }
                ));
    }

    @Override
    public void addAppointment(Meal meal, long timestamp) {
        view.showLoading();
        String appointmentId = meal.getIdMeal() + "_" + timestamp;
        MealAppointment appointment = new MealAppointment(
                appointmentId, 
                meal.getIdMeal(), 
                meal.getStrMeal(), 
                meal.getStrMealThumb(), 
                timestamp
        );

        disposables.add(repository.addAppointment(meal, appointment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.hideLoading();
                            view.showSuccess(R.string.meal_added_to_plan);
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showError(String.format(view.getClass().getName().contains("Fragment") ? ((Fragment)view).getString(R.string.error_add_plan) : "Failed to add: %s", throwable.getMessage()));
                        }
                ));
    }

    @Override
    public void onFavoriteClicked(Meal meal, boolean isFavorite) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            view.navigateToLogin();
            return;
        }

        if (!repository.isOnline()) {
            view.showError("You cannot modify favorites while offline. Please check your internet connection.");
            return;
        }

        if (isFavorite) {
            removeFromFavorites(meal);
        } else {
            addToFavorites(meal);
        }
    }

    @Override
    public void onPlanClicked() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            view.navigateToLogin();
            return;
        }

        if (!repository.isOnline()) {
            view.showError("You cannot plan meals while offline. Please check your internet connection.");
            return;
        }

        view.showDatePicker();
    }



    @Override
    public void onDestroy() {
        disposables.clear();
    }
}
