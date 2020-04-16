package com.seachange.healthandsafty;

import android.content.Intent;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.fragment.app.FragmentTransaction;

import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import com.seachange.healthandsafty.model.HazardType;
import com.seachange.healthandsafty.activity.HazardSourceActivity;
import com.seachange.healthandsafty.fragment.HazardSourceFragment;
import com.seachange.healthandsafty.utils.UtilStrings;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by kevinsong on 11/01/2018.
 */

public class HarzardSourceFragmentTest {

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<HazardSourceActivity>(
            HazardSourceActivity.class, true, true){
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getContext(),HazardSourceActivity.class);
            intent.putExtra(UtilStrings.ZONE_ID,1);
            return intent;
        }
    };

    @Test
    public void fragment_can_be_instantiated() {
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HazardSourceFragment mFragment = startFragment();
                ArrayList<HazardType> mHazardTypes = new ArrayList<>();
                HazardType type = new HazardType();
                type.setSelected(true);
                HazardType type2 = new HazardType();
                type2.setSelected(true);
                HazardType type3 = new HazardType();
                type3.setSelected(true);
                HazardType type4 = new HazardType();
                type4.setSelected(false);
                HazardType type5 = new HazardType();
                type5.setSelected(true);
                HazardType type6 = new HazardType();
                type6.setSelected(false);

                mHazardTypes.add(type);
                mHazardTypes.add(type2);
                mHazardTypes.add(type3);
                mHazardTypes.add(type4);
                mHazardTypes.add(type5);
                mHazardTypes.add(type6);
                mFragment.mHazardTypes = mHazardTypes;
            }
        });


        UtilTest.sleepAction(1000);

        onView(withId(R.id.hazard_source_recent)).perform(RecyclerViewActions.scrollToPosition(2));
        UtilTest.sleepAction(1000);

        // Then use Espresso to test the Fragment
        onView(withId(R.id.hazard_source_framelayout)).check(matches(isDisplayed()));
        onView(withId(R.id.hazard_source_recent)).perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));
        onView(withId(R.id.source_add_hazard)).check(matches(isEnabled()));

        UtilTest.sleepAction(1000);

        onView(withId(R.id.source_add_hazard)).perform(click());

    }

    private HazardSourceFragment startFragment() {
        HazardSourceActivity activity = (HazardSourceActivity) activityRule.getActivity();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        HazardSourceFragment mFragment = new HazardSourceFragment();
        transaction.add(mFragment, "test");
        transaction.commit();
        return mFragment;
    }
}
