package com.seachange.healthandsafty.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class SiteCheckUnitTest {

    @Test
    public void checkDetails() throws Exception {
        SiteCheck siteCheck = new SiteCheck();
        siteCheck.setDate("2018-05-24T00:00:00Z");
        siteCheck.setComplete(false);
        siteCheck.setSiteId(380);
        siteCheck.setNumOfHazardsIdentified(0);
        siteCheck.setStatus(1);
        siteCheck.setSiteTourId("sdfasdrety1sag");
        siteCheck.setTimeStarted("2018-05-24T10:00:00Z");
        siteCheck.setTimeCompleted("2018-05-24T10:30:00Z");
        siteCheck.setTourChecks(new ArrayList<SiteZone>());
        assertEquals(false, siteCheck.isToday());
        assertEquals(false, siteCheck.isComplete());
        assertEquals(380, siteCheck.getSiteId());
        assertEquals(1, siteCheck.getStatus());
        assertEquals("sdfasdrety1sag", siteCheck.getSiteTourId());
    }


    @Test
    public void checkTime() throws Exception {
        SiteCheck siteCheck = new SiteCheck();
        siteCheck.setDate("2018-05-24T00:00:00Z");
        siteCheck.setComplete(false);
        siteCheck.setSiteId(380);
        siteCheck.setNumOfHazardsIdentified(0);
        siteCheck.setStatus(1);
        siteCheck.setSiteTourId("sdfasdrety1sag");
        siteCheck.setTimeStarted("2018-05-24T10:00:00Z");
        siteCheck.setTimeCompleted("2018-05-24T10:30:00Z");
        siteCheck.setTourChecks(new ArrayList<SiteZone>());
        assertEquals("May 24, 2018", siteCheck.getTime());
    }
}
