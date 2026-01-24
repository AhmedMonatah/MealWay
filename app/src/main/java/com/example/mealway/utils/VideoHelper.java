package com.example.mealway.utils;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoHelper {

    /**
     * Extracts the YouTube Video ID from various URL formats.
     */
    public static String extractVideoId(String url) {
        if (url == null || url.trim().isEmpty()) return null;

        String pattern = "(?:https?:\\/\\/)?(?:www\\.|m\\.|music\\.)?youtu(?:be\\.com\\/(?:watch\\?v=|embed\\/|v\\/)|\\.be\\/)([\\w-]{11})";
        Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) return matcher.group(1);
        return null;
    }

    /**
     * Prepares a video for playback using YouTubePlayerView.
     */
    public static void setupVideo(
            @androidx.annotation.NonNull Context context,
            String youtubeUrl,
            @androidx.annotation.NonNull ImageView thumbnailView,
            @androidx.annotation.NonNull YouTubePlayerView playerView,
            @androidx.annotation.NonNull View container,
            android.widget.TextView noVideoTextView
    ) {
        String videoId = extractVideoId(youtubeUrl);

        if (videoId != null && !videoId.isEmpty()) {
            // Show thumbnail
            String thumbUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
            Glide.with(context)
                    .load(thumbUrl)
                    .centerCrop()
                    .into(thumbnailView);

            // Ensure YouTubePlayerView is visible but behind the card initially so it can init
            playerView.setVisibility(View.VISIBLE);
            container.setVisibility(View.VISIBLE);
            if (noVideoTextView != null) noVideoTextView.setVisibility(View.GONE);

            // Click on thumbnail/card to play video
            container.setOnClickListener(v -> {
                container.setVisibility(View.GONE);
                playerView.getYouTubePlayerWhenReady(youTubePlayer -> {
                    youTubePlayer.loadVideo(videoId, 0);
                });
            });
        } else {
            // No valid video
            container.setVisibility(View.GONE);
            playerView.setVisibility(View.GONE);
            if (noVideoTextView != null) noVideoTextView.setVisibility(View.VISIBLE);
        }
    }
}
