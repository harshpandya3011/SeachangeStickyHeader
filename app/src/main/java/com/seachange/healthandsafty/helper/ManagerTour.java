package com.seachange.healthandsafty.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.utils.UtilStrings;

/**
 * Created by kevinsong on 05/10/2017.
 */

public class ManagerTour {

    private Context mCtx;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private String tourName;

    public ManagerTour() {

    }

    public ManagerTour(Context c, ArrayList<SiteZone> zones, String name) {
        mCtx = c;
        prefs = mCtx.getSharedPreferences(UtilStrings.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        this.tourName = name;
        gson = new Gson();
        this.setTourZones(zones);
    }

    public ManagerTour(Context c, String name) {
        mCtx = c;
        prefs = mCtx.getSharedPreferences(UtilStrings.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        this.tourName = name;
        gson = new Gson();
    }

    public void setTourZones (ArrayList<SiteZone> zones) {
        String json = gson.toJson(zones);
        editor.putString(getName(), json).commit();
    }

    public void updateZones (SiteZone zone) {
        ArrayList<SiteZone> arrayList = getTourSiteZone();
        for (int i = 0;i<arrayList.size();i++) {
            if (arrayList.get(i).getZone_id() == zone.getZone_id()) {
                arrayList.set(i, zone);
                break;
            }
        }
        setTourZones(arrayList);
    }

    public ArrayList<SiteZone> getTourSiteZone() {
        ArrayList<SiteZone> arrayList;
        Gson gson = new Gson();
        String json = prefs.getString(getName(), null);
        Type type = new TypeToken<ArrayList<SiteZone>>() {}.getType();
        arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    public boolean allZonesChecked() {
        ArrayList<SiteZone> arrayList;
        Gson gson = new Gson();
        String json = prefs.getString(getName(), null);
        Type type = new TypeToken<ArrayList<SiteZone>>() {
        }.getType();
        arrayList = gson.fromJson(json, type);
        for (int i = 0; i < arrayList.size(); i++) {
            if (!arrayList.get(i).isChecked()) {
                return false;
            }
        }
        return true;
    }

    public int getCurrentCheckedZones(ArrayList<SiteZone> zones) {
        if (zones == null) return 0;
        int checked = 0;
        for (int i = 0;i<zones.size();i++) {
           if (zones.get(i).isChecked()){
               checked++;
           }
        }
        return checked;
    }

    public int getCurrentHazards(ArrayList<SiteZone> zones) {
        int hazards = 0;
        for (int i = 0;i<zones.size();i++) {
            if (zones.get(i).isChecked()){
                hazards = hazards + zones.get(i).getHazards();
            }
        }
        return hazards;
    }

    public String getName() {
        return  UtilStrings.PREFERENCES_SITE_ZONE + tourName;
    }

    //remove all zone check data when manager tour ends
    public void removeAll() {
        editor.putString(getName(), null).apply();
    }

}
