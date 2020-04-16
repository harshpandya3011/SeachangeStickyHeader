package com.seachange.healthandsafty;


import org.junit.Before;
import org.junit.Test;

import com.seachange.healthandsafty.utils.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Created by kevinsong on 26/10/2017.
 */

public class UtilTest {


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void checkToday() throws Exception {
        boolean today = Util.todayDate().equals("27 Oct");
        assertEquals(false, today);
    }

    @Test
    public void checkTodayWithMonth() throws Exception {
        boolean today = new Util().todayDateWithMonth().equals("27 Oct 2018");
        assertEquals(false, today);
    }

    @Test
    public void addMinutes() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            Date tmp = new Date();
            tmp = sdf.parse("2017-10-13T13:30:00+00:00");
            Date date = new Util().addMinutesToDate(30, tmp);
            Date dueDate = sdf.parse("2017-10-13T14:00:00+00:00");
            assertEquals(dueDate, date);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkDate() throws Exception {
        boolean live = new Util().zoneCheckDate(30, "2017-10-13T13:30:00+00:00");
        assertEquals(false, live);
    }
}
