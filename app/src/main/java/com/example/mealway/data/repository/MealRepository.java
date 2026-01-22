package com.example.mealway.data.repository;

import android.content.Context;

import com.example.mealway.data.callback.NetworkCallback;
import com.example.mealway.data.local.AppDatabase;
import com.example.mealway.data.local.MealDao;
import com.example.mealway.data.model.IngredientResponse;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.model.MealAppointment;
import com.example.mealway.data.model.MealResponse;
import com.example.mealway.data.model.Category;
import com.example.mealway.data.model.CategoryResponse;
import com.example.mealway.data.model.Area;
import com.example.mealway.data.model.AreaResponse;
import com.example.mealway.data.model.Ingredient;
import com.example.mealway.data.model.IngredientResponse;
import com.example.mealway.data.remote.api.MealApiService;
import com.example.mealway.data.remote.firebase.FirebaseManager;
import com.example.mealway.data.remote.network.RetrofitClient;

import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealRepository {

    private final MealApiService apiService;
    private final MealDao mealDao;
    private final FirebaseManager firebaseManager;
    private static Meal cachedDailyMeal;

    public MealRepository(Context context) {
        this.apiService = RetrofitClient.getClient().create(MealApiService.class);
        AppDatabase db = AppDatabase.getInstance(context);
        this.mealDao = db.mealDao();
        this.firebaseManager = new FirebaseManager();
    }

    // Existing Network Methods (kept as-is or could be Rx, but user didn't ask to change Retrofit yet)
    // We will wrap them if needed, but for now focusing on DB/Sync
    
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

    // Favorites Logic (Online Only for Add/Remove as per user request)
    public Completable addToFavorites(Meal meal) {
        // Sync Room + Firestore
        return mealDao.insertFavMeal(meal)
                .andThen(firebaseManager.addFavorite(meal))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable removeFromFavorites(Meal meal) {
        return mealDao.deleteFavMeal(meal)
                .andThen(firebaseManager.removeFavorite(meal.getIdMeal()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> isFavorite(String mealId) {
        return mealDao.isMealFavorite(mealId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Meal>> getStoredFavorites() {
        return mealDao.getAllFavMeals()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Appointments Logic
    public Completable addAppointment(MealAppointment appointment) {
        // Room + Firestore (Sync Firestore is optional or best-effort here, but user said "add in local and firestore")
        return mealDao.insertAppointment(appointment)
                .andThen(firebaseManager.addAppointment(appointment))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteAppointment(MealAppointment appointment) {
        return mealDao.deleteAppointment(appointment)
                .andThen(firebaseManager.removeAppointment(appointment.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<MealAppointment>> getAllAppointments() {
        return mealDao.getAllAppointments()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Legacy support for search (could be Rx too)
    public void searchMealsByFirstLetter(String firstLetter, NetworkCallback<List<Meal>> callback) {
        apiService.searchMealsByFirstLetter(firstLetter).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMeals());
                } else {
                    callback.onFailure("Failed to search meals");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void filterByCategory(String category, NetworkCallback<List<Meal>> callback) {
        apiService.filterByCategory(category).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMeals());
                } else {
                    callback.onFailure("Failed to filter by category");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void filterByArea(String area, NetworkCallback<List<Meal>> callback) {
        apiService.filterByArea(area).enqueue(new Callback<MealResponse>() {
            @Override
            public void onResponse(Call<MealResponse> call, Response<MealResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMeals());
                } else {
                    callback.onFailure("Failed to filter by area");
                }
            }

            @Override
            public void onFailure(Call<MealResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void listCategories(NetworkCallback<List<Category>> callback) {
        apiService.listCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getCategories());
                } else {
                    callback.onFailure("Failed to list categories");
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void listAreas(NetworkCallback<List<Area>> callback) {
        apiService.listAreas().enqueue(new Callback<AreaResponse>() {
            @Override
            public void onResponse(Call<AreaResponse> call, Response<AreaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getAreas());
                } else {
                    callback.onFailure("Failed to list areas");
                }
            }

            @Override
            public void onFailure(Call<AreaResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void listIngredients(NetworkCallback<List<Ingredient>> callback) {
        apiService.listIngredients().enqueue(new Callback<IngredientResponse>() {
            @Override
            public void onResponse(Call<IngredientResponse> call, Response<IngredientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getIngredients());
                } else {
                    callback.onFailure("Failed to list ingredients");
                }
            }

            @Override
            public void onFailure(Call<IngredientResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
}
