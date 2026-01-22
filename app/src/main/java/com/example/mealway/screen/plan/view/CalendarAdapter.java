package com.example.mealway.screen.plan.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mealway.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final List<Date> days;
    private final List<Long> plannedTimestamps;
    private int selectedPosition = -1;
    private final OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(Date date, int position);
    }

    public CalendarAdapter(List<Date> days, List<Long> plannedTimestamps, OnDateClickListener listener) {
        this.days = days;
        this.plannedTimestamps = plannedTimestamps;
        this.listener = listener;
        
        // Default select today
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        long todayTs = today.getTimeInMillis();

        for (int i = 0; i < days.size(); i++) {
            Calendar day = Calendar.getInstance();
            day.setTime(days.get(i));
            day.set(Calendar.HOUR_OF_DAY, 0);
            day.set(Calendar.MINUTE, 0);
            day.set(Calendar.SECOND, 0);
            day.set(Calendar.MILLISECOND, 0);
            if (day.getTimeInMillis() == todayTs) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        Date date = days.get(position);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        holder.tvDayNumber.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

        // Marker logic
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long currentDayTs = cal.getTimeInMillis();

        boolean isPlanned = false;
        for (Long ts : plannedTimestamps) {
            if (ts == currentDayTs) {
                isPlanned = true;
                break;
            }
        }
        // Marker/Shadow logic (Show even if not selected)
        if (isPlanned) {
            holder.itemView.setBackgroundResource(R.drawable.bg_day_highlight);
            holder.viewMarker.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setBackground(null);
            holder.viewMarker.setVisibility(View.INVISIBLE);
        }

        // Selection logic
        if (position == selectedPosition) {
            holder.tvDayNumber.setTextColor(Color.WHITE);
            holder.tvDayNumber.setBackgroundResource(R.drawable.bg_circle_main);
        } else {
            // Check if it's today (but not selected) to show a light highlight? 
            // Better to keep it clean.
            holder.tvDayNumber.setTextColor(Color.BLACK);
            holder.tvDayNumber.setBackground(null);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAbsoluteAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
            listener.onDateClick(date, selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayNumber;
        View viewMarker;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayNumber = itemView.findViewById(R.id.tv_day_number);
            viewMarker = itemView.findViewById(R.id.view_marker);
        }
    }
}
