package com.example.mealway.screen.profile.presenter;

import android.net.Uri;

public interface ProfilePresenter {
    void loadUserData();
    void logout();
    void uploadImage(Uri imageUri);
    void onDestroy();
}
