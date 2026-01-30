package com.example.mealway.screen.plan.view;

import android.graphics.Color;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealway.R;
import com.example.mealway.data.model.MealAppointment;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.plan.presenter.PlanPresenter;
import com.example.mealway.screen.plan.presenter.PlanPresenterImpl;
import com.example.mealway.utils.AlertUtils;
import com.example.mealway.utils.CalendarHelper;
import com.example.mealway.utils.NetworkMonitor;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PlanFragment extends Fragment implements PlanView, PlanAdapter.OnDeleteClickListener {

    private PlanPresenter presenter;
    private PlanAdapter adapter;
    private RecyclerView rvAppointments;
    private MaterialCalendarView calendarView;
    private View layoutEmptyState;
    private ProgressBar progressBar;
    private List<MealAppointment> allAppointments = new ArrayList<>();
    private long selectedDateTimestamp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        rvAppointments = view.findViewById(R.id.rv_appointments);
        calendarView = view.findViewById(R.id.calendarView);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);

        rvAppointments.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PlanAdapter(requireContext(), this);
        rvAppointments.setAdapter(adapter);

        presenter = new PlanPresenterImpl(this, new MealRepository(requireContext()));

        setupCalendar();

        return view;
    }

    private void setupCalendar() {
        Calendar today = Calendar.getInstance();
        selectedDateTimestamp = CalendarHelper.normalizeTimestamp(today.getTimeInMillis());
        
        calendarView.setSelectedDate(today);
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            Calendar cal = date.getCalendar();
            selectedDateTimestamp = CalendarHelper.normalizeTimestamp(cal.getTimeInMillis());
            filterAppointmentsByDate();
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.getAppointments();
    }

    @Override
    public void showAppointments(List<MealAppointment> appointments) {
        this.allAppointments = appointments != null ? appointments : new ArrayList<>();
        updateDecorators();
        filterAppointmentsByDate();
    }

    private void updateDecorators() {
        calendarView.removeDecorators();
        HashSet<CalendarDay> dates = new HashSet<>();
        for (MealAppointment appt : allAppointments) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(appt.getDateTimestamp());
            dates.add(CalendarDay.from(cal));
        }
        int color = ContextCompat.getColor(requireContext(), R.color.calendar_event_color);
        calendarView.addDecorator(new EventDecorator(color, dates));
    }

    private void filterAppointmentsByDate() {
        List<MealAppointment> filtered = new ArrayList<>();
        for (MealAppointment appointment : allAppointments) {
            if (CalendarHelper.normalizeTimestamp(appointment.getDateTimestamp()) == selectedDateTimestamp) {
                filtered.add(appointment);
            }
        }

        if (filtered.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvAppointments.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvAppointments.setVisibility(View.VISIBLE);
            adapter.setAppointments(filtered);
        }
    }

    @Override
    public void showMessage(String message) {
        if (isAdded()) {
            if (message.contains("Success") || message.contains("Removed")) {
                AlertUtils.showSuccess(requireContext(), message);
            } else if (message.contains("Failed") || message.contains("Error") || message.contains("logged in")) {
                AlertUtils.showError(requireContext(), message);
            } else {
                AlertUtils.showSuccess(requireContext(), message);
            }
        }
    }

    @Override
    public void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onDeleteClick(MealAppointment appointment) {
        if (NetworkMonitor.isNetworkAvailable(requireContext())) {
            AlertUtils.showConfirmation(requireContext(), "Remove from Plan", 
                "Are you sure you want to remove " + appointment.getMealName() + " from your plan?", "Remove",
                () -> presenter.deleteAppointment(appointment));
        } else {
            showMessage("You must be logged in to sync and remove items");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    private static class EventDecorator implements DayViewDecorator {
        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }
}
