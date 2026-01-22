package com.example.mealway.helper;

import android.util.Pair;
import com.example.mealway.data.model.Meal;
import java.util.ArrayList;
import java.util.List;

public class IngredientsHelper {

    public static List<Pair<String, String>> extractIngredients(Meal meal) {
        List<Pair<String, String>> ingredients = new ArrayList<>();
        Class<?> mealClass = meal.getClass();
        
        for (int i = 1; i <= 20; i++) {
            try {
                String ingredientFieldName = "strIngredient" + i;
                String measureFieldName = "strMeasure" + i;
                
                java.lang.reflect.Field ingredientField = mealClass.getDeclaredField(ingredientFieldName);
                java.lang.reflect.Field measureField = mealClass.getDeclaredField(measureFieldName);
                
                ingredientField.setAccessible(true);
                measureField.setAccessible(true);
                
                String ingredient = (String) ingredientField.get(meal);
                String measure = (String) measureField.get(meal);
                
                addIngredient(ingredients, ingredient, measure);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ingredients;
    }

    private static void addIngredient(List<Pair<String, String>> list, String name, String measure) {
        if (name != null && !name.trim().isEmpty()) {
            list.add(new Pair<>(name, measure != null ? measure : ""));
        }
    }
}
