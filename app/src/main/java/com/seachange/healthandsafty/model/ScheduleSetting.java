package com.seachange.healthandsafty.model;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ScheduleSetting {

    private int zoneId;
    private String name;
    private boolean hasRegularSchedule;
    private ArrayList<Schedule> schedules = new ArrayList<>();
    private boolean unSync;

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
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

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }

    public boolean isUnSync() {
        return unSync;
    }

    public void setUnSync(boolean unSync) {
        this.unSync = unSync;
    }

    public Schedule getScheduleByDay(int day) {
        if (getSchedules() ==  null) return null;
        if (getSchedules().size() == 0) return null;
        for (Schedule temp: getSchedules()) {
            if (temp.getDayOfWeek() != null && temp.getDayOfWeek() == day) {
                return temp;
            }
        }
        return null;
    }

    public Schedule getScheduleByDate(String date){
        if (getSchedules() ==  null) return null;
        if (getSchedules().size() == 0) return null;
        for (Schedule temp: getSchedules()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat sdformat = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
            try {
                Date date1 = sdf.parse(temp.getDate());
                Date date2 = sdformat.parse(date);
               if(DateUtils.isSameDay(date1, date2)){
                   return temp;
               }
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
