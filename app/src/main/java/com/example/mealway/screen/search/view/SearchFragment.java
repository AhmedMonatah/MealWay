package com.example.mealway.screen.search.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.mealway.data.model.Area;
import com.example.mealway.data.model.Category;
import com.example.mealway.data.model.Ingredient;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.common.NoInternetFragment;
import com.example.mealway.screen.mealdetails.view.MealDetailsFragment;
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

        initViews(view);
        setupListeners();
        setupNetworkMonitor();

        if (presenter == null) {
            presenter = new SearchPresenterImpl(this, new MealRepository(requireContext()));
            presenter.loadRandomMeals();
        } else {
            updateButtonStyles(presenter.getSearchMode());
            presenter.loadRandomMeals();
        }
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
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3);
        rvResults.setLayoutManager(layoutManager);
        rvResults.setAdapter(adapter);

        rvResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Scrolling down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        presenter.loadNextPage();
                    }
                }
            }
        });
    }

    private void setupListeners() {
        btnCategory.setOnClickListener(v -> presenter.fetchCategories());
        btnIngredient.setOnClickListener(v -> presenter.fetchIngredients());
        btnCountry.setOnClickListener(v -> presenter.fetchAreas());

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
    public void showFilterOptions(List<?> options, String title, int mode) {
        FilterListFragment fragment = FilterListFragment.newInstance(options, title, mode);
        fragment.setOnFilterSelectedListener((selected, activeMode) -> {
            if (!isAdded() || getContext() == null) return;

            String selectedName = "";
            if (selected instanceof Category) selectedName = ((Category) selected).getStrCategory();
            else if (selected instanceof Ingredient) selectedName = ((Ingredient) selected).getStrIngredient();
            else if (selected instanceof Area) selectedName = ((Area) selected).getStrArea();

            if (selectedName == null || selectedName.isEmpty()) return;

            etSearch.setText("");
            updateButtonStyles(activeMode);
            switch (activeMode) {
                case 1: presenter.filterByCategory(selectedName); break;
                case 2: presenter.filterByIngredient(selectedName); break;
                case 3: presenter.filterByArea(selectedName); break;
            }
        });

        fragment.show(getParentFragmentManager(), "filter_dialog");
    }

    private void updateButtonStyles(int activeMode) {
        if (!isAdded() || getContext() == null) return;

        int mainColor = ContextCompat.getColor(getContext(), R.color.MainColor);
        int white = ContextCompat.getColor(getContext(), R.color.white);
        int black = ContextCompat.getColor(getContext(), R.color.black);
        int backgroundContrast = ContextCompat.getColor(getContext(), R.color.background_light_alt);

        resetButtonStyle(btnCategory, mainColor, black, backgroundContrast);
        resetButtonStyle(btnIngredient, mainColor, black, backgroundContrast);
        resetButtonStyle(btnCountry, mainColor, black, backgroundContrast);

        switch (activeMode) {
            case 1: setActiveButtonStyle(btnCategory, mainColor, white); break;
            case 2: setActiveButtonStyle(btnIngredient, mainColor, white); break;
            case 3: setActiveButtonStyle(btnCountry, mainColor, white); break;
        }
    }

    private void resetButtonStyle(MaterialButton button, int mainColor, int whiteColor, int bgColor) {
        button.setBackgroundColor(bgColor);
        button.setTextColor(whiteColor);
        button.setStrokeColor(ColorStateList.valueOf(whiteColor));
    }

    private void setActiveButtonStyle(MaterialButton button, int mainColor, int whiteColor) {
        button.setBackgroundColor(mainColor);
        button.setTextColor(whiteColor);
        button.setStrokeColor(ColorStateList.valueOf(whiteColor));
    }

    @Override
    public void showError(String message) {
        Log.i("SearchFragment", message);
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
    public String getSafeString(int resId) {
        if (isAdded()) {
            return getString(resId);
        }
        return "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.clearDisposables();
        }
    }
}
