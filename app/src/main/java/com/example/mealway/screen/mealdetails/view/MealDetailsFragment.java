package com.example.mealway.screen.mealdetails.view;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mealway.utils.VideoHelper;

import com.bumptech.glide.Glide;
import com.example.mealway.R;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.mealdetails.IngredientsAdapter;
import com.example.mealway.screen.mealdetails.presenter.MealDetailsPresenter;
import com.example.mealway.screen.mealdetails.presenter.MealDetailsPresenterImpl;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import android.widget.Button;
import java.util.Calendar;
import androidx.cardview.widget.CardView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mealway.utils.AlertUtils;

public class MealDetailsFragment extends Fragment implements MealDetailsView, MealDetailsUIListener {

    private MealDetailsPresenter presenter;
    private ImageView ivMealImage, ivVideoThumbnail;
    private TextView tvInstructions, tvNoVideo, tvDetailArea;
    private YouTubePlayerView youTubePlayerView;

    private Button btnPlanMeal;
    private ProgressBar progressBar;

    private CollapsingToolbarLayout collapsingToolbar;
    private RecyclerView rvIngredients;
    private FloatingActionButton fabFavorite;
    private CardView cardVideo;
    private Meal currentMeal;
    private boolean isFavorite = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_details, container, false);

        ivMealImage = view.findViewById(R.id.iv_detail_meal);
        tvInstructions = view.findViewById(R.id.tv_instructions);
        tvDetailArea = view.findViewById(R.id.tv_detail_area);
        rvIngredients = view.findViewById(R.id.rv_ingredients);
        fabFavorite = view.findViewById(R.id.fab_favorite);
        collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);
        cardVideo = view.findViewById(R.id.card_video);
        ivVideoThumbnail = view.findViewById(R.id.iv_video_thumbnail);
        tvNoVideo = view.findViewById(R.id.tv_no_video);
        btnPlanMeal = view.findViewById(R.id.btn_plan_meal);
        progressBar = view.findViewById(R.id.progress_bar);
        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        presenter = new MealDetailsPresenterImpl(this, new MealRepository(requireContext()));

        if (getArguments() != null) {
            Meal mealFromArgs = getArguments().getParcelable("meal");
            if (mealFromArgs != null) {
                String mealId = mealFromArgs.getIdMeal();
                if (mealId != null) {
                    presenter.getMealDetails(mealId);
                    presenter.checkFavoriteStatus(mealId);
                }
            }
        }





        fabFavorite.setOnClickListener(v -> onFavoriteClicked());
        btnPlanMeal.setOnClickListener(v -> onPlanClicked());


        return view;
    }

    @Override
    public void onFavoriteClicked() {
        presenter.onFavoriteClicked(currentMeal, isFavorite);
    }

    @Override
    public void onPlanClicked() {
        presenter.onPlanClicked();
    }



    @Override
    public void navigateToLogin() {
        AlertUtils.showConfirmation(requireContext(), "Login Required",
            "You need to login to perform this action. Go to login?", "Login",
            () -> {
                Navigation.findNavController(requireActivity(), R.id.nav_host)
                        .navigate(R.id.loginFragment);
            });
    }

    @Override
    public void showDatePicker() {
        if (currentMeal == null) return;

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(requireContext(),
                R.style.CustomPickerTheme,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, monthOfYear, dayOfMonth);
                    presenter.addAppointment(currentMeal, selectedDate.getTimeInMillis());
                }, year, month, day);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            datePickerDialog.getDatePicker().setForceDarkAllowed(false);
        }
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }





    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
        }
    }

    @Override
    public void showMealDetails(Meal meal) {
        this.currentMeal = meal;
        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle(meal.getStrMeal());
        }
        tvInstructions.setText(meal.getStrInstructions());
        if (tvDetailArea != null) {
            tvDetailArea.setText(meal.getStrArea() + " | " + meal.getStrCategory());
        }
        Glide.with(this).load(meal.getStrMealThumb()).into(ivMealImage);

        IngredientsAdapter adapter = new IngredientsAdapter(requireContext(), meal.getIngredientsWithMeasures());
        rvIngredients.setAdapter(adapter);

        setupVideo(meal.getStrYoutube());
    }

    private void setupVideo(String youtubeUrl) {
        VideoHelper.setupVideo(
                requireContext(),
                youtubeUrl,
                ivVideoThumbnail,
                youTubePlayerView,
                cardVideo,
                tvNoVideo
        );
    }



    @Override
    public void showFavoriteStatus(boolean isFavorite) {
        this.isFavorite = isFavorite;
        if (isFavorite) {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            fabFavorite.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.RED));
        } else {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            fabFavorite.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY));
        }
    }

    @Override
    public void showMessage(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showSuccess(int stringResId) {
        if (isAdded()) {
            AlertUtils.showSuccess(requireContext(), getString(stringResId));
        }
    }

    @Override
    public void showError(String message) {
        if (isAdded()) {
            AlertUtils.showError(requireContext(), message);
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
}
