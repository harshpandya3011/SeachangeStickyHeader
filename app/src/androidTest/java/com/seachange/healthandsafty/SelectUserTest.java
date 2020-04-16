package com.seachange.healthandsafty;

import android.content.Intent;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.ScrollToAction;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.NestedScrollView;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;


import com.seachange.healthandsafty.activity.SelectUserActivity;
import com.seachange.healthandsafty.fragment.SelectUserFragment;
import com.seachange.healthandsafty.utils.UtilStrings;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;

/**
 * Created by kevinsong on 25/01/2018.
 */

public class SelectUserTest {

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<SelectUserActivity>(
            SelectUserActivity.class, true, true) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getContext(), SelectUserActivity.class);
            intent.putExtra(UtilStrings.ZONE_ID, 1);
            return intent;
        }
    };

    @Test
    public void fragment_can_be_instantiated() {
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SelectUserFragment mFragment = startFragment();
            }
        });

        onView(withId(R.id.userRecyclyerView)).check(matches(isDisplayed()));

        UtilTest.sleepAction(1000);
        onView(withId(R.id.select_user_scroll)).perform(swipeUp());

    }

    @Test
    public void fragment_login() {

        UtilTest.sleepAction(1000);
        onView(withId(R.id.select_user_scroll)).perform(swipeUp());
    }


    @Test
    public void fragment_scan() {
        onView(withId(R.id.select_user_scroll)).perform(swipeUp());
    }


    private static ViewAction swipeUp() {
        return new GeneralSwipeAction(Swipe.SLOW, GeneralLocation.BOTTOM_CENTER, GeneralLocation.TOP_CENTER, Press.FINGER);
    }

    private SelectUserFragment startFragment() {
        SelectUserActivity activity = (SelectUserActivity) activityRule.getActivity();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        SelectUserFragment mFragment = new SelectUserFragment();
        transaction.add(mFragment, "test");
        transaction.commit();
        return mFragment;
    }

    ViewAction customScrollTo = new ViewAction() {

        @Override
        public Matcher<View> getConstraints() {
            return allOf(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), isDescendantOfA(anyOf(
                    isAssignableFrom(ScrollView.class),
                    isAssignableFrom(HorizontalScrollView.class),
                    isAssignableFrom(NestedScrollView.class)))
            );
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public void perform(UiController uiController, View view) {
            new ScrollToAction().perform(uiController, view);
        }
    };
}
