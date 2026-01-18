package com.example.mealway.data.repository;

import android.content.Context;
import com.example.mealway.data.callback.NetworkCallback;
import com.example.mealway.data.local.AppDatabase;
import com.example.mealway.data.local.MealDao;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.model.MealResponse;
import com.example.mealway.data.remote.api.MealApiService;
import com.example.mealway.data.remote.network.RetrofitClient;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealRepository {

    private final MealApiService apiService;
    private final MealDao mealDao;
    private final ExecutorService executorService;
    private static Meal cachedDailyMeal;

    public MealRepository(Context context) {
        this.apiService = RetrofitClient.getClient().create(MealApiService.class);
        AppDatabase db = AppDatabase.getInstance(context);
        this.mealDao = db.mealDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void getRandomMeal(NetworkCallback<Meal> callback) {
        if (cachedDailyMeal != null) {
            callback.onSuccess(cachedDailyMeal);
            return;
        }

        apiService.getRandomMeal().enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null && !response.body().getMeals().isEmpty()) {
                    cachedDailyMeal = response.body().getMeals().get(0);
                    callback.onSuccess(cachedDailyMeal);
                } else {
                    callback.onFailure("No meal found");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void getMealsByIngredient(String ingredient, NetworkCallback<List<Meal>> callback) {
        apiService.getMealsByIngredient(ingredient).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMeals());
                } else {
                    callback.onFailure("Failed to load meals");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void getMealById(String id, NetworkCallback<Meal> callback) {
        apiService.getMealById(id).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null && !response.body().getMeals().isEmpty()) {
                    callback.onSuccess(response.body().getMeals().get(0));
                } else {
                    callback.onFailure("Meal not found");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void addToFavorites(Meal meal, Runnable onSuccess, NetworkCallback<String> onError) {
        executorService.execute(() -> {
            try {
                mealDao.insertFavMeal(meal);
                // onSuccess is simple runnable as we don't need result
                if (onSuccess != null) onSuccess.run();
            } catch (Exception e) {
                if (onError != null) onError.onFailure(e.getMessage());
            }
        });
    }

    public void removeFromFavorites(Meal meal, Runnable onSuccess, NetworkCallback<String> onError) {
        executorService.execute(() -> {
            try {
                mealDao.deleteFavMeal(meal);
                if (onSuccess != null) onSuccess.run();
            } catch (Exception e) {
                if (onError != null) onError.onFailure(e.getMessage());
            }
        });
    }

    public void isFavorite(String mealId, NetworkCallback<Boolean> callback) {
        executorService.execute(() -> {
            boolean exists = mealDao.isMealFavorite(mealId);
            callback.onSuccess(exists);
        });
    }

    // Using NetworkCallback for list of meals from DB too, to keep it simple
    public void getAllFavorites(NetworkCallback<List<Meal>> callback) {
        executorService.execute(() -> {
            try {
                List<Meal> meals = mealDao.getAllFavMeals();
                callback.onSuccess(meals);
            } catch (Exception e) {
                callback.onFailure(e.getMessage());
            }
        });
    }
}
