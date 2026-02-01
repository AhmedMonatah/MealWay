package com.example.mealway.data.remote.network;

import com.example.mealway.data.model.AreaResponse;
import com.example.mealway.data.model.CategoryResponse;
import com.example.mealway.data.model.IngredientResponse;
import com.example.mealway.data.model.MealResponse;
import com.example.mealway.data.remote.api.MealApiService;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class RemoteDataSource {

    private final MealApiService apiService;

    public RemoteDataSource() {
        this.apiService = RetrofitClient.getClient().create(MealApiService.class);
    }

    public Single<MealResponse> getRandomMeal() {
        return apiService.getRandomMeal();
    }

    public Single<MealResponse> getMealsByIngredient(String ingredient) {
        return apiService.getMealsByIngredient(ingredient);
    }

    public Single<MealResponse> getMealById(String id) {
        return apiService.getMealById(id);
    }

    public Observable<MealResponse> searchMealsByFirstLetter(String firstLetter) {
        return apiService.searchMealsByFirstLetter(firstLetter);
    }

    public Single<CategoryResponse> listCategories() {
        return apiService.listCategories();
    }

    public Single<AreaResponse> listAreas() {
        return apiService.listAreas();
    }

    public Single<IngredientResponse> listIngredients() {
        return apiService.listIngredients();
    }

    public Observable<MealResponse> filterByCategory(String category) {
        return apiService.filterByCategory(category);
    }

    public Observable<MealResponse> filterByArea(String area) {
        return apiService.filterByArea(area);
    }
}
