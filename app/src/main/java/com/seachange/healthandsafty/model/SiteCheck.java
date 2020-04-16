package com.seachange.healthandsafty.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kevinsong on 13/10/2017.
 */

public class SiteCheck {

    private String scheduledSiteTourId, date, timeStarted, timeCompleted, siteTourId;
    private boolean isComplete;
    private int numOfHazardsIdentified, status, siteId;
    private ArrayList<CheckTime> checkTimes;
    private ArrayList<SiteZone> tourChecks;

    public SiteCheck() {

    }

    public String getDate() {
        return date;
    }

    public String getTime() {

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        java.util.Date mDate = null;
        try{
            mDate = form.parse(date);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return format.format(mDate);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<CheckTime> getCheckTimes() {
        return checkTimes;
    }

    public void setCheckTimes(ArrayList<CheckTime> checkTimes) {
        this.checkTimes = checkTimes;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(String timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public ArrayList<SiteZone> getTourChecks() {
        return tourChecks;
    }

    public void setTourChecks(ArrayList<SiteZone> tourChecks) {
        this.tourChecks = tourChecks;
    }

    public boolean isToday() {

        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        java.util.Date mDate = null;
        try{
            mDate = form.parse(date);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd yyyy", Locale.getDefault());
        String newDateStr = format.format(mDate);

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String todayAsString = format.format(today);

        return newDateStr.equals(todayAsString);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getScheduledSiteTourId() {
        return scheduledSiteTourId;
    }

    public void setScheduledSiteTourId(String scheduledSiteTourId) {
        this.scheduledSiteTourId = scheduledSiteTourId;
    }

    public String getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(String timeStarted) {
        this.timeStarted = timeStarted;
    }

    public int getNumOfHazardsIdentified() {
        return numOfHazardsIdentified;
    }

    public void setNumOfHazardsIdentified(int numOfHazardsIdentified) {
        this.numOfHazardsIdentified = numOfHazardsIdentified;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getSiteTourId() {
        return siteTourId;
    }

    public void setSiteTourId(String siteTourId) {
        this.siteTourId = siteTourId;
    }
}
