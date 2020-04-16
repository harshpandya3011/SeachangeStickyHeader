package com.seachange.healthandsafty.model;

import java.util.ArrayList;

public class ScheduleRequest {

    private Integer dayOfWeek;
    private String date;
    private ArrayList<ZoneCheckTimeRange> zoneCheckTimeRanges = new ArrayList();

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

    public void setZoneCheckTimeRanges(ArrayList<ZoneCheckTimeRange> zoneCheckTimeRanges) {
        this.zoneCheckTimeRanges = zoneCheckTimeRanges;
    }

    public ScheduleRequest convertFromSchedule(Schedule schedule) {
        this.dayOfWeek = schedule.getDayOfWeek();
        this.date = schedule.getDate();
        this.zoneCheckTimeRanges = schedule.getZoneCheckTimeRanges();
        return this;
    }
}
