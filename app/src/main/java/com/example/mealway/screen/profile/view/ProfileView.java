package com.example.mealway.screen.profile.view;

import android.net.Uri;

public interface ProfileView {
    void showUserData(String name, String email, String phone);
    void updateProfileImage(String imageUrl);
    void showGuestMode();
    void showLoading();
    void hideLoading();
    void navigateToLogin();
    void showMessage(String message);

    void showOfflineDialog(String title, String message);

}
