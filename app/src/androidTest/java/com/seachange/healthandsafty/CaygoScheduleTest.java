package com.seachange.healthandsafty;

import android.content.Intent;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.fragment.app.FragmentTransaction;

import org.junit.Rule;
import org.junit.Test;


import com.seachange.healthandsafty.activity.CaygoHomeActivity;
import com.seachange.healthandsafty.fragment.ScheduleFragment;
import com.seachange.healthandsafty.utils.UtilStrings;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

/**
 * Created by kevinsong on 25/01/2018.
 */

public class CaygoScheduleTest {

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
                ScheduleFragment mFragment = startFragment();
            }
        });

        UtilTest.sleepAction(1000);

        onView(withId(R.id.navigation_time)).perform(click());
        UtilTest.sleepAction(1000);

        onView(withId(R.id.expandableListViewlayout)).perform(UtilTest.swipeUp());
        UtilTest.sleepAction(1000);

        onView(withId(R.id.expandableListViewlayout)).perform(UtilTest.swipeDown());
        UtilTest.sleepAction(1000);

        onData(anything()).inAdapterView(withId(R.id.expandableListViewlayout)).atPosition(1).perform(click());
        UtilTest.sleepAction(1000);

        onData(anything()).inAdapterView(withId(R.id.expandableListViewlayout)).atPosition(1).perform(click());
        UtilTest.sleepAction(1000);

        onData(anything()).inAdapterView(withId(R.id.expandableListViewlayout)).atPosition(2).perform(click());
        UtilTest.sleepAction(1000);

        onData(anything()).inAdapterView(withId(R.id.expandableListViewlayout)).atPosition(2).perform(click());
        UtilTest.sleepAction(1000);
    }


    private ScheduleFragment startFragment() {
        CaygoHomeActivity activity = (CaygoHomeActivity) activityRule.getActivity();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        ScheduleFragment mFragment = new ScheduleFragment();
        transaction.add(mFragment, "test");
        transaction.commit();
        return mFragment;
    }
}
