package com.example.mealway.helper;

import android.view.View;
import android.widget.ImageView;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import com.bumptech.glide.Glide;

public class VideoHelper {

    public static void bindVideo(View container,
                                 ImageView thumbnailView,
                                 View noVideoView,
                                 WebView webView,
                                 String youtubeUrl) {

        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
            String videoId = extractVideoId(youtubeUrl);

            if (videoId != null) {
                container.setVisibility(View.VISIBLE);
                if (noVideoView != null) noVideoView.setVisibility(View.GONE);

                // تحميل الصورة المصغرة
                String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
                Glide.with(container.getContext())
                        .load(thumbnailUrl)
                        .into(thumbnailView);

                // عند الضغط على thumbnail نشغل الفيديو داخل WebView
                container.setOnClickListener(v -> {
                    container.setVisibility(View.GONE);
                    if (webView != null) {
                        webView.setVisibility(View.VISIBLE);

                        // إعدادات WebView
                        WebSettings webSettings = webView.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        webSettings.setDomStorageEnabled(true);
                        webSettings.setMediaPlaybackRequiresUserGesture(false);
                        webSettings.setLoadWithOverviewMode(true);
                        webSettings.setUseWideViewPort(true);

                        webView.setWebChromeClient(new WebChromeClient());
                        webView.setWebViewClient(new WebViewClient() {
                            @SuppressWarnings("deprecation")
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                return false;
                            }
                        });

                        String videoHtml = "<html><body style='margin:0;padding:0;background-color:black;'>" +
                                "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + videoId + "?autoplay=1&rel=0\" " +
                                "title=\"YouTube video player\" frameborder=\"0\" " +
                                "allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" " +
                                "allowfullscreen></iframe>" +
                                "</body></html>";

                        webView.loadDataWithBaseURL("https://www.youtube.com", videoHtml, "text/html", "utf-8", null);
                    }
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
                String[] split = url.split("youtu.be/");
                if (split.length > 1) {
                    return split[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
