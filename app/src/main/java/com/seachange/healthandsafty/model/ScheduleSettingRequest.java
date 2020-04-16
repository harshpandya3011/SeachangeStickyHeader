package com.seachange.healthandsafty.model;

import java.util.ArrayList;

public class ScheduleSettingRequest {

    private int zoneId;
    private int groupId;
    private String name;
    private boolean hasRegularSchedule;
    private ArrayList<ScheduleRequest> schedules = new ArrayList<>();

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasRegularSchedule() {
        return hasRegularSchedule;
    }

    public void setHasRegularSchedule(boolean hasRegularSchedule) {
        this.hasRegularSchedule = hasRegularSchedule;
    }

    public ArrayList<ScheduleRequest> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<ScheduleRequest> schedules) {
        this.schedules = schedules;
    }

    public void setDataFromSetting(ScheduleSetting scheduleSetting) {
        this.zoneId = scheduleSetting.getZoneId();
        this.name = scheduleSetting.getName();
        this.hasRegularSchedule = scheduleSetting.isHasRegularSchedule();

        for (Schedule schedule : scheduleSetting.getSchedules()) {
            ScheduleRequest scheduleRequest = new ScheduleRequest();
            scheduleRequest.convertFromSchedule(schedule);
            this.schedules.add(scheduleRequest);
        }
    }
}
