package com.example.mealway.screen.profile.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.NavOptions;
import com.bumptech.glide.Glide;
import com.example.mealway.R;
import com.example.mealway.data.repository.AuthRepositoryImpl;
import com.example.mealway.screen.profile.presenter.ProfilePresenter;
import com.example.mealway.screen.profile.presenter.ProfilePresenterImpl;
import com.example.mealway.utils.AlertUtils;
import com.example.mealway.utils.NetworkMonitor;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileFragment extends Fragment implements ProfileView, ProfileUIListener {

    private ProfilePresenter presenter;
    private TextView tvName, tvEmail, tvPhone;
    private ImageView ivProfile;
    private FloatingActionButton fabEditPhoto;
    private MaterialButton btnLogout;
    private ProgressBar progressBar;

    private final ActivityResultLauncher<androidx.activity.result.PickVisualMediaRequest> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            uri -> {
                if (uri != null) {
                    android.util.Log.d("ProfileFragment", "Image picked: " + uri);
                    presenter.uploadImage(uri);
                } else {
                    android.util.Log.d("ProfileFragment", "No image selected");
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_user_email);
        tvPhone = view.findViewById(R.id.tv_user_phone);
        ivProfile = view.findViewById(R.id.iv_profile_pic);
        fabEditPhoto = view.findViewById(R.id.fab_edit_photo);
        btnLogout = view.findViewById(R.id.btn_logout);
        progressBar = view.findViewById(R.id.progress_bar);

        presenter = new ProfilePresenterImpl(this, new AuthRepositoryImpl(requireContext()));

        fabEditPhoto.setOnClickListener(v -> onEditPhotoClicked());

        btnLogout.setOnClickListener(v -> onLogoutClicked());

        presenter.loadUserData();

        return view;
    }

    @Override
    public void showUserData(String name, String email, String phone, Uri photoUrl) {
        tvName.setText(name != null ? name : "MealWay User");
        tvEmail.setText(email);
        tvPhone.setText("Phone: " + (phone != null ? phone : "Not set"));
        btnLogout.setText("Logout");
        fabEditPhoto.setVisibility(View.VISIBLE);
        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).placeholder(R.drawable.ic_profile).into(ivProfile);
        }
    }

    @Override
    public void showGuestMode() {
        tvName.setText("Guest");
        tvEmail.setText("guest@mealway.com");
        tvPhone.setText("Login to see more");
        btnLogout.setText("Login");
        fabEditPhoto.setVisibility(View.GONE);
        ivProfile.setImageResource(R.drawable.ic_profile);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void navigateToLogin() {
        Navigation.findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.loginFragment, null, new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build());
    }

    @Override
    public void showMessage(String message) {
        if (isAdded()) {
            if (message.contains("Success") || message.contains("updated")) {
                AlertUtils.showSuccess(requireContext(), message);
            } else {
                AlertUtils.showError(requireContext(), message);
            }
        }
    }

    @Override
    public void updateProfileImage(Uri uri) {
        Glide.with(this).load(uri).into(ivProfile);
    }

    @Override
    public void onLogoutClicked() {
        if (btnLogout.getText().toString().equalsIgnoreCase("Login")) {
            navigateToLogin();
            return;
        }
        AlertUtils.showConfirmation(requireContext(), "Logout", "Are you sure you want to sign out?", "Logout", () -> presenter.logout());
    }

    @Override
    public void onEditPhotoClicked() {
        if (NetworkMonitor.isNetworkAvailable(requireContext())) {
            imagePickerLauncher.launch(new androidx.activity.result.PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        } else {
            AlertUtils.showError(requireContext(), "Internet connection required to change photo");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
