package com.example.mealway.screen.mealdetails.presenter;

import com.example.mealway.data.callback.NetworkCallback;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.mealdetails.view.MealDetailsView;

import com.example.mealway.data.model.MealAppointment;
import com.example.mealway.utils.VideoHelper;

import com.example.mealway.utils.NetworkMonitor;
import com.google.firebase.auth.FirebaseAuth;
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
        view.showLoading();
        disposables.add(repository.addToFavorites(meal)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.hideLoading();
                            view.showFavoriteStatus(true);
                            view.showSuccess("Added to Favorites");
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showError("Failed to add to Favorites: " + throwable.getMessage());
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
                            view.showSuccess("Removed from Favorites");
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showError("Failed to remove from Favorites: " + throwable.getMessage());
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

        disposables.add(repository.addAppointment(appointment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.hideLoading();
                            view.showSuccess("Meal added to plan");
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showError("Failed to add to plan: " + throwable.getMessage());
                        }
                ));
    }

    @Override
    public void onFavoriteClicked(Meal meal, boolean isFavorite) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            view.navigateToLogin();
            return;
        }

        if (isFavorite) {
            // Need a context for NetworkMonitor, but repository is often context-aware.
            // Let's assume the view or repository handles sync.
            // For now, move the direct repo call.
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
        view.showDatePicker();
    }

    @Override
    public void onVideoClicked(String youtubeUrl) {
        String videoId = VideoHelper.extractVideoId(youtubeUrl);
        if (videoId != null) {
            view.prepareVideo(videoId);
        } else {
            view.showMessage("Video unavailable");
        }
    }



    @Override
    public void onDestroy() {
        disposables.clear();
    }
}
