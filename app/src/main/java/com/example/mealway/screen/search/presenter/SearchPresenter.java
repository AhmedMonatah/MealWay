package com.example.mealway.screen.search.presenter;

public interface SearchPresenter {
    void searchMeals(String query);
    void setSearchMode(int mode);
    int getSearchMode();
    void loadSuggestions();
    void loadRandomMeals();
    
    void fetchCategories();
    void fetchIngredients();
    void fetchAreas();
    
    void filterByCategory(String category);
    void filterByIngredient(String ingredient);
    void filterByArea(String area);

    void loadNextPage();
    void resetPaging();
    void clearDisposables();
}
