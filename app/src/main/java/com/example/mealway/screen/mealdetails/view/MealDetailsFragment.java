package com.example.mealway.screen.mealdetails.view;

import android.os.Bundle;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.bumptech.glide.Glide;
import com.example.mealway.R;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.mealdetails.IngredientsAdapter;
import com.example.mealway.screen.mealdetails.presenter.MealDetailsPresenter;
import com.example.mealway.screen.mealdetails.presenter.MealDetailsPresenterImpl;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import android.app.DatePickerDialog;
import com.example.mealway.utils.NetworkMonitor;
import android.widget.Button;
import java.util.Calendar;
import androidx.cardview.widget.CardView;
import android.widget.ProgressBar;
import com.example.mealway.utils.AlertUtils;

public class MealDetailsFragment extends Fragment implements MealDetailsView, MealDetailsUIListener {

    private MealDetailsPresenter presenter;
    private ImageView ivMealImage, ivVideoThumbnail;
    private TextView tvInstructions, tvNoVideo, tvDetailArea;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer mYouTubePlayer;
    private Button btnPlanMeal;
    private com.google.firebase.auth.FirebaseAuth auth;
    private ProgressBar progressBar;

    private CollapsingToolbarLayout collapsingToolbar;
    private RecyclerView rvIngredients;
    private FloatingActionButton fabFavorite;
    private CardView cardVideo;
    private Meal currentMeal;
    private boolean isFavorite = false;
    private NetworkMonitor networkMonitor;

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

        networkMonitor = new NetworkMonitor(requireContext());

        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        auth = com.google.firebase.auth.FirebaseAuth.getInstance();
        presenter = new MealDetailsPresenterImpl(this, new MealRepository(requireContext()));

        Meal mealFromArgs = (Meal) getArguments().getSerializable("meal");
        if (mealFromArgs != null) {
            String mealId = mealFromArgs.getIdMeal();
            if (mealId != null) {
                presenter.getMealDetails(mealId);
                presenter.checkFavoriteStatus(mealId);
            }
        }

        IFramePlayerOptions options = new IFramePlayerOptions.Builder()
                .controls(1)
                .build();

        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                mYouTubePlayer = youTubePlayer;
            }
        }, options);

        fabFavorite.setOnClickListener(v -> onFavoriteClicked());
        btnPlanMeal.setOnClickListener(v -> onPlanClicked());
        cardVideo.setOnClickListener(v -> onVideoCardClicked());

        return view;
    }

    @Override
    public void onFavoriteClicked() {
        if (auth.getCurrentUser() == null) {
            AlertUtils.showConfirmation(requireContext(), "Login Required", 
                "You need to login to add to favorites. Go to login?", "Login",
                () -> {
                    Navigation.findNavController(requireActivity(), R.id.nav_host)
                            .navigate(R.id.loginFragment);
                });
            return;
        }
        if (currentMeal != null) {
            if (isFavorite) {
                if (NetworkMonitor.isNetworkAvailable(requireContext())) {
                    AlertUtils.showConfirmation(requireContext(), "Remove Favorite", 
                        "Are you sure you want to remove this meal from favorites?", "Remove",
                        () -> presenter.removeFromFavorites(currentMeal));
                } else {
                    showMessage("You must be logged in to sync and remove from Favorites");
                }
            } else {
                presenter.addToFavorites(currentMeal);
            }
        }
    }

    @Override
    public void onPlanClicked() {
        if (auth.getCurrentUser() == null) {
            AlertUtils.showConfirmation(requireContext(), "Login Required", 
                "You need to login to plan meals. Go to login?", "Login",
                () -> {
                    Navigation.findNavController(requireActivity(), R.id.nav_host)
                            .navigate(R.id.loginFragment);
                });
            return;
        }
        showDatePicker();
    }

    @Override
    public void onVideoCardClicked() {
        if (currentMeal != null && mYouTubePlayer != null) {
            String videoId = extractVideoId(currentMeal.getStrYoutube());
            if (videoId != null) {
                // Hide thumbnail and show player (it's already visible behind)
                cardVideo.setVisibility(View.GONE);
                mYouTubePlayer.loadVideo(videoId, 0);
            } else {
                showMessage("Invalid Video ID");
            }
        } else if (mYouTubePlayer == null) {
            showMessage("Preparing player... please tap again in a moment");
        }
    }

    private void showDatePicker() {
        if (currentMeal == null) return;

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
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
            tvNoVideo.setVisibility(View.GONE);

            String videoId = extractVideoId(youtubeUrl);
            if (videoId != null) {
                String thumbUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
                Glide.with(this).load(thumbUrl).into(ivVideoThumbnail);
            }

        } else {
            cardVideo.setVisibility(View.GONE);
            youTubePlayerView.setVisibility(View.GONE);
            tvNoVideo.setVisibility(View.VISIBLE);
        }
    }

    private String extractVideoId(String url) {
        if (url == null || url.isEmpty()) return null;
        String pattern = "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|youtu.be%2F|%2Fv%2F)[^#&?\\n]*";
        java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
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
            android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show();
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
