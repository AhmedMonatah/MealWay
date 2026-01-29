package com.example.mealway.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;

import com.example.mealway.data.local.LocalDataSource;
import com.example.mealway.data.model.IngredientResponse;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.model.MealAppointment;
import com.example.mealway.data.model.MealResponse;
import com.example.mealway.data.model.Category;
import com.example.mealway.data.model.CategoryResponse;
import com.example.mealway.data.model.Area;
import com.example.mealway.data.model.AreaResponse;
import com.example.mealway.data.model.Ingredient;
import com.example.mealway.data.remote.api.MealApiService;
import com.example.mealway.data.remote.firebase.FirebaseManager;
import com.example.mealway.data.remote.network.RetrofitClient;
import com.example.mealway.utils.NetworkMonitor;

import java.util.Collections;
import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealRepository {
    private final MealApiService apiService;
    private final LocalDataSource localDataSource;
    private final FirebaseManager firebaseManager;
    private static Meal cachedDailyMeal;
    private final Context context;

    public MealRepository(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getClient().create(MealApiService.class);
        this.localDataSource = new LocalDataSource(context);
        this.firebaseManager = new FirebaseManager();
    }

    public boolean isOnline() {
        return NetworkMonitor.isNetworkAvailable(context);
    }

    public Single<Meal> getRandomMeal() {
        if (cachedDailyMeal != null) {
            return Single.just(cachedDailyMeal);
        }

        return apiService.getRandomMeal()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    if (response != null && response.getMeals() != null && !response.getMeals().isEmpty()) {
                        cachedDailyMeal = response.getMeals().get(0);
                        return cachedDailyMeal;
                    } else {
                        throw new RuntimeException("No meal found");
                    }
                });
    }

    public Single<List<Meal>> getMealsByIngredient(String ingredient) {
        return apiService.getMealsByIngredient(ingredient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    if (response != null && response.getMeals() != null) {
                        return response.getMeals();
                    } else {
                        return Collections.<Meal>emptyList();
                    }
                });
    }

    public Single<Meal> getMealById(String id) {
        return apiService.getMealById(id)
                .subscribeOn(Schedulers.io())
                .flatMap(response -> {
                    if (response != null && response.getMeals() != null && !response.getMeals().isEmpty()) {
                        return Single.just(response.getMeals().get(0));
                    } else {
                        return localDataSource.getFavMealById(id);
                    }
                })
                .onErrorResumeNext(throwable -> localDataSource.getFavMealById(id))
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<Meal> ensureFullMeal(Meal meal) {
        if (meal.getStrInstructions() != null && !meal.getStrInstructions().isEmpty()) {
            return Single.just(meal);
        } else {
            return apiService.getMealById(meal.getIdMeal())
                    .subscribeOn(Schedulers.io())
                    .map(response -> {
                        if (response != null && response.getMeals() != null && !response.getMeals().isEmpty()) {
                            return response.getMeals().get(0);
                        } else {
                            throw new RuntimeException("Could not fetch full meal details");
                        }
                    });
        }
    }

    public Completable addToFavorites(Meal meal) {
        return ensureFullMeal(meal)
                .flatMapCompletable(fullMeal -> {
                    fullMeal.setFavorite(true);
                    return localDataSource.insertFavMeal(fullMeal)
                            .andThen(firebaseManager.addFavorite(fullMeal));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable removeFromFavorites(Meal meal) {
        return localDataSource.deleteFavMeal(meal)
                .andThen(firebaseManager.removeFavorite(meal.getIdMeal()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Boolean> isFavorite(String mealId) {
        return localDataSource.isMealFavorite(mealId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Meal>> getStoredFavorites() {
        return localDataSource.getAllFavMeals()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable addAppointment(Meal meal, MealAppointment appointment) {
        return ensureFullMeal(meal)
                .flatMapCompletable(fullMeal -> 
                    localDataSource.isMealFavorite(fullMeal.getIdMeal())
                        .flatMapCompletable(isFav -> {
                            fullMeal.setFavorite(isFav);
                            return localDataSource.insertFavMeal(fullMeal);
                        })
                )
                .andThen(localDataSource.insertAppointment(appointment))
                .andThen(firebaseManager.addAppointment(appointment))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteAppointment(MealAppointment appointment) {
        return localDataSource.deleteAppointment(appointment)
                .andThen(firebaseManager.removeAppointment(appointment.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<MealAppointment>> getAllAppointments() {
        return localDataSource.getAllAppointments()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Category>> listCategories() {
        return apiService.listCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    if (response != null && response.getCategories() != null) {
                        return response.getCategories();
                    } else {
                        return Collections.<Category>emptyList();
                    }
                });
    }

    public Single<List<Area>> listAreas() {
        return apiService.listAreas()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    if (response != null && response.getAreas() != null) {
                        return response.getAreas();
                    } else {
                        return Collections.<Area>emptyList();
                    }
                });
    }

    public Single<List<Ingredient>> listIngredients() {
        return apiService.listIngredients()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> {
                    if (response != null && response.getIngredients() != null) {
                        return response.getIngredients();
                    } else {
                        return Collections.<Ingredient>emptyList();
                    }
                });
    }

    private <T extends MealResponse> ObservableTransformer<T, List<Meal>> mealsTransformer() {
        return upstream -> upstream
                .map(response ->
                        response.getMeals() != null
                                ? response.getMeals()
                                : Collections.<Meal>emptyList()
                )
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Meal>> searchMealsByFirstLetterObservable(String firstLetter) {
        return apiService.searchMealsByFirstLetter(firstLetter)
                .compose(mealsTransformer());
    }

    public Observable<List<Meal>> filterByCategoryObservable(String category) {
        return apiService.filterByCategory(category)
                .compose(mealsTransformer());
    }

    public Observable<List<Meal>> filterByAreaObservable(String area) {
        return apiService.filterByArea(area)
                .compose(mealsTransformer());
    }

    public Observable<List<Meal>> getMealsByIngredientObservable(String ingredient) {
        return apiService.getMealsByIngredient(ingredient)
                .toObservable()
                .compose(mealsTransformer());
    }

}
