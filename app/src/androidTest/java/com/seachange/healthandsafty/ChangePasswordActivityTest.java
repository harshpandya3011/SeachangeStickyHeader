package com.seachange.healthandsafty;

//import android.test.ActivityInstrumentationTestCase2;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.seachange.healthandsafty.activity.ChangePasswordActivity;

import org.junit.Rule;
import org.junit.runner.RunWith;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created by kevinsong on 04/01/2018.
 */
@RunWith(AndroidJUnit4.class)
public class ChangePasswordActivityTest {

    private ChangePasswordActivity mActivity;
    private ExtendedEditText mEditEmail;

    @Rule
    public ActivityTestRule<ChangePasswordActivity> mActivityRule = new ActivityTestRule(ChangePasswordActivity.class);



//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        mActivity = getActivity();
//        mEditEmail = mActivity.findViewById(R.id.email_reset);
//        addEmail("kevin.song@seachange-intl.com");
//    }

    public void testPreconditions() {
        mEditEmail = mActivity.findViewById(R.id.email_reset);
        addEmail("kevin.song@seachange-intl.com");
        assertNotNull("mActivity is null", mActivity);
        assertNotNull("mActivity is null", mEditEmail);
    }

    public void testEmail() {
        final String entry = mEditEmail.getText().toString().trim();
        assertEquals("kevin.song@seachange-intl.com", entry);
    }

    public void testEmailValidation() {
        assertTrue(mActivity.isEmailValid(mEditEmail.getText().toString()));
        assertFalse(mActivity.checkEmailError());

    }

    private void addEmail(final String email) {
        // Send string input value
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mEditEmail.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        getInstrumentation().sendStringSync(email);
        getInstrumentation().waitForIdleSync();
    }

}
