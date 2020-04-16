package com.seachange.healthandsafty.model;

import java.util.ArrayList;

public class Schedule {

    private Integer dayOfWeek;
    private String date;
    private ArrayList<ZoneCheckTimeRange> zoneCheckTimeRanges = new ArrayList();
    private boolean needToSync;

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<ZoneCheckTimeRange> getZoneCheckTimeRanges() {
        return zoneCheckTimeRanges;
    }

    public boolean isNeedToSync() {
        return needToSync;
    }

    public void setNeedToSync(boolean needToSync) {
        this.needToSync = needToSync;
    }

    public void setZoneCheckTimeRanges(ArrayList<ZoneCheckTimeRange> zoneCheckTimeRanges) {
        this.zoneCheckTimeRanges = zoneCheckTimeRanges;
    }
}
