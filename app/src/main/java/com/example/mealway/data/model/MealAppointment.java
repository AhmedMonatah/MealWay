package com.example.mealway.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_appointments")
public class MealAppointment implements Parcelable {
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

    protected MealAppointment(Parcel in) {
        id = in.readString();
        mealId = in.readString();
        mealName = in.readString();
        mealThumb = in.readString();
        dateTimestamp = in.readLong();
    }

    public static final Creator<MealAppointment> CREATOR = new Creator<MealAppointment>() {
        @Override
        public MealAppointment createFromParcel(Parcel in) {
            return new MealAppointment(in);
        }

        @Override
        public MealAppointment[] newArray(int size) {
            return new MealAppointment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(mealId);
        dest.writeString(mealName);
        dest.writeString(mealThumb);
        dest.writeLong(dateTimestamp);
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
