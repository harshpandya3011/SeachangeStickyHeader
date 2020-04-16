package com.seachange.healthandsafty.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatsTest {

    @Test
    public void testMinTime() {
        Stats  stats = new Stats();
        stats.setMinTime("111111");
        assertEquals("111111", stats.getMinTime());
    }

    @Test
    public void testMaxTime() {
        Stats  stats = new Stats();
        stats.setMaxTime("222");
        assertEquals("222", stats.getMaxTime());
    }

    @Test
    public void testHazards() {
        Stats  stats = new Stats();
        stats.setNumberOfHazards(12);
        assertEquals(12, (int)stats.getNumberOfHazards());
    }
}
