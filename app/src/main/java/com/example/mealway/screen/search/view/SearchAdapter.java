package com.example.mealway.screen.search.view;

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

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<Meal> meals;
    private final List<Meal> originalMeals;
    private final Context context;
    private final SearchClickListener listener;

    public SearchAdapter(Context context, SearchClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.meals = new ArrayList<>();
        this.originalMeals = new ArrayList<>();
    }

    public void setMeals(List<Meal> meals) {
        this.meals.clear();
        this.originalMeals.clear();
        if (meals != null) {
            this.meals.addAll(meals);
            this.originalMeals.addAll(meals);
        }
        notifyDataSetChanged();
    }

    public void addMeals(List<Meal> newMeals) {
        if (newMeals != null && !newMeals.isEmpty()) {
            this.meals.addAll(newMeals);
            this.originalMeals.addAll(newMeals);
            notifyDataSetChanged();
        }
    }



    public void filterLocally(String query) {
        if (query == null || query.isEmpty()) {
            this.meals = new ArrayList<>(originalMeals);
        } else {
            List<Meal> filtered = new ArrayList<>();
            for (Meal meal : originalMeals) {
                if (meal.getStrMeal().toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(meal);
                }
            }
            this.meals = filtered;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal_vertical, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.tvName.setText(meal.getStrMeal());
        
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
                        holder.ivThumb.setImageResource(R.drawable.error404);
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMealClick(meal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName;
        ShimmerFrameLayout shimmerLayout;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_meal_thumb);
            tvName = itemView.findViewById(R.id.tv_meal_name);
            shimmerLayout = itemView.findViewById(R.id.shimmer_view_container);
        }
    }
}
