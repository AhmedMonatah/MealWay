package com.example.mealway.screen.mealdetails.view;

import android.os.Bundle;
import android.webkit.WebView;
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
    private boolean isPlayerReady = false;

    private MealDetailsPresenter presenter;
    private ImageView ivMealImage, ivVideoThumbnail;
    private TextView tvInstructions, tvNoVideo, tvDetailArea;
    private WebView iframe;

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
        iframe = view.findViewById(R.id.iframe);
        VideoHelper.configureWebView(iframe);

        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        presenter = new MealDetailsPresenterImpl(this, new MealRepository(requireContext()));

        Meal mealFromArgs = (Meal) getArguments().getSerializable("meal");
        if (mealFromArgs != null) {
            String mealId = mealFromArgs.getIdMeal();
            if (mealId != null) {
                presenter.getMealDetails(mealId);
                presenter.checkFavoriteStatus(mealId);
            }
        }





        fabFavorite.setOnClickListener(v -> onFavoriteClicked());
        btnPlanMeal.setOnClickListener(v -> onPlanClicked());
        cardVideo.setOnClickListener(v -> onVideoCardClicked());


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
    public void onVideoCardClicked() {
        if (currentMeal != null) {
            presenter.onVideoClicked(currentMeal.getStrYoutube());
        }
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
    public void prepareVideo(String videoId) {
        cardVideo.setVisibility(View.GONE);
        iframe.setVisibility(View.VISIBLE);
        VideoHelper.loadVideo(iframe, videoId);
    }

    @Override
    public void showDatePicker() {
        if (currentMeal == null) return;

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, monthOfYear, dayOfMonth);
                    presenter.addAppointment(currentMeal, selectedDate.getTimeInMillis());
                }, year, month, day);

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
        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
            cardVideo.setVisibility(View.VISIBLE);
            iframe.setVisibility(android.view.View.GONE);
            tvNoVideo.setVisibility(android.view.View.GONE);

            String videoId = VideoHelper.extractVideoId(youtubeUrl);
            if (videoId != null) {
                String thumbUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
                Glide.with(this).load(thumbUrl).into(ivVideoThumbnail);
            }

        } else {
            cardVideo.setVisibility(android.view.View.GONE);
            iframe.setVisibility(android.view.View.GONE);
            tvNoVideo.setVisibility(android.view.View.VISIBLE);
        }
    }



    @Override
    public void showFavoriteStatus(boolean isFavorite) {
        this.isFavorite = isFavorite;
        if (isFavorite) {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    @Override
    public void showMessage(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showSuccess(String message) {
        if (isAdded()) {
            AlertUtils.showSuccess(requireContext(), message);
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
