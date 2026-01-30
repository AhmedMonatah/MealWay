package com.example.mealway.screen.plan.presenter;

import com.example.mealway.data.model.MealAppointment;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.plan.view.PlanView;
import com.example.mealway.utils.CalendarHelper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class PlanPresenterImpl implements PlanPresenter {
    private final PlanView view;
    private final MealRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public PlanPresenterImpl(PlanView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void getAppointments() {
        view.showLoading();
        disposables.add(repository.getAllAppointments()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        appointments -> {
                            view.hideLoading();
                            view.showAppointments(appointments);
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showMessage("Failed to load plan: " + throwable.getMessage());
                        }
                ));
    }

    @Override
    public void deleteAppointment(MealAppointment appointment) {
        if (!repository.isOnline()) {
            view.showMessage("You cannot remove appointments while offline.");
            return;
        }
        view.showLoading();
        disposables.add(repository.deleteAppointment(appointment)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            view.hideLoading();
                            view.showMessage("Removed from plan");
                            if (view.getContext() != null) {
                                CalendarHelper.deleteFromCalendar(
                                    view.getContext(), 
                                    "Meal: " + appointment.getMealName(), 
                                    appointment.getDateTimestamp()
                                );
                            }
                        },
                        throwable -> {
                            view.hideLoading();
                            view.showMessage("Failed to remove: " + throwable.getMessage());
                        }
                ));
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}
