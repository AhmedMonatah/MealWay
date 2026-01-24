package com.example.mealway.screen.plan.view;

import com.example.mealway.data.model.MealAppointment;
import java.util.List;

public interface PlanView {
    void showAppointments(List<MealAppointment> appointments);
    void showMessage(String message);
    void showLoading();
    void hideLoading();
}
