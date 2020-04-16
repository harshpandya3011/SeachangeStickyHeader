package com.seachange.healthandsafty.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScheduleSettingTest {

    @Test
    public void testZoneId() {
        ScheduleSetting scheduleSetting = new ScheduleSetting();
        scheduleSetting.setZoneId(401);
        assertEquals(401, scheduleSetting.getZoneId());
    }

    @Test
    public void testZoneName() {
        ScheduleSetting scheduleSetting = new ScheduleSetting();
        scheduleSetting.setName("Checkout");
        assertEquals("Checkout", scheduleSetting.getName());
    }

    @Test
    public void testZoneRegularSchedule() {
        ScheduleSetting scheduleSetting = new ScheduleSetting();
        scheduleSetting.setHasRegularSchedule(true);
        assertTrue(scheduleSetting.isHasRegularSchedule());
    }

    @Test
    public void testZoneSchedule() {
        ScheduleSetting scheduleSetting = new ScheduleSetting();

        ArrayList<Schedule> schedules = new ArrayList<>();
        scheduleSetting.setSchedules(schedules);
        assertEquals(schedules, scheduleSetting.getSchedules());
    }
}
