package com.seachange.healthandsafty.model;

import junit.framework.TestCase;

import org.junit.Assert;

public class ZoneCheckHazardTest extends TestCase {

    private ZoneCheckHazard zoneCheckHazard;

    protected void setUp() throws Exception {
        super.setUp();
        zoneCheckHazard = new ZoneCheckHazard();
    }

    public void testZoneCheckId() {
        String expected = "Aasdfsdf";
        zoneCheckHazard.setZoneCheckId(expected);
        String actual = zoneCheckHazard.getZoneCheckId();
        Assert.assertEquals(expected, actual);
    }

    public void testId() {
        String expected = "Asfdsafdff";
        zoneCheckHazard.setScheduledZoneCheckTimeId(expected);
        String actual = zoneCheckHazard.getScheduledZoneCheckTimeId();
        Assert.assertEquals(expected, actual);
    }

    public void testSiteId() {
        String expected = "380";
        zoneCheckHazard.setSiteTourId(expected);
        String actual = zoneCheckHazard.getSiteTourId();
        Assert.assertEquals(expected, actual);
    }

    public void testHazardId() {
        String expected = "380";
        zoneCheckHazard.setHazardId(expected);
        String actual = zoneCheckHazard.getHazardId();
        Assert.assertEquals(expected, actual);
    }

    public void testTimeFound() {
        String expected = "01/01/2019";
        zoneCheckHazard.setTimeFound(expected);
        String actual = zoneCheckHazard.getTimeFound();
        Assert.assertEquals(expected, actual);
    }

    public void testTypeId() {
        String expected = "adfdsf";
        zoneCheckHazard.setTypeId(expected);
        String actual = zoneCheckHazard.getTypeId();
        Assert.assertEquals(expected, actual);
    }

    public void testCategoryName() {
        String expected = "test";
        zoneCheckHazard.setCategoryName(expected);
        String actual = zoneCheckHazard.getCategoryName();
        Assert.assertEquals(expected, actual);
    }

    public void testRiskName() {
        String expected = "test";
        zoneCheckHazard.setRiskName(expected);
        String actual = zoneCheckHazard.getRiskName();
        Assert.assertEquals(expected, actual);
    }

    public void testPasscodeUser() {
        String expected = "test";
        zoneCheckHazard.setPasscodeUser(expected);
        String actual = zoneCheckHazard.getPasscodeUser();
        Assert.assertEquals(expected, actual);
    }

    public void testTypeName() {
        String expected = "test";
        zoneCheckHazard.setTypeName(expected);
        String actual = zoneCheckHazard.getTypeName();
        Assert.assertEquals(expected, actual);
    }

    public void testIsResolved() {
        Boolean expected = true;
        zoneCheckHazard.setResolved(expected);
        Boolean actual = zoneCheckHazard.isResolved();
        Assert.assertEquals(expected, actual);
    }

    public void testGroupId() {
        int expected = 99;
        zoneCheckHazard.setGroupId(expected);
        int actual = zoneCheckHazard.getGroupId();
        Assert.assertEquals(expected, actual);
    }

}