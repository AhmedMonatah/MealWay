package com.example.mealway.screen.mealdetails;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mealway.R;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.helper.MealDetailsUiBinder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MealDetailsFragment extends Fragment implements MealDetailsView {

    private Meal meal;
    private MealDetailsPresenter presenter;
    private FloatingActionButton fabFavorite;
    private boolean isFavorite = false;

    // 1. Create a Handler tied to the main thread's Looper
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            meal = (Meal) getArguments().getSerializable("meal");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_details, container, false);

        if (meal == null) return view;

        fabFavorite = view.findViewById(R.id.fab_favorite);

        // Presenter
        MealRepository repository = new MealRepository(requireContext());
        presenter = new MealDetailsPresenter(this, repository);
        presenter.checkFavoriteStatus(meal.getIdMeal());

        if (meal.getStrInstructions() == null || meal.getStrInstructions().isEmpty() || meal.getStrYoutube() == null) {
            presenter.getMealDetails(meal.getIdMeal());
            bindData(view);
        } else {
            bindData(view);
        }

        fabFavorite.setOnClickListener(v -> {
            if (isFavorite) {
                presenter.removeFromFavorites(meal);
            } else {
                presenter.addToFavorites(meal);
            }
        });

        return view;
    }
    private void bindData(View view) {
        MealDetailsUiBinder.bind(view, meal, getContext());
    }

    @Override
    public void showMealDetails(Meal meal) {
        this.meal = meal;
        if (getView() != null) {
            bindData(getView());
        }
    }

    @Override
    public void showFavoriteStatus(boolean isFavorite) {
        this.isFavorite = isFavorite;
        if (isFavorite) {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            fabFavorite.setColorFilter(android.graphics.Color.RED);
        } else {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            fabFavorite.clearColorFilter();
        }
    }

    @Override
    public void showMessage(String message) {
        mainThreadHandler.post(() -> {
            if (getContext() != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
