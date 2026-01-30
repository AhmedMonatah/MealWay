package com.example.mealway.screen.homecontent.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mealway.R;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.homecontent.presenter.HomeContentPresenter;
import com.example.mealway.screen.homecontent.presenter.HomeContentPresenterImpl;
import com.example.mealway.screen.mealdetails.view.MealDetailsFragment;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeContentFragment extends Fragment implements HomeContentView {

    private HomeContentPresenter presenter;
    private ImageView ivRandomMeal;
    private TextView tvRandomMealName;
    private CardView cardRandomMeal;
    private RecyclerView rvMeals;
    private MealAdapter mealAdapter;
    private ProgressBar progressBar;
    private Meal randomMeal;
    private ShimmerFrameLayout shimmerRandomMeal;
    private ShimmerFrameLayout shimmerHorizontalList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_content, container, false);

        ivRandomMeal = view.findViewById(R.id.iv_random_meal);
        tvRandomMealName = view.findViewById(R.id.tv_random_meal_name);
        cardRandomMeal = view.findViewById(R.id.card_random_meal);
        rvMeals = view.findViewById(R.id.rv_meals_horizontal);
        progressBar = view.findViewById(R.id.progress_home);
        shimmerRandomMeal = view.findViewById(R.id.shimmer_random_meal);
        shimmerHorizontalList = view.findViewById(R.id.shimmer_meals_horizontal);

        setupRecyclerView();

        MealRepository repository = new MealRepository(requireContext());
        presenter = new HomeContentPresenterImpl(this, repository);
        
        shimmerRandomMeal.startShimmer();
        shimmerHorizontalList.startShimmer();

        presenter.getHomeData();

        cardRandomMeal.setOnClickListener(v -> {
            if (randomMeal != null) {
                navigateToDetails(randomMeal);
            }
        });

        return view;
    }

    private void setupRecyclerView() {
        rvMeals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mealAdapter = new MealAdapter(getContext(), new ArrayList<>(), this::navigateToDetails);
        rvMeals.setAdapter(mealAdapter);
    }

    private void navigateToDetails(Meal meal) {
        MealDetailsFragment fragment = new MealDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("meal", meal);
        fragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.home_content_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showRandomMeal(Meal meal) {
        this.randomMeal = meal;
        tvRandomMealName.setText(meal.getStrMeal());
        
        Glide.with(this)
                .load(meal.getStrMealThumb())
                .into(ivRandomMeal);
    }

    @Override
    public void showHorizontalMealList(List<Meal> meals) {
        mealAdapter.setMeals(meals);
        rvMeals.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        if (isAdded()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            hideLoading();
        }
    }

    @Override
    public void showLoading() {
        if (isAdded()) {
            if (shimmerRandomMeal != null) shimmerRandomMeal.startShimmer();
            if (shimmerHorizontalList != null) shimmerHorizontalList.startShimmer();
        }
    }

    @Override
    public void hideLoading() {
        if (isAdded()) {
            if (shimmerRandomMeal != null) {
                shimmerRandomMeal.stopShimmer();
                shimmerRandomMeal.setVisibility(View.GONE);
                ivRandomMeal.setVisibility(View.VISIBLE);
            }
            if (shimmerHorizontalList != null) {
                shimmerHorizontalList.stopShimmer();
                shimmerHorizontalList.setVisibility(View.GONE);
                rvMeals.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }
}
