package com.seachange.healthandsafty.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZoneCheckTimeRangeTest {

    @Test
    public void testStartTime() {
        ZoneCheckTimeRange zoneCheckTimeRange = new ZoneCheckTimeRange();
        zoneCheckTimeRange.setStartTime("11/11/2019");
        assertEquals("11/11/2019", zoneCheckTimeRange.getStartTime());
    }

    @Test
    public void testEndTime() {
        ZoneCheckTimeRange zoneCheckTimeRange = new ZoneCheckTimeRange();
        zoneCheckTimeRange.setEndTime("11/11/2019");
        assertEquals("11/11/2019", zoneCheckTimeRange.getEndTime());
    }

    @Test
    public void testIntervalInMinutes() {
        ZoneCheckTimeRange zoneCheckTimeRange = new ZoneCheckTimeRange();
        zoneCheckTimeRange.setIntervalInMinutes(30);
        assertEquals(30, (int)zoneCheckTimeRange.getIntervalInMinutes());
    }

    @Test
    public void testTimeToCompleteInMinutes() {
        ZoneCheckTimeRange zoneCheckTimeRange = new ZoneCheckTimeRange();
        zoneCheckTimeRange.setTimeToCompleteInMinutes(30);
        assertEquals(30, (int)zoneCheckTimeRange.getTimeToCompleteInMinutes());
    }

    @Test
    public void testEndTimeIsNextDay() {
        ZoneCheckTimeRange zoneCheckTimeRange = new ZoneCheckTimeRange();
        zoneCheckTimeRange.setEndTimeIsNextDay(true);
        assertTrue(zoneCheckTimeRange.isEndTimeIsNextDay());
    }
}
