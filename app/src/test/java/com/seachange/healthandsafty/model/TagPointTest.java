package com.seachange.healthandsafty.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TagPointTest {

    @Test
    public void testSummaryId() {
        TagPoint tagPoint = new TagPoint();
        tagPoint.setTagSummaryId("adfsdfsf123");
        assertEquals("adfsdfsf123", tagPoint.getTagSummaryId());
    }

    @Test
    public void testDate() {
        TagPoint tagPoint = new TagPoint();
        tagPoint.setSetupDate("11/11/2019");
        assertEquals("11/11/2019", tagPoint.getSetupDate());
    }
}
