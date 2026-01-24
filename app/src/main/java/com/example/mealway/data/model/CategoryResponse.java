package com.example.mealway.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CategoryResponse {
    @SerializedName("meals")
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
