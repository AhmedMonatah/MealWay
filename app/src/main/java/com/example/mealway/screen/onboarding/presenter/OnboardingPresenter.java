package com.example.mealway.screen.onboarding.presenter;

public interface OnboardingPresenter {
    void getOnboardingData();
    void handleNextClick(int currentItem, int itemCount);
    void handleSkipClick();
    void handlePageSelected(int position, int itemCount);
}
