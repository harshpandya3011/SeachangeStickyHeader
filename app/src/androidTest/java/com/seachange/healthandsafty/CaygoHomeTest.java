package com.seachange.healthandsafty;

import android.content.Intent;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.fragment.app.FragmentTransaction;

import org.junit.Rule;
import org.junit.Test;

import com.seachange.healthandsafty.activity.CaygoHomeActivity;
import com.seachange.healthandsafty.fragment.HomeFragment;
import com.seachange.healthandsafty.utils.UtilStrings;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by kevinsong on 25/01/2018.
 */

public class CaygoHomeTest {

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<CaygoHomeActivity>(
            CaygoHomeActivity.class, true, true){
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getContext(),CaygoHomeActivity.class);
            intent.putExtra(UtilStrings.ZONE_ID,1);
            intent.putExtra(UtilStrings.MANAGER_HOME, true);
            return intent;
        }
    };

    @Test
    public void fragment_can_be_instantiated() {
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HomeFragment mFragment = startFragment();
            }
        });

        UtilTest.sleepAction(1000);

        onView(withId(R.id.navigation_time)).perform(click());
        UtilTest.sleepAction(1000);

        onView(withId(R.id.navigation_home)).perform(click());
        UtilTest.sleepAction(1000);

        onView(withId(R.id.manager_home_tour_view)).check(matches(isDisplayed()));
        onView(withId(R.id.manager_home_tour_view)).perform(click());
        UtilTest.sleepAction(1000);

        onView(withId(R.id.home_scroll)).perform(UtilTest.swipeUp());
        UtilTest.sleepAction(1000);

        onView(withId(R.id.home_scroll)).perform(UtilTest.swipeDown());
        UtilTest.sleepAction(1000);

    }

    @Test
    public void startCheck() {

        UtilTest.sleepAction(1000);
        onView(withId(R.id.home_scroll)).perform(UtilTest.swipeUp());
//        onView(withId(R.id.home_list)).perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));
    }


    private HomeFragment startFragment() {
        CaygoHomeActivity activity = (CaygoHomeActivity) activityRule.getActivity();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        HomeFragment mFragment = new HomeFragment();
        transaction.add(mFragment, "test");
        transaction.commit();
        return mFragment;
    }
}
