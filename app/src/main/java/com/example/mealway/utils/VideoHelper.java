package com.example.mealway.utils;

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
     * Configures the WebView with settings optimized for YouTube playback.
     */
    public static void configureWebView(android.webkit.WebView webView) {
        if (webView == null) return;

        android.webkit.WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        
        // Mimic a full mobile browser to avoid "An error occurred" or restriction issues
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.119 Mobile Safari/537.36");

        webView.setWebChromeClient(new android.webkit.WebChromeClient());
        webView.setWebViewClient(new android.webkit.WebViewClient());
    }

    /**
     * Loads the YouTube video into the WebView using the embed API.
     */
    public static void loadVideo(android.webkit.WebView webView, String videoId) {
        if (webView == null || videoId == null) return;

        String html = "<html><body style=\"margin: 0; padding: 0\">" +
                "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + videoId + "\"" +
                " frameborder=\"0\" allowfullscreen style=\"border:none\"></iframe>" +
                "</body></html>";

        // Using loadDataWithBaseURL is crucial for YouTube resources to load correctly
        webView.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null);
    }
}
