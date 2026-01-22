package com.example.mealway.screen.plan.presenter;

import com.example.mealway.data.model.MealAppointment;

public interface PlanPresenter {
    void getAppointments();
    void deleteAppointment(MealAppointment appointment);
    void onDestroy();
}
