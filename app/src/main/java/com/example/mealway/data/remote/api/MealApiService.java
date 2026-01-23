package com.example.mealway.data.remote.api;

import com.example.mealway.data.model.MealResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiService {
    @GET("random.php")
    Call<MealResponse> getRandomMeal();

    @GET("filter.php")
    Call<MealResponse> getMealsByIngredient(@Query("i") String ingredient);
    
    @GET("lookup.php")
    Call<MealResponse> getMealById(@Query("i") String id);
}
