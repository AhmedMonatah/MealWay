package com.example.mealway.screen.favorite.view;

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
import com.example.mealway.data.model.Meal;
import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private final Context context;
    private List<Meal> favorites = new ArrayList<>();
    private final OnFavoriteClickListener listener;



    public FavoriteAdapter(Context context, OnFavoriteClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setMeals(List<Meal> meals) {
        if (meals != null) {
            this.favorites = meals;
        } else {
            this.favorites = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Meal meal = favorites.get(position);
        holder.tvName.setText(meal.getStrMeal());
        String subtitle = (meal.getStrArea() != null ? meal.getStrArea() : "") + 
                (meal.getStrCategory() != null ? " | " + meal.getStrCategory() : "");
        holder.tvSubtitle.setText(subtitle);
        
        Glide.with(context).load(meal.getStrMealThumb()).into(holder.ivThumb);

        holder.itemView.setOnClickListener(v -> listener.onMealClick(meal));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(meal));
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName, tvSubtitle;
        ImageButton btnDelete;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_meal_thumb);
            tvName = itemView.findViewById(R.id.tv_meal_name);
            tvSubtitle = itemView.findViewById(R.id.tv_meal_subtitle);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
