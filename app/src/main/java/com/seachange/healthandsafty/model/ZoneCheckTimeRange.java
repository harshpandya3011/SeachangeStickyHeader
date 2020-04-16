package com.seachange.healthandsafty.model;

public class ZoneCheckTimeRange {

    private Integer intervalInMinutes, timeToCompleteInMinutes;
    private String startTime, endTime;
    private boolean endTimeIsNextDay;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isEndTimeIsNextDay() {
        return endTimeIsNextDay;
    }

    public void setEndTimeIsNextDay(boolean endTimeIsNextDay) {
        this.endTimeIsNextDay = endTimeIsNextDay;
    }
}
