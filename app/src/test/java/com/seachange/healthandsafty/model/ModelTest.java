package com.seachange.healthandsafty.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by kevinsong on 26/10/2017.
 */

public class ModelTest {

    @Test
    public void checkTimeTest() throws Exception {
        CheckTime checkTime = new CheckTime();
        checkTime.setCheck_date("2017-10-13T13:30:00+00:00");
        assertEquals("13:30", checkTime.getCheckTime());
    }

    @Test
    public void checkTimeTotalHarzardsTest() throws Exception {
        CheckTime checkTime = new CheckTime();
        ArrayList<SiteZone> zones = new ArrayList<>();

        for (int i=0; i<5;i++) {
            SiteZone temp = new SiteZone();
            temp.setHazards(i);
            zones.add(temp);
        }
        checkTime.setScheduledCheckses(zones);
        assertEquals(10, checkTime.getTotalHazards());
    }

    @Test
    public void checkSiteCheck() throws Exception {
        SiteCheck siteCheck = new SiteCheck();
        siteCheck.setDate("2017-10-13T13:30:00+00:00");
        assertEquals("October 13, 2017", siteCheck.getTime());
    }

    @Test
    public void checkSiteCheckToday() throws Exception {
        SiteCheck siteCheck = new SiteCheck();
        siteCheck.setDate("2017-10-13T13:30:00+00:00");
        assertEquals(false, siteCheck.isToday());
    }
}
