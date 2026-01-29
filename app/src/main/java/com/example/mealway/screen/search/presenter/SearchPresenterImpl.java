package com.example.mealway.screen.search.presenter;

import com.example.mealway.R;
import com.example.mealway.data.model.Area;
import com.example.mealway.data.model.Category;
import com.example.mealway.data.model.Ingredient;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.search.view.SearchView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenterImpl implements SearchPresenter {

    private final SearchView view;
    private final MealRepository repository;
    private int currentMode = 0;
    private final CompositeDisposable disposables = new CompositeDisposable();
    
    // Paging state
    private char currentLetter = 'a';
    private boolean isAllLoaded = false;
    private boolean isLoading = false;
    private final List<Meal> cachedMeals = new ArrayList<>();

    public SearchPresenterImpl(SearchView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void setSearchMode(int mode) {
        this.currentMode = mode;
        resetPaging();
    }

    @Override
    public int getSearchMode() {
        return currentMode;
    }

    @Override
    public void loadSuggestions() {}

    @Override
    public void fetchCategories() {
        view.showLoading();
        disposables.add(
            repository.listCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    result -> {
                        view.hideLoading();
                        view.showFilterOptions(result, view.getSafeString(R.string.title_select_category), 1);
                    },
                    throwable -> {
                        view.hideLoading();
                        view.showError(throwable.getMessage());
                    }
                )
        );
    }

    @Override
    public void fetchIngredients() {
        view.showLoading();
        disposables.add(
            repository.listIngredients()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    result -> {
                        view.hideLoading();
                        view.showFilterOptions(result, view.getSafeString(R.string.title_select_ingredient), 2);
                    },
                    throwable -> {
                        view.hideLoading();
                        view.showError(throwable.getMessage());
                    }
                )
        );
    }

    @Override
    public void fetchAreas() {
        view.showLoading();
        disposables.add(
            repository.listAreas()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    result -> {
                        view.hideLoading();
                        view.showFilterOptions(result, view.getSafeString(R.string.title_select_country), 3);
                    },
                    throwable -> {
                        view.hideLoading();
                        view.showError(throwable.getMessage());
                    }
                )
        );
    }

    @Override
    public void filterByCategory(String category) {
        setSearchMode(1);
        searchMeals(category);
    }

    @Override
    public void filterByIngredient(String ingredient) {
        setSearchMode(2);
        searchMeals(ingredient);
    }

    @Override
    public void filterByArea(String area) {
        setSearchMode(3);
        searchMeals(area);
    }

    @Override
    public void loadRandomMeals() {
        if (!cachedMeals.isEmpty()) {
            view.showMeals(cachedMeals);
            return;
        }
        loadNextPage(); 
    }

    @Override
    public void loadNextPage() {
        if (isLoading || isAllLoaded || currentMode != 0) return;

        isLoading = true;
        view.showLoading();

        disposables.add(
            repository.searchMealsByFirstLetterObservable(String.valueOf(currentLetter))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    meals -> {
                        view.hideLoading();
                        isLoading = false;
                        if (meals != null && !meals.isEmpty()) {
                            cachedMeals.addAll(meals);
                            view.showMeals(new ArrayList<>(cachedMeals));
                        }
                        
                        // Increment letter for next page
                        if (currentLetter < 'z') {
                            currentLetter++;
                        } else {
                            isAllLoaded = true;
                        }
                        
                        // If current letter returned nothing, automatically try next until we find data or reach 'z'
                        if (meals == null || meals.isEmpty()) {
                            loadNextPage();
                        }
                    },
                    throwable -> {
                        view.hideLoading();
                        isLoading = false;
                        view.showError(view.getSafeString(R.string.error_loading_meals_prefix) + ": " + throwable.getMessage());
                    }
                )
        );
    }

    @Override
    public void resetPaging() {
        currentLetter = 'a';
        isAllLoaded = false;
        isLoading = false;
        cachedMeals.clear();
    }

    @Override
    public void clearDisposables() {
        disposables.clear();
    }

    @Override
    public void searchMeals(String query) {
        if (query == null || query.trim().isEmpty()) {
            resetPaging();
            loadNextPage();
            return;
        }

        view.showLoading();
        disposables.clear(); // Cancel previous searches/paging

        io.reactivex.rxjava3.core.Observable<List<Meal>> observable;
        switch (currentMode) {
            case 1: observable = repository.filterByCategoryObservable(query); break;
            case 2: observable = repository.getMealsByIngredientObservable(query); break;
            case 3: observable = repository.filterByAreaObservable(query); break;
            case 0:
            default:
                observable = repository.searchMealsByFirstLetterObservable(query.substring(0, 1));
                break;
        }

        disposables.add(
            observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    result -> {
                        view.hideLoading();
                        cachedMeals.clear();
                        if (result != null) {
                            cachedMeals.addAll(result);
                        }
                        view.showMeals(new ArrayList<>(cachedMeals));
                    },
                    throwable -> {
                        view.hideLoading();
                        view.showError(throwable.getMessage());
                    }
                )
        );
    }
}
