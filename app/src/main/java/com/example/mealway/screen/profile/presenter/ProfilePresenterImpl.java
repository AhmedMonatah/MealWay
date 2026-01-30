package com.example.mealway.screen.profile.presenter;

import android.net.Uri;
import com.example.mealway.data.callback.AuthCallback;
import com.example.mealway.data.repository.AuthRepository;
import com.example.mealway.data.repository.UserDataCallback;
import com.example.mealway.screen.profile.view.ProfileView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

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
        repository.getUserDetails(new UserDataCallback() {
            @Override
            public void onDataFetched(String fullName, String phone, String email, String profileImage) {
                if (view != null) {
                    view.hideLoading();
                    view.showUserData(fullName, email, phone);
                    if (profileImage != null) {
                        view.updateProfileImage(profileImage);
                    }
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
        if (view == null) return;

        view.showLoading();

        repository.signOut()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (view != null) {
                        view.hideLoading();
                        view.navigateToLogin();
                    }
                }, throwable -> {
                    if (view == null) return;

                    view.hideLoading();

                    if (throwable instanceof IllegalStateException
                            && "NO_NETWORK".equals(throwable.getMessage())) {

                        view.showOfflineDialog(
                                "Offline",
                                "Cannot logout while offline"
                        );
                    } else {
                        view.showMessage("Logout failed");
                    }
                });
    }



    @Override
    public void uploadProfileImage(Uri imageUri) {
        if (view != null) {
            view.showLoading();
            repository.uploadProfileImage(imageUri)
                    .subscribe(() -> {
                        if (view != null) {
                            loadUserData();
                            view.showMessage("Profile picture updated successfully!");
                        }
                    }, throwable -> {
                        if (view != null) {
                            view.hideLoading();
                            view.showMessage("Failed to upload image: " + throwable.getMessage());
                        }
                    });
        }
    }




    @Override
    public void onDestroy() {
        view = null;
    }
}
