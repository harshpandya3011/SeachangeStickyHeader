package com.seachange.healthandsafty.activity;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class BaseActivityUnitTest {

    private BaseActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(SplashActivity.class);
    }

    @Test
    public void showDateTimeDialog() {

        MaterialDialog dialog = activity.showDateTimeDialog();

        // Key part 1 : simulate button click in unit test
        MDButton confirm = dialog.getActionButton(DialogAction.POSITIVE);
        confirm.performClick();

        // Key part 2 : Check that startActivityForResult is invoke
        ShadowActivity shadowActivity = shadowOf(activity);
        ShadowActivity.IntentForResult intentForResult = shadowActivity.getNextStartedActivityForResult();

        // assert that the proper request to start activity is sent
        String action = intentForResult.intent.getAction();
        assertEquals("android.settings.DATE_SETTINGS", action);
    }
}
