package com.example.mealway.screen.search.presenter;

public interface SearchPresenter {
    void searchMeals(String query);
    void setSearchMode(int mode); // 0: Name, 1: Category, 2: Ingredient, 3: Country
    int getSearchMode();
    void loadSuggestions(); // Used for random/initial load
    void loadRandomMeals();
    
    // New methods for explicit filter selection
    void fetchCategories();
    void fetchIngredients();
    void fetchAreas();
    
    void filterByCategory(String category);
    void filterByIngredient(String ingredient);
    void filterByArea(String area);
}
