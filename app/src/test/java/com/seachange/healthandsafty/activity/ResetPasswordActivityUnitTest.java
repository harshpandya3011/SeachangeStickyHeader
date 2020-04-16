package com.seachange.healthandsafty.activity;

import com.seachange.healthandsafty.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ResetPasswordActivityUnitTest {

    private ResetPasswordActivity activity;
    private ExtendedEditText mPassword, mEmail;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(ResetPasswordActivity.class);
    }

    @Test
    public void checkValidation() {
        assertEquals(true, activity.checkError());
    }
}
