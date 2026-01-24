package com.example.mealway.screen.plan.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mealway.R;
import com.example.mealway.data.model.MealAppointment;
import com.example.mealway.utils.NetworkMonitor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private final Context context;
    private List<MealAppointment> appointments = new ArrayList<>();
    private final OnDeleteClickListener deleteClickListener;
    private final NetworkMonitor networkMonitor;

    public interface OnDeleteClickListener {
        void onDeleteClick(MealAppointment appointment);
    }

    public PlanAdapter(Context context, OnDeleteClickListener deleteClickListener) {
        this.context = context;
        this.deleteClickListener = deleteClickListener;
        this.networkMonitor = new NetworkMonitor(context);
    }

    public void setAppointments(List<MealAppointment> appointments) {
        this.appointments = appointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        MealAppointment appointment = appointments.get(position);
        holder.tvName.setText(appointment.getMealName());
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(appointment.getDateTimestamp())));

        Glide.with(context).load(appointment.getMealThumb()).into(holder.ivThumb);

        holder.btnDelete.setOnClickListener(v -> {
            deleteClickListener.onDeleteClick(appointment);
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName, tvDate;
        ImageButton btnDelete;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_meal_thumb);
            tvName = itemView.findViewById(R.id.tv_meal_name);
            tvDate = itemView.findViewById(R.id.tv_appointment_date);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
