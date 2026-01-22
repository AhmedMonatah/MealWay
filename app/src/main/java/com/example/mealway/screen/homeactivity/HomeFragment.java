package com.example.mealway.screen.homeactivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.mealway.R;
import com.example.mealway.screen.common.NoInternetFragment;
import com.example.mealway.screen.homeactivity.view.HomeContentFragment;
import com.example.mealway.screen.favorite.view.FavoriteFragment;
import com.example.mealway.screen.search.view.SearchFragment;
import com.example.mealway.utils.NetworkMonitor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout noInternetContainer;
    private NetworkMonitor networkMonitor;
    private Fragment currentContentFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bottomNavigationView = view.findViewById(R.id.home_bottom_nav);
        noInternetContainer = view.findViewById(R.id.no_internet_container); 

        loadContentFragment(new HomeContentFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            if (id == R.id.nav_home) {
                fragment = new HomeContentFragment();
            } else if (id == R.id.nav_favorite) {
                fragment = new FavoriteFragment();
            } else if (id == R.id.nav_search) {
                fragment = new SearchFragment();
            }
            
            if (fragment != null) {
                loadContentFragment(fragment);
                checkNetworkForCurrentFragment(); // Re-check when switching
            }
            return true;
        });

        networkMonitor = new NetworkMonitor(requireContext());
        networkMonitor.observe(getViewLifecycleOwner(), isConnected -> {
            if (currentContentFragment instanceof HomeContentFragment || currentContentFragment instanceof SearchFragment) {
                if (isConnected) {
                    hideNoInternet();
                } else {
                    showNoInternet();
                }
            } else {
                hideNoInternet();
            }
        });

        return view;
    }

    private void loadContentFragment(Fragment fragment) {
        currentContentFragment = fragment;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.home_content_container, fragment);
        transaction.commit();
    }
    
    private void checkNetworkForCurrentFragment() {
        if (networkMonitor.getValue() != null) {
            boolean isConnected = networkMonitor.getValue();
            if (currentContentFragment instanceof HomeContentFragment || currentContentFragment instanceof SearchFragment) {
                if (!isConnected) {
                    showNoInternet();
                } else {
                    hideNoInternet();
                }
            } else {
                hideNoInternet();
            }
        }
    }

    private void showNoInternet() {
        if (noInternetContainer != null && noInternetContainer.getVisibility() != View.VISIBLE) {
            noInternetContainer.setVisibility(View.VISIBLE);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.no_internet_container, new NoInternetFragment());
            transaction.commitAllowingStateLoss();
        }
    }

    private void hideNoInternet() {
        if (noInternetContainer != null && noInternetContainer.getVisibility() == View.VISIBLE) {
            noInternetContainer.setVisibility(View.GONE);
        }
    }
}
