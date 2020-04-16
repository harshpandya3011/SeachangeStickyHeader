package com.seachange.healthandsafty.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ZoneCheckSettings {

    @SerializedName("dayTimes")
    public ArrayList<DayTime> times;

    private boolean zoneCheckPhotoEnabled, zoneCheckPhotoRequired;
    private Integer intervalInMinutes, timeToCompleteInMinutes;

    public ArrayList<DayTime> getTimes() {
        return times;
    }

    public void setTimes(ArrayList<DayTime> times) {
        this.times = times;
    }

    public boolean isZoneCheckPhotoEnabled() {
        return zoneCheckPhotoEnabled;
    }

    public void setZoneCheckPhotoEnabled(boolean zoneCheckPhotoEnabled) {
        this.zoneCheckPhotoEnabled = zoneCheckPhotoEnabled;
    }

    public boolean isZoneCheckPhotoRequired() {
        return zoneCheckPhotoRequired;
    }

    public void setZoneCheckPhotoRequired(boolean zoneCheckPhotoRequired) {
        this.zoneCheckPhotoRequired = zoneCheckPhotoRequired;
    }

    public Integer getIntervalInMinutes() {
        return intervalInMinutes;
    }

    public void setIntervalInMinutes(Integer intervalInMinutes) {
        this.intervalInMinutes = intervalInMinutes;
    }

    public Integer getTimeToCompleteInMinutes() {
        return timeToCompleteInMinutes;
    }

    public void setTimeToCompleteInMinutes(Integer timeToCompleteInMinutes) {
        this.timeToCompleteInMinutes = timeToCompleteInMinutes;
    }
}
