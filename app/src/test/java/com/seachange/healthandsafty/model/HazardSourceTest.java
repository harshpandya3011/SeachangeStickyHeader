package com.seachange.healthandsafty.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class HazardSourceTest {

    @Test
    public void testName() {
        HazardSource hazardSource = new HazardSource();
        hazardSource.setSource_name("name");
        assertEquals("name", hazardSource.getSource_name());
    }

    @Test
    public void testId() {
        HazardSource hazardSource = new HazardSource();
        hazardSource.setSource_id("namedsgfdgs");
        assertEquals("namedsgfdgs", hazardSource.getSource_id());
    }

    @Test
    public void testHazardTypes() {
        HazardSource hazardSource = new HazardSource();
        ArrayList<HazardType> tmp = new ArrayList<>();
        hazardSource.setHazardTypes(tmp);
        assertEquals(tmp, hazardSource.getHazardTypes());
    }
}
