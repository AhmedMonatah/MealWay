package com.example.mealway.screen.homeactivity;

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
import com.example.mealway.screen.mealdetails.MealDetailsFragment;
import java.util.ArrayList;
import java.util.List;

public class HomeContentFragment extends Fragment implements HomeView {

    private HomePresenter presenter;
    private ImageView ivRandomMeal;
    private TextView tvRandomMealName;
    private CardView cardRandomMeal;
    private RecyclerView rvMeals;
    private MealAdapter mealAdapter;
    private ProgressBar progressBar;
    private Meal randomMeal;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_content, container, false);

        ivRandomMeal = view.findViewById(R.id.iv_random_meal);
        tvRandomMealName = view.findViewById(R.id.tv_random_meal_name);
        cardRandomMeal = view.findViewById(R.id.card_random_meal);
        rvMeals = view.findViewById(R.id.rv_meals_horizontal);
        progressBar = view.findViewById(R.id.progress_home);

        setupRecyclerView();

        MealRepository repository = new MealRepository(requireContext());
        presenter = new HomePresenter(this, repository);
        presenter.getRandomMeal();
        presenter.getMealsForList();

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
        args.putSerializable("meal", meal);
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
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivRandomMeal);
    }

    @Override
    public void showHorizontalMealList(List<Meal> meals) {
        mealAdapter.setMeals(meals);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
}
