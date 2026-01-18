package com.example.mealway.screen.homeactivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mealway.R;
import com.example.mealway.screen.favorite.FavoriteFragment;
import com.example.mealway.screen.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bottomNavigationView = view.findViewById(R.id.home_bottom_nav);

        loadContentFragment(new HomeContentFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadContentFragment(new HomeContentFragment());
            } else if (id == R.id.nav_favorite) {
                loadContentFragment(new FavoriteFragment());
            } else if (id == R.id.nav_search) {
                loadContentFragment(new SearchFragment());
            }
            return true;
        });

        return view;
    }

    private void loadContentFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.home_content_container, fragment);
        transaction.commit();
    }
}
