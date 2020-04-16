package com.seachange.healthandsafty.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CaygoUserUnitTest {

    @Test
    public void checkDetails() throws Exception {
        UserData user = new UserData("Kevin", "Song", "Kevin Song", "KS", "kevin.song@seachange-intl.com", "", "", true, true, "",
            null, null, false, 232131232, null, "", null, null, true);
        assertEquals("KS", user.getInitials());
        assertEquals("Kevin", user.getFirstName());
        assertEquals("Song", user.getLastName());
    }
}
