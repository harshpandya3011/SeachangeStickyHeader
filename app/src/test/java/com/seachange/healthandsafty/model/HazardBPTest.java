package com.seachange.healthandsafty.model;

import junit.framework.TestCase;

import org.junit.Assert;

public class HazardBPTest extends TestCase {

    private HazardBP hazardBP;

    protected void setUp() throws Exception {
        super.setUp();
        hazardBP = new HazardBP();
    }

    public void testSubTitle() {
        String expected = "this is a test answer";
        hazardBP.setSubTitle(expected);
        String actual = hazardBP.getSubTitle();
        Assert.assertEquals(expected, actual);
    }

    public void testTitle() {
        String expected = "this is a test answer";
        hazardBP.setTitle(expected);
        String actual = hazardBP.getTitle();
        Assert.assertEquals(expected, actual);
    }

    public void testIsHazard() {
        Boolean expected = true;
        hazardBP.setHazard(expected);
        Boolean actual = hazardBP.isHazard();
        Assert.assertEquals(expected, actual);
    }
}