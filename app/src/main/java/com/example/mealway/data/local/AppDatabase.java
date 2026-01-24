package com.example.mealway.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.model.MealAppointment;

@Database(entities = {Meal.class, MealAppointment.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract MealDao mealDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "mealway_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
