package com.example.mealway.screen.search.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealway.R;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.common.NoInternetFragment;
import com.example.mealway.screen.search.presenter.SearchPresenter;
import com.example.mealway.screen.search.presenter.SearchPresenterImpl;
import com.example.mealway.utils.NetworkMonitor;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class SearchFragment extends Fragment implements SearchView, SearchClickListener {

    private SearchPresenter presenter;
    private SearchAdapter adapter;
    private ProgressBar progressBar;
    private TextInputEditText etSearch;
    private MaterialButton btnCategory, btnIngredient, btnCountry;
    private FrameLayout noInternetContainer;
    private NetworkMonitor networkMonitor;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new SearchPresenterImpl(this, new MealRepository(requireContext()));
        
        initViews(view);
        setupListeners();
        
        presenter.loadRandomMeals();
        setupNetworkMonitor();
    }

    private void setupNetworkMonitor() {
        networkMonitor = new NetworkMonitor(requireContext());
        networkMonitor.observe(getViewLifecycleOwner(), isConnected -> {
            if (isConnected) {
                noInternetContainer.setVisibility(View.GONE);
            } else {
                noInternetContainer.setVisibility(View.VISIBLE);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.no_internet_search_container, new NoInternetFragment())
                        .commitAllowingStateLoss();
            }
        });
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progress_bar_search);
        etSearch = view.findViewById(R.id.et_search);
        btnCategory = view.findViewById(R.id.btn_filter_category);
        btnIngredient = view.findViewById(R.id.btn_filter_ingredient);
        btnCountry = view.findViewById(R.id.btn_filter_country);
        noInternetContainer = view.findViewById(R.id.no_internet_search_container);
        
        RecyclerView rvResults = view.findViewById(R.id.rv_search_results);
        adapter = new SearchAdapter(requireContext(), this);
        rvResults.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rvResults.setAdapter(adapter);
    }

    private void setupListeners() {
        btnCategory.setOnClickListener(v -> presenter.fetchCategories());
        btnIngredient.setOnClickListener(v -> presenter.fetchIngredients());
        btnCountry.setOnClickListener(v -> presenter.fetchAreas());

        // Real-time local filtering as requested ("If write name other item deleted in view")
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterLocally(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnClickListener(v -> {
            presenter.setSearchMode(0);
            updateButtonStyles(0);
        });

        // Search by name (API call) when pressing Enter
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                presenter.setSearchMode(0); // Reset to Name search
                updateButtonStyles(0);
                presenter.searchMeals(v.getText().toString());
                return true;
            }
            return false;
        });
    }

    @Override
    public void showMeals(List<Meal> meals) {
        adapter.setMeals(meals);
    }
    
    @Override
    public void showSuggestions(List<String> suggestions) {
    }

    @Override
    public void showFilterOptions(List<String> options, String title, int mode) {
        String[] items = options.toArray(new String[0]);
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(title)
                .setItems(items, (dialog, which) -> {
                    String selected = items[which];
                    etSearch.setText(""); // Clear search bar for new filter
                    updateButtonStyles(mode); // Update UI state
                    switch (mode) {
                        case 1: presenter.filterByCategory(selected); break;
                        case 2: presenter.filterByIngredient(selected); break;
                        case 3: presenter.filterByArea(selected); break;
                    }
                })
                .show();
    }

    private void updateButtonStyles(int activeMode) {
        int mainColor = ContextCompat.getColor(requireContext(), R.color.MainColor);
        int white = ContextCompat.getColor(requireContext(), R.color.white);
        int black = ContextCompat.getColor(requireContext(), R.color.black);
        int backgroundContrast = ContextCompat.getColor(requireContext(), R.color.background_light_alt);

        // Reset all (Inactive)
        resetButtonStyle(btnCategory, mainColor, black, backgroundContrast);
        resetButtonStyle(btnIngredient, mainColor, black, backgroundContrast);
        resetButtonStyle(btnCountry, mainColor, black, backgroundContrast);

        // Set active
        switch (activeMode) {
            case 1: setActiveButtonStyle(btnCategory, mainColor, white); break;
            case 2: setActiveButtonStyle(btnIngredient, mainColor, white); break;
            case 3: setActiveButtonStyle(btnCountry, mainColor, white); break;
        }
    }

    private void resetButtonStyle(MaterialButton button, int mainColor, int whiteColor, int bgColor) {
        button.setBackgroundColor(bgColor);
        button.setTextColor(whiteColor);
        button.setStrokeColor(android.content.res.ColorStateList.valueOf(whiteColor));
    }

    private void setActiveButtonStyle(MaterialButton button, int mainColor, int whiteColor) {
        button.setBackgroundColor(mainColor);
        button.setTextColor(whiteColor);
        button.setStrokeColor(android.content.res.ColorStateList.valueOf(whiteColor));
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

    @Override
    public void onMealClick(Meal meal) {
        com.example.mealway.screen.mealdetails.view.MealDetailsFragment fragment = new com.example.mealway.screen.mealdetails.view.MealDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("meal", meal);
        fragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.home_content_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
