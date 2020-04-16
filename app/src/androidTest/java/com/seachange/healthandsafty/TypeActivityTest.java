package com.seachange.healthandsafty;

import android.content.Intent;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.fragment.app.FragmentTransaction;

import org.junit.Rule;
import org.junit.Test;
import org.parceler.Parcels;

import java.util.ArrayList;

import com.seachange.healthandsafty.model.HazardSource;
import com.seachange.healthandsafty.model.HazardType;
import com.seachange.healthandsafty.activity.HazardTypeActivity;
import com.seachange.healthandsafty.fragment.HazardTypeFragment;
import com.seachange.healthandsafty.utils.UtilStrings;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by kevinsong on 11/01/2018.
 */

public class TypeActivityTest {

    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<HazardTypeActivity>(
            HazardTypeActivity.class, true, true){

        @Override
        protected Intent getActivityIntent() {


            //mock data for type page
            HazardSource source = new HazardSource();
            source.setSource_name("Obstruction");

            ArrayList<HazardType> mHazardTypes = new ArrayList<>();
            HazardType type = new HazardType();
            type.setSelected(false);
            type.setCategory("Obstruction");
            type.setType_name("Stock");

            HazardType type2 = new HazardType();
            type2.setSelected(false);
            type2.setCategory("Obstruction");
            type2.setType_name("Basket");

            HazardType type3 = new HazardType();
            type3.setSelected(false);
            type3.setCategory("Obstruction");
            type3.setType_name("Display");

            HazardType type4 = new HazardType();
            type4.setSelected(false);
            type4.setCategory("Obstruction");
            type4.setType_name("Trolley");

            HazardType type5 = new HazardType();
            type5.setSelected(false);
            type5.setCategory("Obstruction");
            type5.setType_name("Furniture");

            HazardType type6 = new HazardType();
            type6.setSelected(false);
            type6.setCategory("Obstruction");
            type6.setType_name("Clutter");

            mHazardTypes.add(type);
            mHazardTypes.add(type2);
            mHazardTypes.add(type3);
            mHazardTypes.add(type4);
            mHazardTypes.add(type5);
            mHazardTypes.add(type6);
            source.setHazardTypes(mHazardTypes);

            Intent intent = new Intent(InstrumentationRegistry.getContext(), HazardTypeActivity.class);
            intent.putExtra(UtilStrings.OBJECT_HAZARD_SOURCE, Parcels.wrap(source));
            return intent;
        }
    };


    @Test
    public void fragment_can_be_instantiated() {
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HazardTypeFragment mFragment = startVoiceFragment();
            }
        });

        // Then use Espresso to test the Fragment
        onView(withId(R.id.hazard_type_framelayout)).check(matches(isDisplayed()));
        onView(withId(R.id.type_add_button)).check(matches(isDisplayed()));

        UtilTest.sleepAction(1000);

        onView(withId(R.id.hazard_category_title)).check(matches(withText("Hazard Category")));
        onView(withId(R.id.source_type_content_title)).check(matches(withText("Obstruction")));

    }


    @Test
    public void typeClicked() {

        UtilTest.sleepAction(1000);

        onView(withId(R.id.hazard_type_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.type_selected_hazards)).check(matches(withText("1 Selected")));

        UtilTest.sleepAction(1000);

        onView(withId(R.id.hazard_type_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.type_selected_hazards)).check(matches(withText("2 Selected")));

        UtilTest.sleepAction(1000);

        onView(withId(R.id.hazard_type_list)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.type_selected_hazards)).check(matches(withText("3 Selected")));

        onView(withId(R.id.hazard_type_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.type_selected_hazards)).check(matches(withText("2 Selected")));
        onView(withId(R.id.type_add_button)).check(matches(isEnabled()));

    }

    private HazardTypeFragment startVoiceFragment() {
        HazardTypeActivity activity = (HazardTypeActivity) activityRule.getActivity();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        HazardTypeFragment voiceFragment = new HazardTypeFragment();
        transaction.add(voiceFragment, "test");
        transaction.commit();
        return voiceFragment;
    }

}
