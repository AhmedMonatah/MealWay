package com.example.mealway.screen.profile.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.NavOptions;
import com.example.mealway.R;
import com.example.mealway.data.repository.AuthRepositoryImpl;
import com.example.mealway.screen.profile.presenter.ProfilePresenter;
import com.example.mealway.screen.profile.presenter.ProfilePresenterImpl;
import com.example.mealway.utils.AlertUtils;
import com.example.mealway.utils.NetworkMonitor;
import com.google.android.material.button.MaterialButton;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.example.mealway.utils.AlertUtils;

public class ProfileFragment extends Fragment implements ProfileView, ProfileUIListener {

    private ProfilePresenter presenter;
    private TextView tvName, tvEmail, tvPhone;
    private ImageView ivProfile;
    private MaterialButton btnLogout;
    private com.google.android.material.floatingactionbutton.FloatingActionButton btnChangePhoto;
    private ProgressBar progressBar;

    private String currentName, currentPhone;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    presenter.uploadProfileImage(uri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.tv_user_name);
        tvEmail = view.findViewById(R.id.tv_user_email);
        tvPhone = view.findViewById(R.id.tv_user_phone);
        ivProfile = view.findViewById(R.id.iv_profile_pic);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnChangePhoto = view.findViewById(R.id.btn_change_photo);
        progressBar = view.findViewById(R.id.progress_bar);

        presenter = new ProfilePresenterImpl(this, new AuthRepositoryImpl(requireContext()));

        btnLogout.setOnClickListener(v -> onLogoutClicked());
        ivProfile.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnChangePhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        presenter.loadUserData();

        return view;
    }


    @Override
    public void updateProfileImage(String imageUrl) {
        if (isAdded() && imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.ic_profile)
                    .into(ivProfile);
        }
    }

    @Override
    public void showUserData(String name, String email, String phone) {
        this.currentName = name;
        this.currentPhone = phone;

        tvName.setText(name != null ?  name : getString(R.string.default_username));
        tvEmail.setText(email);
        
        String phoneToShow = (phone != null && !phone.isEmpty()) ? phone : getString(R.string.not_set);
        tvPhone.setText(getString(R.string.phone_label_with_placeholder, phoneToShow));
        
        if (btnChangePhoto != null) {
            btnChangePhoto.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showGuestMode() {
        this.currentName = null;
        this.currentPhone = null;
        tvName.setText(getString(R.string.guest));
        tvEmail.setText(getString(R.string.guest_email));
        tvPhone.setText(getString(R.string.login_to_see_more));
        btnLogout.setText(getString(R.string.Login));
        ivProfile.setImageResource(R.drawable.ic_profile);
        if (btnChangePhoto != null) {
            btnChangePhoto.setVisibility(View.GONE);
        }
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
    public void onLogoutClicked() {
        if (btnLogout.getText().toString().equalsIgnoreCase(getString(R.string.Login))) {
            navigateToLogin();
            return;
        }

        if (!NetworkMonitor.isNetworkAvailable(requireContext())) {
            AlertUtils.showError(requireContext(), getString(R.string.no_internet_error));
            return;
        }

        AlertUtils.showConfirmation(requireContext(), 
                getString(R.string.logout_confirmation_title), 
                getString(R.string.logout_confirmation_message), 
                getString(R.string.logout), 
                () -> presenter.logout());
    }

    @Override
    public void showOfflineDialog(String title, String message) {
        AlertUtils.showConfirmation(
                requireContext(),
                title,
                message,
                "Try Again",
                () -> presenter.logout()
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
