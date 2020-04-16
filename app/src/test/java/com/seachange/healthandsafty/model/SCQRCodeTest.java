package com.seachange.healthandsafty.model;

import junit.framework.TestCase;

import org.junit.Assert;

public class SCQRCodeTest extends TestCase {

    private SCQRCode scqrCode;

    protected void setUp() throws Exception {
        super.setUp();
        scqrCode = new SCQRCode();
    }

    public void testPoint() {
        String expected = "A";
        scqrCode.setPoint(expected);
        String actual = scqrCode.getPoint();
        Assert.assertEquals(expected, actual);
    }

    public void testId() {
        String expected = "Asfdsafdff";
        scqrCode.setPointQRId(expected);
        String actual = scqrCode.getPointQRId();
        Assert.assertEquals(expected, actual);
    }

    public void testSiteId() {
        String expected = "380";
        scqrCode.setSiteId(expected);
        String actual = scqrCode.getSiteId();
        Assert.assertEquals(expected, actual);
    }

    public void testZoneId() {
        String expected = "404";
        scqrCode.setZoneId(expected);
        String actual = scqrCode.getZoneId();
        Assert.assertEquals(expected, actual);
    }
}
