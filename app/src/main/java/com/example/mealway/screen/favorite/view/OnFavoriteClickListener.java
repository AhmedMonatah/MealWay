package com.example.mealway.screen.favorite.view;


import com.example.mealway.data.model.Meal;

public interface OnFavoriteClickListener {
    void onMealClick(Meal meal);
    void onDeleteClick(Meal meal);
}