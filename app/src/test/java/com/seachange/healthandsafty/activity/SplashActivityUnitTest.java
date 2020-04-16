package com.seachange.healthandsafty.activity;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class SplashActivityUnitTest {

    private SplashActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(SplashActivity.class);
    }

    @Test
    public void showShowLogin() {
        assertEquals(true, activity.checkTokenShowLogin());
    }
}
