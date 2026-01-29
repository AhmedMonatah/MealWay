package com.example.mealway.screen.mealdetails.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealway.R;
import java.util.List;
import android.util.Pair;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    private Context context;
    private List<Pair<String, String>> ingredients;

    public IngredientsAdapter(Context context, List<Pair<String, String>> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Pair<String, String> item = ingredients.get(position);
        String name = item.first;
        String measure = item.second;

        holder.tvName.setText(name);
        holder.tvMeasure.setText(measure);

        String imageUrl = "https://www.themealdb.com/images/ingredients/" + name + ".png";
        Glide.with(context)
                .load(imageUrl)
                .into(holder.ivThumb);
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName;
        TextView tvMeasure;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_ingredient);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tvMeasure = itemView.findViewById(R.id.tv_ingredient_measure);
        }
    }
}
