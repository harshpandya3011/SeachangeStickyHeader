package com.seachange.healthandsafty.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DBCheckCompleteTest {

    @Test
    public void testId() {
        DBCheckComplete dbCheckComplete = new DBCheckComplete();
        dbCheckComplete.setQrCodeId("sdfsdfie932");
        assertEquals("sdfsdfie932", dbCheckComplete.getQrCodeId());
    }

    @Test
    public void testComplete() {
        DBCheckComplete dbCheckComplete = new DBCheckComplete();
        dbCheckComplete.setTimeCompleted("11/11/2019");
        assertEquals("11/11/2019", dbCheckComplete.getTimeCompleted());
    }
}
