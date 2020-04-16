package com.seachange.healthandsafty.model;

import junit.framework.TestCase;

import org.junit.Assert;

public class RAHazardsTest extends TestCase {

    private RAHazards raHazards;
    protected void setUp() throws Exception {
        super.setUp();
        raHazards = new RAHazards();
    }

    public void testRAId() {
        String expected = "d46be904-cafd-11e9-a32f-2a2ae2dbcce4";
        raHazards.setRiskAssessmentId(expected);
        String actual = raHazards.getRiskAssessmentId();
        Assert.assertEquals(expected, actual);
    }

    public void testDescription() {
        String expected = "this is a test description";
        raHazards.setDescription(expected);
        String actual = raHazards.getDescription();
        Assert.assertEquals(expected, actual);
    }

    public void testRef() {
        String expected = "refere";
        raHazards.setReference(expected);
        String actual = raHazards.getReference();
        Assert.assertEquals(expected, actual);
    }

    public void testImageUrl() {
        String expected = "http://www.imageurl.com/url";
        raHazards.setPrimaryImageUrl(expected);
        String actual = raHazards.getPrimaryImageUrl();
        Assert.assertEquals(expected, actual);
    }

    public void testId() {
        String expected = "d46be904-cafd-11e9-a32f-2a2ae2dbcce4";
        raHazards.setId(expected);
        String actual = raHazards.getId();
        Assert.assertEquals(expected, actual);
    }

    public void testNumberOfControls() {
        int expected = 3;
        raHazards.setNumOfNewControls(expected);
        int actual = raHazards.getNumOfNewControls();
        Assert.assertEquals(expected, actual);
    }
}
