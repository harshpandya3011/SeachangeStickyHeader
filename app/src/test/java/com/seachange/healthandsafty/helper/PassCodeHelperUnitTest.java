package com.seachange.healthandsafty.helper;

import org.junit.Assert;
import org.junit.Test;

public class PassCodeHelperUnitTest {

    @Test
    public void InValidEmailTest() {

        PassCodeHelper helper = new PassCodeHelper();
        Assert.assertEquals(helper.isValidPassCode("$2a$10$llw0G6IyibUob8h5XRt9xuRczaGdCm/AiV6SSjf5v78XS824EGbh", "1234"), false);
    }

    @Test
    public void ValidEmailTest() {

        PassCodeHelper helper = new PassCodeHelper();
        Assert.assertEquals(helper.isValidPassCode("$2a$10$4hw6k45sD9Sw5Q0wgH6HBuZXoDwIuVmjp56riLZVcbQODyUz2SV.K", "1234"), true);
    }

    @Test
    public void PassCodeTest() {
        PassCodeHelper helper = new PassCodeHelper();
        String passCode = helper.hashedPassword("1234");
        Assert.assertEquals(helper.isValidPassCode(passCode, "1234"), true);

    }
}
