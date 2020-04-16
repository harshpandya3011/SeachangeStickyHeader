package com.seachange.healthandsafty.offline.model;

public class CheckUtils {
    public static ZoneCheck clone(ZoneCheck from, boolean syncPending) {
        return new ZoneCheck();
    }

    public static ZoneCheck clone(ZoneCheck from, long id) {
        return new ZoneCheck(id);
    }
}