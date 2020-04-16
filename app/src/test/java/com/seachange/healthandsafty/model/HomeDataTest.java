package com.seachange.healthandsafty.model;

import com.seachange.healthandsafty.helper.HomeData;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class HomeDataTest {

    @Test
    public void checkTodayChecks() {

        HomeData data = new HomeData();
        ArrayList<SiteCheck> siteChecks = new ArrayList<>();
        SiteCheck tmp = new SiteCheck();
        tmp.setDate("2018-11-20T00:00:00Z");
        SiteCheck c = data.getTodayCheck(siteChecks);
        assertNull(c);
    }

    @Test
    public void getCheckTimeFinished() {
        HomeData data = new HomeData();
        ArrayList<SiteZone> siteChecks = new ArrayList<>();
        SiteZone c = new SiteZone();
        c.setChecked(false);
        siteChecks.add(c);
        boolean result = data.checkTimeFinished(siteChecks);
        assertFalse(result);
    }

    @Test
    public void currentCheckStatus() {
        HomeData data = new HomeData();
        CheckTime checkTime = new CheckTime();
        checkTime.setStart_time("2017-10-13T13:30:00+00:00");
        checkTime.setEnd_time("2017-10-13T14:30:00+00:00");
        checkTime.processCheckTime();
        assertFalse(data.isCheckLive(checkTime));
    }
}
