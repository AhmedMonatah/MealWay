package com.example.mealway.screen.onboarding.presenter;

import android.content.Context;
import android.view.View;

import com.example.mealway.R;
import com.example.mealway.data.model.OnboardingItem;
import com.example.mealway.screen.onboarding.view.OnboardingView;

import java.util.ArrayList;
import java.util.List;

public class OnboardingPresenterImpl implements OnboardingPresenter {

    private OnboardingView view;
    private Context context;

    public OnboardingPresenterImpl(OnboardingView view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void getOnboardingData() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();
        
        int meal1 = R.drawable.meal_1;
        int meal2 = R.drawable.meal_2;
        int meal3 = R.drawable.meal_3;

        onboardingItems.add(new OnboardingItem(
                context.getString(R.string.onboarding_title_1),
                context.getString(R.string.onboarding_desc_1),
                meal1 != 0 ? meal1 : R.drawable.ic_launcher_background
        ));

        onboardingItems.add(new OnboardingItem(
                context.getString(R.string.onboarding_title_2),
                context.getString(R.string.onboarding_desc_2),
                meal2 != 0 ? meal2 : R.drawable.ic_launcher_background
        ));

        onboardingItems.add(new OnboardingItem(
                context.getString(R.string.onboarding_title_3),
                context.getString(R.string.onboarding_desc_3),
                meal3 != 0 ? meal3 : R.drawable.ic_launcher_background
        ));


        view.showOnboardingItems(onboardingItems);
    }

    @Override
    public void handleNextClick(int currentItem, int itemCount) {
        if (currentItem < itemCount - 1) {
            view.navigateToNextPage();
        } else {
            view.navigateToLogin();
        }
    }

    @Override
    public void handleSkipClick() {
        view.navigateToLogin();
    }

    @Override
    public void handlePageSelected(int position, int itemCount) {
        if (position == itemCount - 1) {
            view.updateNextButtonText("Start");
            view.updateSkipButtonVisibility(View.INVISIBLE);
        } else {
            view.updateNextButtonText("Next");
            view.updateSkipButtonVisibility(View.VISIBLE);
        }
    }
}
