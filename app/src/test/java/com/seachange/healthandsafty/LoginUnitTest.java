package com.seachange.healthandsafty;

import android.widget.Button;

import com.seachange.healthandsafty.activity.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class LoginUnitTest {


    private LoginActivity activity;
    private ExtendedEditText mPassword, mEmail;
    private Button mShowButton;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(LoginActivity.class);
        mPassword = activity.findViewById(R.id.password);
        mEmail = activity.findViewById(R.id.email);
        mShowButton = activity.findViewById(R.id.login_show_button);

        mEmail.setText("me");
        mPassword.setText("dsf");
    }

    @Test
    public void shouldHideButtonAfterClick() {

        mShowButton.performClick();
        assertTrue(activity.getPasswordShowing());
    }

    @Test
    public void checkPasswordAndEmail() {
//        assertEquals(true, activity.checkEmailAndPassword());
    }

    @Test
    public void validatePassword() {
        assertFalse(activity.isPasswordValid(mPassword.getText().toString()));
    }

    @Test
    public void clickLogin() {
        Button button = activity.findViewById(R.id.email_sign_in_button);
        button.performClick();
        assertFalse(button.isEnabled());
    }

    @Test
    public void clickLoginCheckEmail() {
        Button button = activity.findViewById(R.id.email_sign_in_button);
        button.performClick();
        assertTrue(mEmail.isEnabled());
        assertTrue(mPassword.isEnabled());
    }
}
