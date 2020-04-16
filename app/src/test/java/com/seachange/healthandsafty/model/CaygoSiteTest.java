package com.seachange.healthandsafty.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CaygoSiteTest {

    @Test
    public void testSiteId() {
        CaygoSite caygoSite = new CaygoSite();
        caygoSite.setSite_id(380);
        assertEquals(380, caygoSite.getSite_id());
    }

    @Test
    public void testSiteName() {
        CaygoSite caygoSite = new CaygoSite();
        caygoSite.setSite_name("Demo");
        assertEquals("Demo", caygoSite.getSite_name());
    }

    @Test
    public void testGroupId() {
        CaygoSite caygoSite = new CaygoSite();
        caygoSite.setGroupId(12);
        assertEquals(12, caygoSite.getGroupId());
    }

    @Test
    public void testGroupName() {
        CaygoSite caygoSite = new CaygoSite();
        caygoSite.setGroup_name("seachange");
        assertEquals("seachange", caygoSite.getGroup_name());
    }

    @Test
    public void testIsUsingNFC() {
        CaygoSite caygoSite = new CaygoSite();
        caygoSite.setUsingNfc(true);
        assertTrue(caygoSite.isUsingNfc());
    }
}
