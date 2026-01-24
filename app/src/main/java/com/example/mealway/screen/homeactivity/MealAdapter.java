package com.example.mealway.screen.homeactivity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.mealway.R;
import com.example.mealway.data.model.Meal;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> meals;
    private Context context;
    private OnMealClickListener listener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public MealAdapter(Context context, List<Meal> meals, OnMealClickListener listener) {
        this.context = context;
        this.meals = meals;
        this.listener = listener;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal_horizontal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.tvName.setText(meal.getStrMeal());

        // Show Shimmer, Hide Image
        holder.shimmerLayout.startShimmer();
        holder.shimmerLayout.setVisibility(View.VISIBLE);
        holder.ivThumb.setVisibility(View.INVISIBLE);

        Glide.with(context)
                .load(meal.getStrMealThumb())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.shimmerLayout.stopShimmer();
                        holder.shimmerLayout.setVisibility(View.GONE);
                        holder.ivThumb.setVisibility(View.VISIBLE);
                        holder.ivThumb.setImageResource(R.drawable.error404); // Default error image
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.shimmerLayout.stopShimmer();
                        holder.shimmerLayout.setVisibility(View.GONE);
                        holder.ivThumb.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.ivThumb);

        holder.itemView.setOnClickListener(v -> listener.onMealClick(meal));
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName;
        ShimmerFrameLayout shimmerLayout;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_meal_thumb);
            tvName = itemView.findViewById(R.id.tv_meal_name);
            shimmerLayout = itemView.findViewById(R.id.shimmer_view_container);
        }
    }
}
