package com.example.mealway.screen.profile.presenter;

import android.net.Uri;
import com.example.mealway.data.callback.AuthCallback;
import com.example.mealway.data.repository.AuthRepository;
import com.example.mealway.screen.profile.view.ProfileView;

public class ProfilePresenterImpl implements ProfilePresenter {

    private ProfileView view;
    private AuthRepository repository;

    public ProfilePresenterImpl(ProfileView view, AuthRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void loadUserData() {
        if (!repository.isLoggedIn()) {
            view.showGuestMode();
            return;
        }

        view.showLoading();
        repository.getUserDetails(new AuthRepository.UserDataCallback() {
            @Override
            public void onDataFetched(String fullName, String phone, String email, Uri photoUrl) {
                if (view != null) {
                    view.hideLoading();
                    view.showUserData(fullName, email, phone, photoUrl);
                }
            }

            @Override
            public void onError(String error) {
                if (view != null) {
                    view.hideLoading();
                    view.showMessage(error);
                }
            }
        });
    }

    @Override
    public void logout() {
        repository.signOut();
        view.navigateToLogin();
    }

    @Override
    public void uploadImage(Uri imageUri) {
        if (view == null) return;
        view.showLoading();
        repository.uploadProfileImage(imageUri, new AuthCallback() {
            @Override
            public void onSuccess() {
                if (view != null) {
                    view.hideLoading();
                    view.updateProfileImage(imageUri);
                    view.showMessage("Profile photo updated!");
                }
            }

            @Override
            public void onFailure(String error) {
                if (view != null) {
                    view.hideLoading();
                    view.showMessage("Failed to upload image: " + error);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}
