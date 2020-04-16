//package com.seachange.healthandsafty;
//
//import android.test.ActivityInstrumentationTestCase2;
//import android.widget.TextView;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//
//import com.seachange.healthandsafty.activity.CaygoZoneCheckActivity;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//
///**
// * Created by kevinsong on 11/01/2018.
// */
//
//public class StartCheckActivityTest extends ActivityInstrumentationTestCase2<CaygoZoneCheckActivity> {
//
//    private CaygoZoneCheckActivity mActivity;
//    private TextView mTimeView;
//
//    public StartCheckActivityTest() {
//        super(CaygoZoneCheckActivity.class);
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        mActivity = getActivity();
//        mTimeView = mActivity.findViewById(R.id.tour_time);
//        mActivity.getDateString();
//    }
//
//    public void testPreconditions() {
//        assertNotNull("mActivity is null", mActivity);
//        assertNotNull("mTimeView is null", mTimeView);
//    }
//
//
//    public void testDateString() {
//        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
//        String time = sdf.format(Calendar.getInstance().getTime());
//
//        String tmp = mActivity.getDateString();
//        assertEquals(time, tmp);
//    }
//
//    public void testDate() {
//
//        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
//        String time = sdf.format(Calendar.getInstance().getTime());
//
//        onView(withId(R.id.tour_time))
//                .check(matches(withText(time)));
//
//        onView(withId(R.id.add_hazard)).check(matches(isEnabled()));
//        onView(withId(R.id.end_tour)).check(matches(isEnabled()));
//        onView(withId(R.id.end_tour)).check(matches(isEnabled()));
//        onView(withId(R.id.tour_time)).check(matches(isDisplayed()));
//
////        onView(withId(R.id.tour_hazard_count))
////                .check(matches(withText("0")));
//
//
//
//        onView(withId(R.id.end_tour))
//                .perform(click());
//
//    }
//}
