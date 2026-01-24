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
            public void onDataFetched(String fullName, String phone, String email) {
                if (view != null) {
                    view.hideLoading();
                    view.showUserData(fullName, email, phone);
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
    public void onDestroy() {
        view = null;
    }
}
