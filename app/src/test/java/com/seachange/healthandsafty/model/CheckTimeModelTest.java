package com.seachange.healthandsafty.model;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CheckTimeModelTest {

    @Test
    public void startTimeTest() {
        CheckTime checkTime = new CheckTime();
        checkTime.setStart_time("2017-10-13T13:30:00+00:00");
        assertEquals("13:30", checkTime.getCheckStartTime());
    }

    @Test
    public void endTimeTest() {
        CheckTime checkTime = new CheckTime();
        checkTime.setEnd_time("2017-10-13T13:30:00+00:00");
        assertEquals("13:30", checkTime.getCheckEndTime());
    }

    @Test
    public void checkStatusTest() {
        CheckTime checkTime = new CheckTime();
        checkTime.setStart_time("2017-10-13T13:30:00+00:00");
        checkTime.setEnd_time("2017-10-13T14:30:00+00:00");
        checkTime.processCheckTime();
        assertEquals(1, (int)checkTime.getStatus());
    }

    @Test
    public void checkPrecentageTest() {
        CheckTime checkTime = new CheckTime();
        checkTime.setPercentage(50);
        assertEquals(50, (int)checkTime.getPercentage());
    }


    @Test
    public void checkCurrentZonesChecked() throws Exception {
        CheckTime checkTime = new CheckTime();

        ArrayList<SiteZone> tmp = new ArrayList<>();
        tmp.add(new SiteZone(true));
        tmp.add(new SiteZone(true));
        tmp.add(new SiteZone(false));
        checkTime.setScheduledCheckses(tmp);
        checkTime.getScheduledCheckses().add(new SiteZone(false));
        assertEquals(2, checkTime.getCurrentCheckedZones());
    }

    @Test
    public void checkCurrentZoneHazards() throws Exception {
        CheckTime checkTime = new CheckTime();
        ArrayList<SiteZone> tmp = new ArrayList<>();
        tmp.add(new SiteZone(1));
        tmp.add(new SiteZone(2));
        tmp.add(new SiteZone(3));
        tmp.add(new SiteZone(4));
        tmp.add(new SiteZone(5));
        checkTime.setScheduledCheckses(tmp);
        assertEquals(15, checkTime.getCurrentHazards());
    }
}
