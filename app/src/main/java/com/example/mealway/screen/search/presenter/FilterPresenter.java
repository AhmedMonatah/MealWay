package com.example.mealway.screen.search.presenter;

import java.util.List;

public interface FilterPresenter {
    void init(List<?> options, int mode);
    void search(String query);
    void selectFilter(Object item);
}
