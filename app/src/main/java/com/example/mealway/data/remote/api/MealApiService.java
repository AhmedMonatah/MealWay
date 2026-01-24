package com.example.mealway.data.remote.api;

import com.example.mealway.data.model.MealResponse;
import com.example.mealway.data.model.CategoryResponse;
import com.example.mealway.data.model.AreaResponse;
import com.example.mealway.data.model.IngredientResponse;
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

    @GET("search.php")
    Call<MealResponse> searchMealsByFirstLetter(@Query("f") String firstLetter);

    @GET("list.php?c=list")
    Call<CategoryResponse> listCategories();

    @GET("list.php?a=list")
    Call<AreaResponse> listAreas();

    @GET("list.php?i=list")
    Call<IngredientResponse> listIngredients();

    @GET("filter.php")
    Call<MealResponse> filterByCategory(@Query("c") String category);

    @GET("filter.php")
    Call<MealResponse> filterByArea(@Query("a") String area);
}
