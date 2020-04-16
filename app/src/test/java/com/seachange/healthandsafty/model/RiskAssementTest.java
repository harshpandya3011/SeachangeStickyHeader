package com.seachange.healthandsafty.model;

import junit.framework.TestCase;

import org.junit.Assert;

public class RiskAssementTest extends TestCase {

    private RiskAssement riskAssement;

    protected void setUp() throws Exception {
        super.setUp();
        riskAssement = new RiskAssement();
    }

    public void testId() {
        String expected = "Aasdfsdf";
        riskAssement.setId(expected);
        String actual = riskAssement.getId();
        Assert.assertEquals(expected, actual);
    }


    public void testType() {
        int expected = 2;
        riskAssement.setType(expected);
        int actual = riskAssement.getType();
        Assert.assertEquals(expected, actual);
    }

    public void testVersion() {
        int expected = 2;
        riskAssement.setVersion(expected);
        int actual = riskAssement.getVersion();
        Assert.assertEquals(expected, actual);
    }

    public void testDateScheduled() {
        String expected = "01/01/2019";
        riskAssement.setDateScheduled(expected);
        String actual = riskAssement.getDateScheduled();
        Assert.assertEquals(expected, actual);
    }
}