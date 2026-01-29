package com.example.mealway.screen.search.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealway.R;
import com.example.mealway.data.model.Area;
import com.example.mealway.data.model.Category;
import com.example.mealway.data.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class FilterCardAdapter extends RecyclerView.Adapter<FilterCardAdapter.FilterViewHolder> {

    private List<?> fullList;
    private List<Object> filteredList;
    private final OnFilterItemClickListener listener;

    public interface OnFilterItemClickListener {
        void onFilterItemClick(Object item);
    }

    public FilterCardAdapter(List<?> items, OnFilterItemClickListener listener) {
        this.fullList = items != null ? items : new ArrayList<>();
        this.filteredList = new ArrayList<>(fullList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_card, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        Object item = filteredList.get(position);
        String name = "";
        String imageUrl = "";

        if (item instanceof Category) {
            Category cat = (Category) item;
            name = cat.getStrCategory();
            imageUrl = cat.getStrCategoryThumb();
            if (imageUrl == null || imageUrl.isEmpty()) {
                imageUrl = "https://www.themealdb.com/images/category/" + name.toLowerCase() + ".png";
            }
        } else if (item instanceof Ingredient) {
            Ingredient ing = (Ingredient) item;
            name = ing.getStrIngredient();
            imageUrl = "https://www.themealdb.com/images/ingredients/" + name + ".png";
        } else if (item instanceof Area) {
            Area area = (Area) item;
            name = area.getStrArea();
            imageUrl = area.getFlagUrl();
        }

        String displayName = capitalize(name);
        holder.tvName.setText(displayName);
        
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(holder.ivBackground);

        holder.tvName.setTextColor(holder.itemView.getContext().getColor(R.color.white));

        holder.itemView.setOnClickListener(v -> listener.onFilterItemClick(item));
    }

    public void setFilters(List<Object> items) {
        this.filteredList = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    static class FilterViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivBackground;
        com.google.android.material.card.MaterialCardView cardRoot;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_filter_card_name);
            ivBackground = itemView.findViewById(R.id.iv_filter_card_bg);
            cardRoot = itemView.findViewById(R.id.card_filter_root);
        }
    }
}
