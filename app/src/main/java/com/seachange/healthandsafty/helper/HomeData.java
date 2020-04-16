package com.seachange.healthandsafty.helper;

import android.content.Context;
import android.util.Log;

import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.CheckTime;
import com.seachange.healthandsafty.model.SiteCheck;
import com.seachange.healthandsafty.model.SiteZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kevinsong on 25/10/2017.
 */

public class HomeData {

    public SiteCheck siteCheck;
    public CheckTime mCurrentCheck;
    public String nextCheckTime, tourName;
    public ArrayList<SiteZone> mZones;
    public ManagerTour managerTour;
    private Context mCtx;
    public Date firstCheckDate;
    public boolean beforeFirstCheck, afterLastCheck;
    public CaygoSite caygoSite;

    public HomeData() {

    }

    public HomeData(Context c, String name) {
        this.mCtx = c;
        this.tourName = name;
        this.caygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();
        this.initSiteSchedule();
    }

    private void initSiteSchedule() {
        ArrayList<SiteCheck> siteChecks = PreferenceHelper.getInstance(mCtx).getSiteScheduleDataWithOutRefresh();
        if (siteChecks != null) {
            siteCheck = getTodayCheck(siteChecks);
            if (siteCheck == null) return;
            mCurrentCheck = getCurrentCheckTime(siteCheck.getCheckTimes());
            if (mCurrentCheck != null) {
                mZones = mCurrentCheck.getScheduledCheckses();
                managerTour = new ManagerTour(mCtx, mZones, tourName);
            }
        }
    }

    public void reloadDataFromCache() {
        initSiteSchedule();
    }

    public void updateScheduleInPrefs() {
        ArrayList<SiteCheck> siteChecks = PreferenceHelper.getInstance(mCtx).getSiteScheduleDataWithOutRefresh();
        if (siteChecks != null && mCurrentCheck != null) {
            for (int i = 0; i < siteChecks.size(); i++) {
                if (siteChecks.get(i).isToday()) {
                    for (CheckTime c : siteChecks.get(i).getCheckTimes()) {
                        if (c.getCheck_id().equals(mCurrentCheck.getCheck_id())
                            && mCurrentCheck.getScheduledCheckses() != null
                            && c.getScheduledCheckses() != null) {
                            ArrayList<SiteZone> updatedZones = new ArrayList<>();
                            for (SiteZone scheduledChecks : c.getScheduledCheckses()) {
                                if (scheduledChecks.isChecked()) {
                                    updatedZones.add(scheduledChecks);
                                } else {
                                    for (SiteZone checks : mCurrentCheck.getScheduledCheckses()) {
                                        if (checks.getZone_id() == scheduledChecks.getZone_id()) {
                                            updatedZones.add(checks);
                                            break;
                                        }
                                    }
                                }
                            }
                            c.setScheduledCheckses(updatedZones);
                            Logger.info("CHECK: update prefs found " + c.getCheck_id());
                        }
                    }
                }
            }
        }

//        PreferenceHelper.getInstance(mCtx).saveSiteScheduleData(siteChecks);
        initSiteSchedule();
    }

    public void updateCurrentCheck() {

        if (mCurrentCheck != null && siteCheck != null) {
            for (CheckTime c : siteCheck.getCheckTimes()) {
                if (c.getCheck_id().equals(mCurrentCheck.getCheck_id())) {
                    for (SiteZone zone : c.getScheduledCheckses()) {
                        zone.setChecked(true);
                    }
                }
            }
        }

        if (siteCheck != null) {
            mCurrentCheck = getCurrentCheckTime(siteCheck.getCheckTimes());
        }
        if (mCurrentCheck != null) {
            mZones = mCurrentCheck.getScheduledCheckses();
        }
        managerTour = new ManagerTour(mCtx, mZones, tourName);
    }

    public SiteCheck getTodayCheck(ArrayList<SiteCheck> siteChecks) {
        SiteCheck siteCheck = null;
        for (int i = 0; i < siteChecks.size(); i++) {
            if (siteChecks.get(i).isToday()) {
                return siteChecks.get(i);
            }
        }
        return siteCheck;
    }

    private CheckTime getCurrentCheckTime(ArrayList<CheckTime> checkTimes) {
        CheckTime checkTime = null;
        if (checkTimes == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            for (int i = 0; i < checkTimes.size(); i++) {
                Date mDate = sdf.parse(checkTimes.get(i).getCheck_date());
                Date currentDate = new Date();
                if (i == 0 && currentDate.before(mDate)) {
                    beforeFirstCheck = true;
                    firstCheckDate = mDate;
                }
                if ((i == checkTimes.size() - 1) && currentDate.after(mDate)) {
                    afterLastCheck = true;
                }
                if (mDate.after(currentDate)) {
                    KLogger.info("CHECK date found for next caygo" + checkTimes.get(i).getCheck_date());
                    if (i == 0 && beforeFirstCheck) {
                        return checkTime;
                    } else if (i == 0) {
                        return checkTimes.get(i);
                    }
                    //previous one not finished
                    else if (!checkTimeFinished(checkTimes.get(i - 1).getScheduledCheckses())) {
                        nextCheckTime = checkTimes.get(i).getCheck_date();
                        Logger.info("CHECK current one not finished.");
                        return checkTimes.get(i - 1);
                    } else {
                        Date mPre = sdf.parse(checkTimes.get(i - 1).getCheck_date());
                        if (mPre.before(currentDate)) {
                            nextCheckTime = checkTimes.get(i).getCheck_date();
                            return checkTimes.get(i - 1);
                        }

                        //previous one finished
                        if (i < checkTimes.size() - 1) {
                            nextCheckTime = checkTimes.get(i + 1).getCheck_date();
                        }
                        Logger.info("CHECK Next one found.");
                        return checkTimes.get(i);
                    }
                } else {
                    KLogger.info("CHECK date not found for next caygo");
                }
                KLogger.info("CHECK date not found for next caygo" + i);

                //
                //last zone check still not done yet
                //

                if ((i == checkTimes.size() - 1) && !checkTimeFinished(checkTimes.get(i).getScheduledCheckses())) {
                    KLogger.info("CHECK last one");
                    if (checkTimes.get(i).getTotalChecks() == checkTimes.get(i).getTotalChecksComplete()) {
                        return null;
                    } else if (isCheckLive(checkTimes.get(i))) {
                        return checkTimes.get(i);
                    } else {
                        return null;
                    }
                }

                //last check has finished, but within interval time...
//                else if ((i == checkTimes.size() -1 ) && checkTimeFinished(checkTimes.get(i).getScheduledCheckses())) {
//                    if(new Util().checkDate(checkTimes.get(i).getCheck_date(), caygoSite.getCheckSettings().getIntervalInMinutes())) {
//                        KLogger.info("CHECK last one done, but within interval time");
//                        return checkTimes.get(i);
//                    }
//                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return checkTime;
    }

    public boolean checkTimeFinished(ArrayList<SiteZone> siteZones) {
        for (int i = 0; i < siteZones.size(); i++) {
            if (!siteZones.get(i).isChecked()) {
                return false;
            }
        }
        return true;
    }

    public boolean isCheckLive(CheckTime checkTime) {
        checkTime.processCheckTime();
        if (checkTime.getStatus() == 2) return true;
        else return false;
    }

    public boolean getCheckEndTime() {
        if (mCurrentCheck == null) return false;
        mCurrentCheck.processCheckTime();
        if (mCurrentCheck.getStatus() == 2) return true;
        else return false;
    }

}
