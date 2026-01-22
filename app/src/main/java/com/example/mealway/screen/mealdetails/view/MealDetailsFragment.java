package com.example.mealway.screen.mealdetails.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.bumptech.glide.Glide;
import com.example.mealway.R;
import com.example.mealway.data.model.Meal;
import com.example.mealway.data.repository.MealRepository;
import com.example.mealway.screen.mealdetails.IngredientsAdapter;
import com.example.mealway.screen.mealdetails.presenter.MealDetailsPresenter;
import com.example.mealway.screen.mealdetails.presenter.MealDetailsPresenterImpl;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.webkit.WebResourceRequest;

import android.app.DatePickerDialog;
import com.example.mealway.utils.NetworkMonitor;
import android.widget.Button;
import java.util.Calendar;
import androidx.cardview.widget.CardView;

public class MealDetailsFragment extends Fragment implements MealDetailsView {

    private MealDetailsPresenter presenter;
    private ImageView ivMealImage, ivVideoThumbnail;
    private TextView tvInstructions, tvNoVideo, tvDetailArea;
    private YouTubePlayerView youTubePlayerView;
    private Button btnPlanMeal;

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

        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        networkMonitor = new NetworkMonitor(requireContext());

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

        fabFavorite.setOnClickListener(v -> {
            if (currentMeal != null) {
                Boolean isConnected = networkMonitor.getValue();
                if (isConnected != null && isConnected) {
                    if (isFavorite) {
                        presenter.removeFromFavorites(currentMeal);
                    } else {
                        presenter.addToFavorites(currentMeal);
                    }
                } else {
                    showMessage("Internet connection required for Favorites");
                }
            }
        });

        btnPlanMeal.setOnClickListener(v -> showDatePicker());

        cardVideo.setOnClickListener(v -> {
            String videoId = extractVideoId(currentMeal.getStrYoutube());
            if (videoId == null) return;

            cardVideo.setVisibility(View.GONE);
            youTubePlayerView.setVisibility(View.VISIBLE);

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.cueVideo(videoId, 0);
                }
            });
        });

        return view;
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
            youTubePlayerView.setVisibility(View.GONE);
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
        if (url == null) return null;
        if (url.contains("v=")) {
            return url.split("v=")[1].split("&")[0];
        } else if (url.contains("be/")) {
            return url.split("be/")[1];
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
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
