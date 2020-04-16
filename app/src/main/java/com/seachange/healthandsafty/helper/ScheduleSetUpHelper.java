package com.seachange.healthandsafty.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.Schedule;
import com.seachange.healthandsafty.model.ScheduleSetting;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.model.ZoneCheckTimeRange;
import com.seachange.healthandsafty.utils.UtilStrings;

import org.apache.commons.lang3.time.DateUtils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class ScheduleSetUpHelper {

    private Context mCtx;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private CaygoSite mSite;

    public ScheduleSetUpHelper(Context c) {
        mCtx = c;
        prefs = mCtx.getSharedPreferences(UtilStrings.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        mSite = PreferenceHelper.getInstance(mCtx).getSiteData();
    }

    public void initSchedules() {
        if (getSavedSchedules().size() == 0) {
            setSchedulesFromServer();
        } else if (!unsyncedScheduleOnApp()){
            setSchedulesFromServer();
        }
    }

    private boolean unsyncedScheduleOnApp() {
        ArrayList<ScheduleSetting> savedSchedules = getSavedSchedules();
        for (ScheduleSetting tmp: savedSchedules) {
            if (tmp.isUnSync()) return true;
        }
        return false;
    }

    private void setSchedulesFromServer() {
        Gson gson = new Gson();
        String json = gson.toJson(currentSchedules());
        editor.putString(UtilStrings.PREFERENCES_SCHEDULES, json).apply();
    }

    public void resetSavedSchedules() {
        editor.putString(UtilStrings.PREFERENCES_SCHEDULES, null).apply();
    }

    public ArrayList<ScheduleSetting> currentSchedules() {
        mSite = PreferenceHelper.getInstance(mCtx).getSiteData();
        return mSite.getScheduleSetting();
    }

    public ArrayList<ScheduleSetting> getSavedSchedules() {
        Gson gson = new Gson();
        String json = prefs.getString(UtilStrings.PREFERENCES_SCHEDULES, null);
        if (json == null || json.equals("null")) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<ScheduleSetting>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void updateSavedSchedules(ArrayList<ScheduleSetting> settings) {
        Gson gson = new Gson();
        String json = gson.toJson(settings);
        editor.putString(UtilStrings.PREFERENCES_SCHEDULES, json).apply();
        Logger.info(json);
    }

    public ScheduleSetting getCurrentScheduleSettingByZoneId(int zoneId, boolean savedData) {
        ArrayList<ScheduleSetting> settings;
        if (savedData) {
            settings = getSavedSchedules();
        } else {
            settings = currentSchedules();
        }

        if (settings == null) return null;
        if (settings.size() == 0) return null;

        for (ScheduleSetting temp: settings) {
            if (temp.getZoneId() == zoneId) return temp;
        }
        return null;
    }

    public void updateCurrentScheduleSettingByZoneId(int zoneId, ScheduleSetting setting, boolean withUnSync) {
        if (withUnSync)setting.setUnSync(true);
        ArrayList<ScheduleSetting> settings = getSavedSchedules();
        if (settings == null) return;
        if (settings.size() == 0) {
            settings.add(setting);
        }else {
            boolean needToAdd = true;
            for (int i = 0; i < settings.size(); i++) {
                ScheduleSetting temp = settings.get(i);
                if (temp.getZoneId() == zoneId) {
                    settings.set(i, setting);
                    needToAdd = false;
                }
            }
            if (needToAdd){
                settings.add(setting);
            }
        }
        updateSavedSchedules(settings);
    }

    public void AddTimeRangeToExistingZoneSettingRegular(SiteZone zone, int tab, ZoneCheckTimeRange zoneCheckTimeRange){
        ScheduleSetting scheduleSetting = getCurrentScheduleSettingByZoneId(zone.getZone_id(), true);
        if (scheduleSetting!=null) {
            ArrayList<Schedule> schedules = scheduleSetting.getSchedules();

            boolean isEmpty = true;
            for (Schedule tmp: schedules) {
                if (tmp != null && tmp.getDayOfWeek() !=null && tmp.getDayOfWeek() == tab) {
                    tmp.getZoneCheckTimeRanges().add(zoneCheckTimeRange);
                    tmp.setNeedToSync(true);
                    tmp.setZoneCheckTimeRanges(sortSchedulesByStartTime(tmp.getZoneCheckTimeRanges()));
                    isEmpty = false;
                }
            }

            if (isEmpty) {
                Schedule temp = new Schedule();
                temp.setDayOfWeek(tab);
                temp.setDate(null);
                temp.setNeedToSync(true);
                temp.getZoneCheckTimeRanges().add(zoneCheckTimeRange);
                temp.setZoneCheckTimeRanges(sortSchedulesByStartTime(temp.getZoneCheckTimeRanges()));
                schedules.add(temp);
            }
            scheduleSetting.setSchedules(schedules);
        } else {
            scheduleSetting = new ScheduleSetting();

            Schedule temp = new Schedule();
            temp.setDayOfWeek(tab);
            temp.getZoneCheckTimeRanges().add(zoneCheckTimeRange);
            temp.setNeedToSync(true);
            temp.setZoneCheckTimeRanges(sortSchedulesByStartTime(temp.getZoneCheckTimeRanges()));
            scheduleSetting.setZoneId(zone.getZone_id());
            scheduleSetting.setName(zone.getZone_name());
            scheduleSetting.getSchedules().add(temp);

        }
        scheduleSetting.setUnSync(true);
        scheduleSetting.setHasRegularSchedule(true);
        updateCurrentScheduleSettingByZoneId(zone.getZone_id(), scheduleSetting, true);
    }

    public void editTimeRangeToExistingZoneSettingRegular(SiteZone zone, int tab, ZoneCheckTimeRange zoneCheckTimeRange, ZoneCheckTimeRange editZoneCheckTimeRange){
        ScheduleSetting scheduleSetting = getCurrentScheduleSettingByZoneId(zone.getZone_id(), true);
        if (scheduleSetting!=null) {
            ArrayList<Schedule> schedules = scheduleSetting.getSchedules();
            for (Schedule tmp: schedules) {
                if (tmp.getDayOfWeek() !=null && tmp.getDayOfWeek() == tab) {
                    for (int i=0; i< tmp.getZoneCheckTimeRanges().size();i++) {
                        if (tmp.getZoneCheckTimeRanges().get(i).getStartTime().equals(editZoneCheckTimeRange.getStartTime()) && tmp.getZoneCheckTimeRanges().get(i).getEndTime().equals(editZoneCheckTimeRange.getEndTime())) {
                            tmp.getZoneCheckTimeRanges().set(i, zoneCheckTimeRange);
                            tmp.setNeedToSync(true);
                            tmp.setZoneCheckTimeRanges(sortSchedulesByStartTime(tmp.getZoneCheckTimeRanges()));
                        }
                    }
                }
            }

            scheduleSetting.setSchedules(schedules);
        }

        scheduleSetting.setUnSync(true);
        updateCurrentScheduleSettingByZoneId(zone.getZone_id(), scheduleSetting, true);
    }

    private ArrayList<ZoneCheckTimeRange> sortSchedulesByStartTime(ArrayList<ZoneCheckTimeRange> timeRanges) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Collections.sort(timeRanges, (ZoneCheckTimeRange o1, ZoneCheckTimeRange o2) -> {
            try {
                return sdf.parse(o1.getStartTime()).compareTo(sdf.parse(o2.getStartTime()));
            } catch (ParseException e) {
                return 0;
            }
        });
        return timeRanges;
    }

    public void AddZoneTimeRangesToExistingZoneSettingRegular(SiteZone zone, int tab, ArrayList<ZoneCheckTimeRange> zoneCheckTimeRanges){
        ScheduleSetting scheduleSetting = getCurrentScheduleSettingByZoneId(zone.getZone_id(), true);
        if (scheduleSetting!=null) {
            ArrayList<Schedule> schedules = scheduleSetting.getSchedules();

            boolean isEmpty = true;
            for (Schedule tmp: schedules) {
                if (tmp.getDayOfWeek()!=null && tmp.getDayOfWeek() == tab) {
                    tmp.setZoneCheckTimeRanges(zoneCheckTimeRanges);
                    isEmpty = false;
                    tmp.setNeedToSync(true);
                    tmp.setZoneCheckTimeRanges(sortSchedulesByStartTime(tmp.getZoneCheckTimeRanges()));

                }
            }

            if (isEmpty) {
                Schedule temp = new Schedule();
                temp.setDayOfWeek(tab);
                temp.setDate(null);
                temp.setNeedToSync(true);
                temp.setZoneCheckTimeRanges(zoneCheckTimeRanges);
                temp.setZoneCheckTimeRanges(sortSchedulesByStartTime(temp.getZoneCheckTimeRanges()));
                schedules.add(temp);
            }
            scheduleSetting.setSchedules(schedules);
        } else {
            scheduleSetting = new ScheduleSetting();

            Schedule temp = new Schedule();
            temp.setDayOfWeek(tab);
            temp.setZoneCheckTimeRanges(zoneCheckTimeRanges);
            temp.setNeedToSync(true);
            temp.setZoneCheckTimeRanges(sortSchedulesByStartTime(temp.getZoneCheckTimeRanges()));
            scheduleSetting.setZoneId(zone.getZone_id());
            scheduleSetting.setName(zone.getZone_name());
            scheduleSetting.setHasRegularSchedule(true);
            scheduleSetting.getSchedules().add(temp);

        }
        scheduleSetting.setUnSync(true);
        updateCurrentScheduleSettingByZoneId(zone.getZone_id(), scheduleSetting, true);
    }

    public void RemoveTimeRangeToExistingZoneSettingRegular(SiteZone zone, int tab, ZoneCheckTimeRange zoneCheckTimeRange){
        ScheduleSetting scheduleSetting = getCurrentScheduleSettingByZoneId(zone.getZone_id(), true);
        if (scheduleSetting!=null) {
            ArrayList<Schedule> schedules = scheduleSetting.getSchedules();
            for (int j = 0; j< schedules.size();j++) {
                Schedule tmp = schedules.get(j);
                if (tmp!=null && tmp.getDayOfWeek() !=null && tmp.getDayOfWeek() == tab) {
                    for (int i=0; i< tmp.getZoneCheckTimeRanges().size();i++) {
                        if (tmp.getZoneCheckTimeRanges().get(i).getStartTime().equals(zoneCheckTimeRange.getStartTime()) && tmp.getZoneCheckTimeRanges().get(i).getEndTime().equals(zoneCheckTimeRange.getEndTime())) {
                            tmp.getZoneCheckTimeRanges().remove(i);
                            if (tmp.getZoneCheckTimeRanges().size() == 0) schedules.remove(tmp);
                        }
                    }
                }
            }
            scheduleSetting.setUnSync(true);
            scheduleSetting.setSchedules(schedules);
        }
        updateCurrentScheduleSettingByZoneId(zone.getZone_id(), scheduleSetting, true);
    }

    //irregular
    public void AddTimeRangeToExistingZoneSettingIrregular(SiteZone zone, String date, ZoneCheckTimeRange zoneCheckTimeRange){
        ScheduleSetting scheduleSetting = getCurrentScheduleSettingByZoneId(zone.getZone_id(), true);
        if (scheduleSetting!=null) {
            ArrayList<Schedule> schedules = scheduleSetting.getSchedules();

            boolean isEmpty = true;
            for (Schedule tmp: schedules) {

                if (tmp.getDate() !=null) {
                    Date date1 = new Date();
                    Date date2 = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    try {
                        date1 = sdf.parse(tmp.getDate());
                        date2 = sdf.parse(date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (DateUtils.isSameDay(date1, date2)) {
                        tmp.getZoneCheckTimeRanges().add(zoneCheckTimeRange);
                        tmp.setZoneCheckTimeRanges(sortSchedulesByStartTime(tmp.getZoneCheckTimeRanges()));
                        isEmpty = false;
                    }
                }
            }

            if (isEmpty) {
                Schedule temp = new Schedule();
                temp.setDate(date);
                temp.getZoneCheckTimeRanges().add(zoneCheckTimeRange);
                temp.setZoneCheckTimeRanges(sortSchedulesByStartTime(temp.getZoneCheckTimeRanges()));
                schedules.add(temp);
            }
            scheduleSetting.setSchedules(schedules);
        } else {
            scheduleSetting = new ScheduleSetting();

            Schedule temp = new Schedule();
            temp.setDate(date);
            temp.getZoneCheckTimeRanges().add(zoneCheckTimeRange);
            temp.setZoneCheckTimeRanges(sortSchedulesByStartTime(temp.getZoneCheckTimeRanges()));

            scheduleSetting.setZoneId(zone.getZone_id());
            scheduleSetting.setName(zone.getZone_name());
            scheduleSetting.getSchedules().add(temp);
        }
        scheduleSetting.setHasRegularSchedule(false);
        scheduleSetting.setUnSync(true);
        updateCurrentScheduleSettingByZoneId(zone.getZone_id(), scheduleSetting, true);
    }

    public void editTimeRangeToExistingZoneSettingIrregular(SiteZone zone, String date, ZoneCheckTimeRange zoneCheckTimeRange, ZoneCheckTimeRange editZoneCheckTimeRange){
        ScheduleSetting scheduleSetting = getCurrentScheduleSettingByZoneId(zone.getZone_id(), true);
        if (scheduleSetting!=null) {
            ArrayList<Schedule> schedules = scheduleSetting.getSchedules();
            for (Schedule tmp: schedules) {

                if (tmp.getDate() !=null) {
                    Date date1 = new Date();
                    Date date2 = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    try {
                        date1 = sdf.parse(tmp.getDate());
                        date2 = sdf.parse(date);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (DateUtils.isSameDay(date1, date2)) {
                        for (int i=0; i< tmp.getZoneCheckTimeRanges().size();i++) {
                            if (tmp.getZoneCheckTimeRanges().get(i).getStartTime().equals(editZoneCheckTimeRange.getStartTime()) && tmp.getZoneCheckTimeRanges().get(i).getEndTime().equals(editZoneCheckTimeRange.getEndTime())) {
                                tmp.getZoneCheckTimeRanges().set(i, zoneCheckTimeRange);
                                tmp.setZoneCheckTimeRanges(sortSchedulesByStartTime(tmp.getZoneCheckTimeRanges()));
                            }
                        }
                    }
                }
            }

            scheduleSetting.setSchedules(schedules);
        }
        //set unsync to be true, need to enable button
        scheduleSetting.setUnSync(true);
        updateCurrentScheduleSettingByZoneId(zone.getZone_id(), scheduleSetting, true);
    }

    public void RemoveTimeRangeToExistingZoneSettingIrregular(SiteZone zone, String date, ZoneCheckTimeRange zoneCheckTimeRange){
        ScheduleSetting scheduleSetting = getCurrentScheduleSettingByZoneId(zone.getZone_id(), true);
        if (scheduleSetting!=null) {
            ArrayList<Schedule> schedules = scheduleSetting.getSchedules();
            for (int j = 0; j< schedules.size();j++) {
                Schedule tmp = schedules.get(j);
                Date date1 = new Date();
                Date date2 = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat sdf2 = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
                try {
                    date1 = sdf.parse(tmp.getDate());
                    date2 = sdf2.parse(date);
                } catch (Exception e){
                    e.printStackTrace();
                }
                if(DateUtils.isSameDay(date1, date2)){
                    for (int i=0; i< tmp.getZoneCheckTimeRanges().size();i++) {
                        if (tmp.getZoneCheckTimeRanges().get(i).getStartTime().equals(zoneCheckTimeRange.getStartTime()) && tmp.getZoneCheckTimeRanges().get(i).getEndTime().equals(zoneCheckTimeRange.getEndTime())) {
                            tmp.getZoneCheckTimeRanges().remove(i);
                            if (tmp.getZoneCheckTimeRanges().size() == 0) schedules.remove(tmp);
                        }
                    }
                }
            }
            //set unsync to be true, need to enable button
            scheduleSetting.setUnSync(true);
            scheduleSetting.setSchedules(schedules);
        }
        updateCurrentScheduleSettingByZoneId(zone.getZone_id(), scheduleSetting, true);
    }
}
