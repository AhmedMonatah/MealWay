package com.example.mealway.screen.plan.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.example.mealway.utils.NetworkMonitor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class PlanFragment extends Fragment implements PlanView, PlanAdapter.OnDeleteClickListener {

    private PlanPresenter presenter;
    private PlanAdapter adapter;
    private RecyclerView rvAppointments, rvCalendar;
    private TextView tvMonthName, tvNoAppointments;
    private ProgressBar progressBar;
    private CalendarAdapter calendarAdapter;
    private Calendar currentDisplayMonth = Calendar.getInstance();
    private List<MealAppointment> allAppointments = new ArrayList<>();
    private long selectedDateTimestamp;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        rvAppointments = view.findViewById(R.id.rv_appointments);
        rvCalendar = view.findViewById(R.id.rv_calendar);
        tvNoAppointments = view.findViewById(R.id.tv_no_appointments);
        tvMonthName = view.findViewById(R.id.tv_month_name);
        progressBar = view.findViewById(R.id.progress_bar);
        
        rvAppointments.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PlanAdapter(requireContext(), this);
        rvAppointments.setAdapter(adapter);

        presenter = new PlanPresenterImpl(this, new MealRepository(requireContext()));

        // Setup Grid Calendar (7 columns)
        rvCalendar.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 7));
        
        view.findViewById(R.id.btn_prev_month).setOnClickListener(v -> {
            currentDisplayMonth.add(Calendar.MONTH, -1);
            setupCalendar();
        });

        view.findViewById(R.id.btn_next_month).setOnClickListener(v -> {
            currentDisplayMonth.add(Calendar.MONTH, 1);
            setupCalendar();
        });

        setupCalendar();

        return view;
    }

    private void setupCalendar() {
        List<Date> days = new ArrayList<>();
        Calendar cal = (Calendar) currentDisplayMonth.clone();
        
        // Month Title
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthName.setText(monthFormat.format(cal.getTime()));

        // Start from beginning of the month being displayed
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        // Add "padding" days from previous month to align with day headers
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 2 = Monday...
        cal.add(Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1));

        // Generate exactly 42 days (6 weeks) to fill the grid
        for (int i = 0; i < 42; i++) {
            days.add(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Selected date (today by default)
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        selectedDateTimestamp = today.getTimeInMillis();

        // Extract planned timestamps
        List<Long> plannedTs = new ArrayList<>();
        for (MealAppointment appt : allAppointments) {
            Calendar pCal = Calendar.getInstance();
            pCal.setTimeInMillis(appt.getDateTimestamp());
            pCal.set(Calendar.HOUR_OF_DAY, 0);
            pCal.set(Calendar.MINUTE, 0);
            pCal.set(Calendar.SECOND, 0);
            pCal.set(Calendar.MILLISECOND, 0);
            plannedTs.add(pCal.getTimeInMillis());
        }

        calendarAdapter = new CalendarAdapter(days, plannedTs, (date, pos) -> {
            Calendar selected = Calendar.getInstance();
            selected.setTime(date);
            selected.set(Calendar.HOUR_OF_DAY, 0);
            selected.set(Calendar.MINUTE, 0);
            selected.set(Calendar.SECOND, 0);
            selected.set(Calendar.MILLISECOND, 0);
            selectedDateTimestamp = selected.getTimeInMillis();
            filterAppointmentsByDate();
        });
        rvCalendar.setAdapter(calendarAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.getAppointments();
    }

    @Override
    public void showAppointments(List<MealAppointment> appointments) {
        this.allAppointments = appointments != null ? appointments : new ArrayList<>();
        setupCalendar(); // Re-setup to update markers
        filterAppointmentsByDate();
    }

    private void filterAppointmentsByDate() {
        List<MealAppointment> filtered = new ArrayList<>();
        for (MealAppointment appointment : allAppointments) {
            Calendar apptCal = Calendar.getInstance();
            apptCal.setTimeInMillis(appointment.getDateTimestamp());
            apptCal.set(Calendar.HOUR_OF_DAY, 0);
            apptCal.set(Calendar.MINUTE, 0);
            apptCal.set(Calendar.SECOND, 0);
            apptCal.set(Calendar.MILLISECOND, 0);

            if (apptCal.getTimeInMillis() == selectedDateTimestamp) {
                filtered.add(appointment);
            }
        }

        if (filtered.isEmpty()) {
            tvNoAppointments.setVisibility(View.VISIBLE);
            rvAppointments.setVisibility(View.GONE);
        } else {
            tvNoAppointments.setVisibility(View.GONE);
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
}
