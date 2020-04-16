package com.seachange.healthandsafty.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.seachange.healthandsafty.model.CheckTime;
import com.seachange.healthandsafty.model.SiteCheck;
import com.seachange.healthandsafty.model.SiteZone;

/**
 * Created by kevinsong on 24/10/2017.
 */

public class SiteCheckUtil {

    public SiteCheckUtil() {

    }

    public static HashMap<String, List<SiteZone>> getData(SiteCheck siteCheck) {
        LinkedHashMap<String, List<SiteZone>> expandableListDetail = new LinkedHashMap<String, List<SiteZone>>();

        List<SiteZone> tmp = siteCheck.getTourChecks();
        expandableListDetail.put(siteCheck.getDate(), tmp);

        for (int i= 0; i< siteCheck.getCheckTimes().size();i++) {
            CheckTime checkTime = siteCheck.getCheckTimes().get(i);
            expandableListDetail.put(checkTime.getStart_time()+"-"+checkTime.getEnd_time(), checkTime.getScheduledCheckses());
        }
        return expandableListDetail;
    }


    public static HashMap<String, List<SiteZone>> getHomeData(SiteCheck siteCheck) {
        LinkedHashMap<String, List<SiteZone>> expandableListDetail = new LinkedHashMap<String, List<SiteZone>>();
        for (int i= 0; i< siteCheck.getCheckTimes().size();i++) {
            CheckTime checkTime = siteCheck.getCheckTimes().get(i);
            checkTime.processCheckTime();
            if (checkTime.getStatus() ==2) {
                expandableListDetail.put(checkTime.getStart_time() + "-" + checkTime.getEnd_time(), checkTime.getScheduledCheckses());
            }
        }
        return expandableListDetail;
    }

    public static ArrayList<CheckTime> getHomeDataList(SiteCheck siteCheck) {
        ArrayList<CheckTime>expandableListDetail = new ArrayList<>();
        for (int i= 0; i< siteCheck.getCheckTimes().size();i++) {
            CheckTime checkTime = siteCheck.getCheckTimes().get(i);
            checkTime.processCheckTime();
            if (checkTime.getStatus() ==2) {
               expandableListDetail.add(checkTime);
            }
        }
        return expandableListDetail;
    }
}
