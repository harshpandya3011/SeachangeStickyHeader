//package com.seachange.healthandsafty;
//
//import android.test.ActivityInstrumentationTestCase2;
//import android.widget.Button;
//
//
//import com.seachange.healthandsafty.activity.LoginActivity;
//
//import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//
///**
// * Created by kevinsong on 04/01/2018.
// */
//
//public class LoginActivityTest extends
//        ActivityInstrumentationTestCase2<LoginActivity> {
//
//    private LoginActivity mActivity;
//    private ExtendedEditText mEmail, mPassword;
//    private Button mShowButton;
//    private String mStringToBetyped;
//
//    public LoginActivityTest() {
//        super(LoginActivity.class);
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        mActivity = getActivity();
//        mPassword = mActivity.findViewById(R.id.password);
//        mEmail = mActivity.findViewById(R.id.email);
//        mShowButton = mActivity.findViewById(R.id.login_show_button);
//
//        addEmail("kevin.song@seachange-intl.com");
//        addPassword("11111111");
//        mStringToBetyped = "kevin.song@seachange-intl.com";
//
//    }
//
//    public void testPreconditions() {
//
//        assertNotNull("mActivity is null", mActivity);
//        assertNotNull("mPassword is null", mPassword);
//        assertNotNull("mEmail is null", mEmail);
//    }
//
//    public void testShowButton() {
////        showButtonClicked();
//        onView(withId(R.id.login_show_button)).perform(click());
//
//        assertEquals(true, mActivity.getPasswordShowing());
//        assertEquals(true, mActivity.isPasswordValid(mPassword.getText().toString()));
//
//    }
//
//    public void testEspressoActivity() {
//
////        onView(withId(R.id.email))
////                .perform(typeText(mStringToBetyped), closeSoftKeyboard());
//        onView(withId(R.id.password)).perform(click());
//        onView(withId(R.id.email))
//                .check(matches(withText(mStringToBetyped)));
//    }
//
//    private void showButtonClicked(){
//        getInstrumentation().runOnMainSync(new Runnable() {
//            @Override
//            public void run() {
//                mShowButton.performClick();
//            }
//        });
//        getInstrumentation().waitForIdleSync();
//        getInstrumentation().waitForIdleSync();
//    }
//
//    private void addEmail(final String email) {
//        // Send string input value
//        getInstrumentation().runOnMainSync(new Runnable() {
//            @Override
//            public void run() {
//                mEmail.requestFocus();
//            }
//        });
//        getInstrumentation().waitForIdleSync();
//        getInstrumentation().sendStringSync(email);
//        getInstrumentation().waitForIdleSync();
//    }
//
//    private void addPassword(final String password) {
//        // Send string input value
//        getInstrumentation().runOnMainSync(new Runnable() {
//            @Override
//            public void run() {
//                mPassword.requestFocus();
//            }
//        });
//        getInstrumentation().waitForIdleSync();
//        getInstrumentation().sendStringSync(password);
//        getInstrumentation().waitForIdleSync();
//    }
//}
