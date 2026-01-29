package com.example.mealway.screen.search.view;

import java.util.List;

public interface FilterView {
    void showFilters(List<?> filters);
    void closeFilter(Object selected, int mode);
}
