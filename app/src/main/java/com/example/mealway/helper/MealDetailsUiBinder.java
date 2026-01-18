package com.example.mealway.helper;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mealway.R;
import com.example.mealway.data.model.Meal;
import com.example.mealway.screen.mealdetails.IngredientsAdapter;
import java.util.List;

public class MealDetailsUiBinder {

    public static void bind(View view, Meal meal, Context context) {
        ImageView ivHeader = view.findViewById(R.id.iv_detail_meal);
        TextView tvArea = view.findViewById(R.id.tv_detail_area);
        TextView tvInstructions = view.findViewById(R.id.tv_instructions);
        View cardVideo = view.findViewById(R.id.card_video);
        ImageView ivVideoThumbnail = view.findViewById(R.id.iv_video_thumbnail);
        TextView tvNoVideo = view.findViewById(R.id.tv_no_video);
        RecyclerView rvIngredients = view.findViewById(R.id.rv_ingredients);

        Glide.with(context).load(meal.getStrMealThumb()).into(ivHeader);
        
        String subtitle = "";
        if (meal.getStrArea() != null) subtitle += meal.getStrArea();
        if (meal.getStrCategory() != null) subtitle += (subtitle.isEmpty() ? "" : " | ") + meal.getStrCategory();
        tvArea.setText(subtitle);

        tvInstructions.setText(meal.getStrInstructions());

        // Use VideoHelper (New method bindVideo with fallback)
        VideoHelper.bindVideo(cardVideo, ivVideoThumbnail, tvNoVideo, meal.getStrYoutube());

        // Use IngredientsHelper
        List<Pair<String, String>> ingredients = IngredientsHelper.extractIngredients(meal);
        IngredientsAdapter adapter = new IngredientsAdapter(context, ingredients);
        rvIngredients.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rvIngredients.setAdapter(adapter);
    }
}
