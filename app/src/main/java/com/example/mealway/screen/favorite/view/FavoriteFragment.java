package com.example.mealway.screen.favorite.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealway.R;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.favorite.presenter.FavoritePresenter;
import com.example.mealway.screen.favorite.presenter.FavoritePresenterImpl;
import com.example.mealway.screen.favorite.view.FavoriteAdapter;
import com.example.mealway.screen.mealdetails.view.MealDetailsFragment;
import com.example.mealway.utils.NetworkMonitor;
import com.example.mealway.utils.AlertUtils;
import com.google.android.material.snackbar.Snackbar;
import android.widget.ProgressBar;

import java.util.List;

public class FavoriteFragment extends Fragment implements FavoriteView, FavoriteAdapter.OnFavoriteClickListener {

    private FavoritePresenter presenter;
    private FavoriteAdapter adapter;
    private RecyclerView rvFavorites;
    private android.widget.TextView tvNoFavorites;
    private NetworkMonitor networkMonitor;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        rvFavorites = view.findViewById(R.id.rv_favorites);
        tvNoFavorites = view.findViewById(R.id.tv_no_favorites);
        progressBar = view.findViewById(R.id.progress_bar);
        rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 2)); 
        adapter = new FavoriteAdapter(requireContext(), this);
        rvFavorites.setAdapter(adapter);

        networkMonitor = new NetworkMonitor(requireContext());
        presenter = new FavoritePresenterImpl(this, new MealRepository(requireContext()));
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.getFavorites();
    }

    @Override
    public void showFavorites(List<Meal> meals) {
        if (meals == null || meals.isEmpty()) {
            tvNoFavorites.setVisibility(View.VISIBLE);
            rvFavorites.setVisibility(View.GONE);
        } else {
            tvNoFavorites.setVisibility(View.GONE);
            rvFavorites.setVisibility(View.VISIBLE);
            adapter.setMeals(meals);
        }
    }

    @Override
    public void showMessage(String message) {
        if (isAdded()) {
            if (message.contains("Success") || message.contains("Removed")) {
                AlertUtils.showSuccess(requireContext(), message);
            } else if (message.contains("Failed") || message.contains("Error") || message.contains("logged in")) {
                AlertUtils.showError(requireContext(), message);
            } else {
                AlertUtils.showSuccess(requireContext(), message);
            }
        }
    }

    @Override
    public void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onMealClick(Meal meal) {
        MealDetailsFragment fragment = new MealDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("meal", meal);
        fragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.home_content_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDeleteClick(Meal meal) {
        if (NetworkMonitor.isNetworkAvailable(requireContext())) {
            AlertUtils.showConfirmation(requireContext(), "Remove Favorite", 
                "Are you sure you want to remove " + meal.getStrMeal() + " from favorites?", "Remove",
                () -> presenter.removeFromFavorites(meal));
        } else {
            showMessage("You must be logged in to sync and remove favorites");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter instanceof FavoritePresenterImpl) {
            ((FavoritePresenterImpl) presenter).onDestroy();
        }
    }
}
