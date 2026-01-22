package com.example.mealway.screen.search.presenter;

import com.example.mealway.data.callback.NetworkCallback;
import com.example.mealway.data.model.Category;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.search.view.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SearchPresenterImpl implements SearchPresenter {

    private final SearchView view;
    private final MealRepository repository;
    private int currentMode = 0; // 0: Name, 1: Category, 2: Ingredient, 3: Country

    public SearchPresenterImpl(SearchView view, MealRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void setSearchMode(int mode) {
        this.currentMode = mode;
    }

    @Override
    public int getSearchMode() {
        return currentMode;
    }

    @Override
    public void loadSuggestions() {
        // Not used much in Redesign v2, but keeping for compatibility
    }

    @Override
    public void fetchCategories() {
        view.showLoading();
        repository.listCategories(new NetworkCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                view.hideLoading();
                List<String> categories = new ArrayList<>();
                if (result != null) {
                    for (Category c : result) categories.add(c.getStrCategory());
                }
                view.showFilterOptions(categories, "Select Category", 1);
            }
            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void fetchIngredients() {
        view.showLoading();
        repository.listIngredients(new NetworkCallback<List<com.example.mealway.data.model.Ingredient>>() {
            @Override
            public void onSuccess(List<com.example.mealway.data.model.Ingredient> result) {
                view.hideLoading();
                List<String> ingredients = new ArrayList<>();
                if (result != null) {
                    // Show top list or searchable
                    for (int i = 0; i < Math.min(result.size(), 100); i++) {
                        ingredients.add(result.get(i).getStrIngredient());
                    }
                }
                view.showFilterOptions(ingredients, "Select Ingredient", 2);
            }
            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
    }

    @Override
    public void fetchAreas() {
        view.showLoading();
        repository.listAreas(new NetworkCallback<List<com.example.mealway.data.model.Area>>() {
            @Override
            public void onSuccess(List<com.example.mealway.data.model.Area> result) {
                view.hideLoading();
                List<String> areas = new ArrayList<>();
                if (result != null) {
                    for (com.example.mealway.data.model.Area a : result) areas.add(a.getStrArea());
                }
                view.showFilterOptions(areas, "Select Country", 3);
            }
            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.showError(message);
            }
        });
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
        view.showLoading();
        repository.listCategories(new NetworkCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                if (result != null && !result.isEmpty()) {
                    Random random = new Random();
                    Category randomCategory = result.get(random.nextInt(result.size()));
                    repository.filterByCategory(randomCategory.getStrCategory(), new NetworkCallback<List<Meal>>() {
                        @Override
                        public void onSuccess(List<Meal> meals) {
                            view.hideLoading();
                            view.showMeals(meals);
                        }
                        @Override
                        public void onFailure(String errorMsg) {
                            view.hideLoading();
                            view.showError("Failed to load random meals");
                        }
                    });
                } else {
                    view.hideLoading();
                }
            }
            @Override
            public void onFailure(String message) {
                view.hideLoading();
            }
        });
    }

    @Override
    public void searchMeals(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }

        view.showLoading();

        NetworkCallback<List<Meal>> callback = new NetworkCallback<List<Meal>>() {
            @Override
            public void onSuccess(List<Meal> result) {
                view.hideLoading();
                if (result != null) {
                    view.showMeals(result);
                } else {
                    view.showMeals(Collections.emptyList());
                }
            }
            @Override
            public void onFailure(String errorMsg) {
                view.hideLoading();
                view.showError(errorMsg);
            }
        };

        switch (currentMode) {
            case 1:
                repository.filterByCategory(query, callback);
                break;
            case 2:
                repository.getMealsByIngredient(query, callback);
                break;
            case 3:
                repository.filterByArea(query, callback);
                break;
            case 0:
            default:
                if (query.length() > 0) {
                    repository.searchMealsByFirstLetter(query.substring(0, 1), callback);
                }
                break;
        }
    }
}
