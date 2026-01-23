package com.example.mealway.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.mealway.data.model.Meal;
import java.util.List;

@Dao
public interface MealDao {
    @Query("SELECT * FROM meals")
    List<Meal> getAllFavMeals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavMeal(Meal meal);

    @Delete
    void deleteFavMeal(Meal meal);

    @Query("SELECT EXISTS(SELECT 1 FROM meals WHERE idMeal = :id LIMIT 1)")
    boolean isMealFavorite(String id);
}
