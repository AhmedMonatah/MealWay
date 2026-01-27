package com.example.mealway.screen.onboarding.view;

import com.example.mealway.data.model.OnboardingItem;
import java.util.List;

public interface OnboardingView {
    void showOnboardingItems(List<OnboardingItem> items);
    void navigateToNextPage();
    void navigateToLogin();
    void updateNextButtonText(String text);
    void updateSkipButtonVisibility(int visibility);
}
