package com.example.mealway.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "meal_appointments")
public class MealAppointment implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String mealId;
    private String mealName;
    private String mealThumb;
    private long dateTimestamp;

    public MealAppointment() {
    }

    public MealAppointment(@NonNull String id, String mealId, String mealName, String mealThumb, long dateTimestamp) {
        this.id = id;
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealThumb = mealThumb;
        this.dateTimestamp = dateTimestamp;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getMealId() { return mealId; }
    public void setMealId(String mealId) { this.mealId = mealId; }

    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }

    public String getMealThumb() { return mealThumb; }
    public void setMealThumb(String mealThumb) { this.mealThumb = mealThumb; }

    public long getDateTimestamp() { return dateTimestamp; }
    public void setDateTimestamp(long dateTimestamp) { this.dateTimestamp = dateTimestamp; }
}
