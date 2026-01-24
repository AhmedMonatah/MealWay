package com.example.mealway.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.mealway.data.model.Meal;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import com.example.mealway.data.model.MealAppointment;

@Dao
public interface MealDao {
    @Query("SELECT * FROM meals WHERE isFavorite = 1")
    Observable<List<Meal>> getAllFavMeals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertFavMeal(Meal meal);

    @Delete
    Completable deleteFavMeal(Meal meal);

    @Query("SELECT EXISTS(SELECT 1 FROM meals WHERE idMeal = :id LIMIT 1)")
    Single<Boolean> isMealFavorite(String id);

    @Query("SELECT * FROM meals WHERE idMeal = :id LIMIT 1")
    Single<Meal> getFavMealById(String id);

    @Query("SELECT * FROM meal_appointments ORDER BY dateTimestamp ASC")
    Observable<List<MealAppointment>> getAllAppointments();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAppointment(MealAppointment appointment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAllFavMeals(List<Meal> meals);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAllAppointments(List<MealAppointment> appointments);

    @Delete
    Completable deleteAppointment(MealAppointment appointment);

    @Query("SELECT * FROM meal_appointments WHERE mealId = :mealId")
    Observable<List<MealAppointment>> getAppointmentsForMeal(String mealId);

    @Query("DELETE FROM meals")
    Completable clearAllFavorites();

    @Query("DELETE FROM meal_appointments")
    Completable clearAllAppointments();
}
