package com.example.mealway.screen.search.presenter;

import com.example.mealway.data.model.Area;
import com.example.mealway.data.model.Category;
import com.example.mealway.data.model.Ingredient;
import com.example.mealway.screen.search.view.FilterView;

import java.util.ArrayList;
import java.util.List;

public class FilterPresenterImpl implements FilterPresenter {

    private final FilterView view;
    private List<?> fullList;
    private int mode;

    public FilterPresenterImpl(FilterView view) {
        this.view = view;
    }

    @Override
    public void init(List<?> options, int mode) {
        this.fullList = options != null ? options : new ArrayList<>();
        this.mode = mode;
        view.showFilters(fullList);
    }

    @Override
    public void search(String query) {
        if (query == null || query.isEmpty()) {
            view.showFilters(fullList);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<Object> filteredList = new ArrayList<>();

        for (Object item : fullList) {
            String name = "";
            if (item instanceof Category) name = ((Category) item).getStrCategory();
            else if (item instanceof Ingredient) name = ((Ingredient) item).getStrIngredient();
            else if (item instanceof Area) name = ((Area) item).getStrArea();

            if (name != null && name.toLowerCase().contains(lowerQuery)) {
                filteredList.add(item);
            }
        }
        view.showFilters(filteredList);
    }

    @Override
    public void selectFilter(Object item) {
        view.closeFilter(item, mode);
    }
}
