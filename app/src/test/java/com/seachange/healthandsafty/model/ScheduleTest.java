package com.seachange.healthandsafty.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class ScheduleTest {

    @Test
    public void testDayOfWeek() {
        Schedule schedule = new Schedule();
        schedule.setDayOfWeek(2);
        assertEquals(2, (int)schedule.getDayOfWeek());
    }

    @Test
    public void testDate() {
        Schedule schedule = new Schedule();
        schedule.setDate("11/11/2019");
        assertEquals("11/11/2019", schedule.getDate());
    }

    @Test
    public void testZoneCheckTimeRange() {
        Schedule schedule = new Schedule();
        ArrayList<ZoneCheckTimeRange> zoneCheckTimeRanges = new ArrayList<>();
        schedule.setZoneCheckTimeRanges(zoneCheckTimeRanges);
        assertEquals(zoneCheckTimeRanges, schedule.getZoneCheckTimeRanges());
    }
}
