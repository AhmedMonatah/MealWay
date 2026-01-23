package com.example.mealway.helper;

import android.view.View;
import android.widget.ImageView;

public class VideoHelper {

    public static void bindVideo(View container, ImageView thumbnailView, View noVideoView, String youtubeUrl) {
        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
             String videoId = extractVideoId(youtubeUrl);
             if (videoId != null) {
                 container.setVisibility(View.VISIBLE);
                 if (noVideoView != null) noVideoView.setVisibility(View.GONE);
                 
                 // Fetch YouTube high-quality thumbnail
                 String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
                 com.bumptech.glide.Glide.with(container.getContext())
                         .load(thumbnailUrl)
                         .into(thumbnailView);

                 container.setOnClickListener(v -> {
                     android.content.Intent intent = new android.content.Intent(
                             android.content.Intent.ACTION_VIEW, 
                             android.net.Uri.parse(youtubeUrl));
                     container.getContext().startActivity(intent);
                 });
             } else {
                 container.setVisibility(View.GONE);
                 if (noVideoView != null) noVideoView.setVisibility(View.VISIBLE);
             }
        } else {
            container.setVisibility(View.GONE);
            if (noVideoView != null) noVideoView.setVisibility(View.VISIBLE);
        }
    }

    private static String extractVideoId(String url) {
        try {
            if (url.contains("v=")) {
                String[] split = url.split("v=");
                if (split.length > 1) {
                    String videoId = split[1];
                    int ampersandPosition = videoId.indexOf('&');
                    if (ampersandPosition != -1) {
                        videoId = videoId.substring(0, ampersandPosition);
                    }
                    return videoId;
                }
            } else if (url.contains("youtu.be/")) {
                // Handle shortened urls if they appear
                 String[] split = url.split("youtu.be/");
                 if (split.length > 1) {
                     return split[1];
                 }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Invalid or unparsable URL
    }
}
