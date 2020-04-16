package com.seachange.healthandsafty.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HazardTypeTest {

    @Test
    public void testName() {
        HazardType hazardType = new HazardType();
        hazardType.setType_name("name");
        assertEquals("name", hazardType.getType_name());
    }

    @Test
    public void testCategoryName() {
        HazardType hazardType = new HazardType();
        hazardType.setCategory("name");
        assertEquals("name", hazardType.getCategory());
    }

    @Test
    public void testRiskName() {
        HazardType hazardType = new HazardType();
        hazardType.setRiskName("slip");
        assertEquals("slip", hazardType.getRiskName());
    }

    @Test
    public void testId() {
        HazardType hazardType = new HazardType();
        hazardType.setType_id("namedsgfdgs");
        assertEquals("namedsgfdgs", hazardType.getType_id());
    }

    @Test
    public void testSelected() {
        HazardType hazardType = new HazardType();
        hazardType.setSelected(true);
        assertTrue(hazardType.isSelected());
    }
}
