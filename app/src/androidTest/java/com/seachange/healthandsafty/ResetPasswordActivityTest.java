//package com.seachange.healthandsafty;
//
//import android.test.ActivityInstrumentationTestCase2;
//
//import com.seachange.healthandsafty.activity.ResetPasswordActivity;
//import studio.carbonylgroup.textfieldboxes.ExtendedEditText;
//import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;
//
///**
// * Created by kevinsong on 04/01/2018.
// */
//
//public class ResetPasswordActivityTest extends
//        ActivityInstrumentationTestCase2<ResetPasswordActivity> {
//
//    private ResetPasswordActivity mActivity;
//    private ExtendedEditText mPassword, mPasswordConfirm;
//    private TextFieldBoxes mPasswordBox;
//
//    public ResetPasswordActivityTest() {
//        super(ResetPasswordActivity.class);
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        mActivity = getActivity();
//        mPassword = mActivity.findViewById(R.id.password_reset);
//        mPasswordConfirm = mActivity.findViewById(R.id.password_reset_confirm);
//        mPasswordBox = mActivity.findViewById(R.id.password_reset_text_box);
//
//    }
//
//    public void testPreconditions() {
//
//        assertNotNull("mActivity is null", mActivity);
//        assertNotNull("mPassword is null", mPassword);
//        assertNotNull("mPasswordConfirm is null", mPasswordConfirm);
//    }
//
//    public void testCheckPasswordValidation() {
//        addPassword("11111111");
//        addPasswordConfirm("11111111");
//        assertEquals(false,  mActivity.checkError());
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
//
//    private void addPasswordConfirm(final String password) {
//        // Send string input value
//        getInstrumentation().runOnMainSync(new Runnable() {
//            @Override
//            public void run() {
//                mPasswordConfirm.requestFocus();
//            }
//        });
//        getInstrumentation().waitForIdleSync();
//        getInstrumentation().sendStringSync(password);
//        getInstrumentation().waitForIdleSync();
//    }
//}
