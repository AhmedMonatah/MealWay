package com.example.mealway.data.remote.api;

import com.example.mealway.data.model.MealResponse;
import com.example.mealway.data.model.CategoryResponse;
import com.example.mealway.data.model.AreaResponse;
import com.example.mealway.data.model.IngredientResponse;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealApiService {
    @GET("random.php")
    Single<MealResponse> getRandomMeal();

    @GET("filter.php")
    Single<MealResponse> getMealsByIngredient(@Query("i") String ingredient);
    
    @GET("lookup.php")
    Single<MealResponse> getMealById(@Query("i") String id);

    @GET("search.php")
    Observable<MealResponse> searchMealsByFirstLetter(@Query("f") String firstLetter);

    @GET("list.php?c=list")
    Single<CategoryResponse> listCategories();

    @GET("list.php?a=list")
    Single<AreaResponse> listAreas();

    @GET("list.php?i=list")
    Single<IngredientResponse> listIngredients();

    @GET("filter.php")
    Observable<MealResponse> filterByCategory(@Query("c") String category);

    @GET("filter.php")
    Observable<MealResponse> filterByArea(@Query("a") String area);
}
