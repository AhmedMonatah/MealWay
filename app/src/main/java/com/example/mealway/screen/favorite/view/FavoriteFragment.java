package com.example.mealway.screen.favorite.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.example.mealway.screen.search.view.SearchAdapter;
import com.example.mealway.screen.search.view.SearchClickListener;
import com.example.mealway.screen.mealdetails.view.MealDetailsFragment;

import java.util.List;

public class FavoriteFragment extends Fragment implements FavoriteView, SearchClickListener {

    private FavoritePresenter presenter;
    private SearchAdapter adapter;
    private RecyclerView rvFavorites;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        rvFavorites = view.findViewById(R.id.rv_favorites);
        rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        adapter = new SearchAdapter(requireContext(), this);
        rvFavorites.setAdapter(adapter);

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
        adapter.setMeals(meals);
    }

    @Override
    public void showMessage(String message) {
        if (isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
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
    public void onDestroy() {
        super.onDestroy();
        if (presenter instanceof FavoritePresenterImpl) {
            ((FavoritePresenterImpl) presenter).onDestroy();
        }
    }
}
