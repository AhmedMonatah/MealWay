package com.example.mealway.screen.onboarding.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mealway.R;
import com.example.mealway.data.model.OnboardingItem;
import com.example.mealway.data.repository.AuthRepository;
import com.example.mealway.data.repository.AuthRepositoryImpl;
import com.example.mealway.screen.onboarding.presenter.OnboardingPresenter;
import com.example.mealway.screen.onboarding.presenter.OnboardingPresenterImpl;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class OnboardingFragment extends Fragment implements OnboardingView {

    private ViewPager2 viewPagerOnboarding;
    private TabLayout tabLayoutIndicator;
    private TextView buttonNext; 
    private TextView textSkip;
    private OnboardingAdapter onboardingAdapter;
    private OnboardingPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPagerOnboarding = view.findViewById(R.id.viewPagerOnboarding);
        tabLayoutIndicator = view.findViewById(R.id.tabLayoutIndicator);
        buttonNext = view.findViewById(R.id.buttonNext);
        textSkip = view.findViewById(R.id.textSkip);

        presenter = new OnboardingPresenterImpl(this, requireContext());
        
        setupViewPager();
        setupListeners();
        
        presenter.getOnboardingData();

        viewPagerOnboarding.setAlpha(0f);
        viewPagerOnboarding.animate().alpha(1f).setDuration(1200).start();
        
        tabLayoutIndicator.setAlpha(0f);
        tabLayoutIndicator.setTranslationY(30f);
        tabLayoutIndicator.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(400).start();
        
        buttonNext.setAlpha(0f);
        buttonNext.animate().alpha(1f).setDuration(800).setStartDelay(600).start();
        
        textSkip.setAlpha(0f);
        textSkip.animate().alpha(1f).setDuration(800).setStartDelay(600).start();
    }
    
    private void setupViewPager() {
        viewPagerOnboarding.setOffscreenPageLimit(1);

        viewPagerOnboarding.setPageTransformer((page, position) -> {
            View imageContainer = page.findViewById(R.id.imageContainer);
            View textTitle = page.findViewById(R.id.textTitle);
            View textDescription = page.findViewById(R.id.textDescription);
            View deco1 = page.findViewById(R.id.decoCircle1);
            View deco2 = page.findViewById(R.id.decoCircle2);

            float absPos = Math.abs(position);

            if (imageContainer != null) {
                float scale = 0.8f + (0.2f * (1f - absPos));
                imageContainer.setScaleX(scale);
                imageContainer.setScaleY(scale);
                imageContainer.setAlpha(1f - absPos);
                imageContainer.setRotation(position * -20);
            }
            
            if (textTitle != null) {
                textTitle.setAlpha(1f - absPos);
                textTitle.setTranslationX(position * 300f);
            }

            if (textDescription != null) {
                textDescription.setAlpha(1f - (absPos * 1.2f));
                textDescription.setTranslationX(position * 500f);
            }

            if (deco1 != null) {
                deco1.setTranslationX(position * -400f);
                deco1.setAlpha(0.6f - (absPos * 0.4f));
            }

            if (deco2 != null) {
                deco2.setTranslationX(position * -200f);
                deco2.setAlpha(0.6f - (absPos * 0.4f));
            }
        });

    }

    private void setupListeners() {
        buttonNext.setOnClickListener(v -> {
            if (onboardingAdapter != null) {
                presenter.handleNextClick(
                        viewPagerOnboarding.getCurrentItem(), 
                        onboardingAdapter.getItemCount()
                );
            }
        });

        textSkip.setOnClickListener(v -> presenter.handleSkipClick());

        viewPagerOnboarding.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (onboardingAdapter != null) {
                    presenter.handlePageSelected(position, onboardingAdapter.getItemCount());
                }
            }
        });
    }

    @Override
    public void showOnboardingItems(List<OnboardingItem> items) {
        onboardingAdapter = new OnboardingAdapter(items);
        viewPagerOnboarding.setAdapter(onboardingAdapter);
        
        new TabLayoutMediator(tabLayoutIndicator, viewPagerOnboarding, (tab, position) -> {
            // Dots
        }).attach();
    }

    @Override
    public void navigateToNextPage() {
        viewPagerOnboarding.setCurrentItem(viewPagerOnboarding.getCurrentItem() + 1);
    }

    @Override
    public void navigateToLogin() {
        if (getContext() != null) {
          AuthRepository repository = new AuthRepositoryImpl(requireContext());
            repository.setOnboardingCompleted(true);

            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_onboarding_to_login); 
        }
    }

    @Override
    public void updateNextButtonText(String text) {
        buttonNext.setText(text);
        if (text.equalsIgnoreCase("Start")) {
             buttonNext.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start();
        } else {
             buttonNext.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
        }
    }

    @Override
    public void updateSkipButtonVisibility(int visibility) {
        if (visibility == View.INVISIBLE) {
            textSkip.animate().alpha(0f).setDuration(200).withEndAction(() -> {
                if (textSkip != null) textSkip.setVisibility(View.INVISIBLE);
            }).start();
        } else {
            textSkip.setVisibility(View.VISIBLE);
            textSkip.animate().alpha(1f).setDuration(200).start();
        }
    }
}
