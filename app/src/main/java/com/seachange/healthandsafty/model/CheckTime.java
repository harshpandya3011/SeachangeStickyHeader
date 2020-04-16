package com.seachange.healthandsafty.model;

import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// status of check time

// 0 default, not processed
// 1 is for passed zone time
// 2 is currently available to check
// 3 is further checks
//

/**
 * Created by kevinsong on 13/10/2017.
 */
@Parcel
public class CheckTime {

    String check_date, check_id, start_time, end_time;
    Integer totalChecks, totalChecksComplete, percentage, numOfHazardsIdentified, status = 0;
    ArrayList<SiteZone> scheduledCheckses;

    public CheckTime() {

    }

    public String getCheck_date() {
        return check_date;
    }

    public void setCheck_date(String check_date) {
        this.check_date = check_date;
    }

    public Integer getTotalChecks() {
        return totalChecks;
    }

    public void setTotalChecks(Integer totalChecks) {
        this.totalChecks = totalChecks;
    }

    public Integer getTotalChecksComplete() {
        return totalChecksComplete;
    }

    public void setTotalChecksComplete(Integer totalChecksComplete) {
        this.totalChecksComplete = totalChecksComplete;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public ArrayList<SiteZone> getScheduledCheckses() {
        return scheduledCheckses;
    }

    public void setScheduledCheckses(ArrayList<SiteZone> scheduledCheckses) {
        this.scheduledCheckses = scheduledCheckses;
    }

    public int getTotalHazards() {
        int total = 0;
        for (int i=0; i<this.scheduledCheckses.size();i++) {
            SiteZone siteZone = scheduledCheckses.get(i);
            total = total + siteZone.getHazards();
        }
        return total;
    }

    public String getCheckTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date mDate = sdf.parse(getCheck_date());
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(mDate);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCheckStartTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date mDate = sdf.parse(getStart_time());
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(mDate);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCheckEndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date mDate = sdf.parse(getEnd_time());
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(mDate);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getNumOfHazardsIdentified() {
        return numOfHazardsIdentified;
    }

    public void setNumOfHazardsIdentified(Integer numOfHazardsIdentified) {
        this.numOfHazardsIdentified = numOfHazardsIdentified;
    }

    public String getCheck_id() {
        return check_id;
    }

    public void setCheck_id(String check_id) {
        this.check_id = check_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void processCheckTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        try {
            Date start = sdf.parse(getStart_time());
            Date end = sdf.parse(getEnd_time());
            Date currentDate = new Date();

            if (currentDate.before(start)) {
                status = 3;
            } else if(currentDate.before(end) && currentDate.after(start)) {
                status = 2;
            } else {
                status = 1;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentCheckedZones() {
        int checked = 0;
        for (int i = 0;i<scheduledCheckses.size();i++) {
            if (scheduledCheckses.get(i).isChecked()){
                checked++;
            }
        }
        return checked;
    }

    public int getCurrentHazards() {
        int hazards = 0;
        for (int i = 0;i<scheduledCheckses.size();i++) {
            if (scheduledCheckses.get(i).isChecked()){
                hazards = hazards + scheduledCheckses.get(i).getHazards();
            }
        }
        return hazards;
    }
}
