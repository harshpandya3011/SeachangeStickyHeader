package com.seachange.healthandsafty.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ZoneFoundHazardTest {

    @Test
    public void checkDetails() throws Exception {
        ZoneFoundHazard zoneFoundHazard = new ZoneFoundHazard();
        zoneFoundHazard.setTimeStarted("2018-05-24T10:00:00Z");
        assertEquals("Thu, 24 May 2018, 10:00", zoneFoundHazard.getStartDateString());
    }
}
