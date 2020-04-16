package com.seachange.healthandsafty.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import com.seachange.healthandsafty.model.UserData;
import com.seachange.healthandsafty.utils.UtilStrings;

/**
 * Created by kevinsong on 17/01/2018.
 */

public class CheckPreference {

    private Context mCtx;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Gson gson;


    public CheckPreference(){

    }

    public CheckPreference(Context c){
        this.mCtx = c;
        initReference();
        gson = new Gson();
    }

    private void initReference(){
        prefs = mCtx.getSharedPreferences(UtilStrings.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveZoneUUID(String token) {
        editor.putString(UtilStrings.PREFERENCES_ZONE_CHECK_UUID, token).apply();
    }

    public String getZoneUUID() {
        return prefs.getString(UtilStrings.PREFERENCES_ZONE_CHECK_UUID, null);
    }

    public void saveTourUUID(String token) {
        editor.putString(UtilStrings.PREFERENCES_TOUR_CHECK_UUID, token).apply();
    }

    public String getTourUUID() {
        return prefs.getString(UtilStrings.PREFERENCES_TOUR_CHECK_UUID, null);
    }

    public void saveCaygoUser(UserData user) {
        String json = gson.toJson(user);
        editor.putString(UtilStrings.PREFERENCES_CAYGO_USER, json).apply();
    }

    public UserData getCurrentCaygoUser() {
        String userString = prefs.getString(UtilStrings.PREFERENCES_CAYGO_USER, null);
        return gson.fromJson(userString, UserData.class);
    }

    public void saveTimeSpendOnZoneCheck(long secs) {
        editor.putLong(UtilStrings.PREFERENCES_CAYGO_CHECK_TIME_SPEND, secs).apply();
    }

    public String getTimeSpendOnZoneCheck() {
        long secs = prefs.getLong(UtilStrings.PREFERENCES_CAYGO_CHECK_TIME_SPEND, 0);
        if (secs>3600){
            return String.format("%02d", (secs % 86400) / 3600)+ "h " + String.format("%02d", (secs % 3600) / 60) + "m "+ String.format("%02d",  secs % 60)+"s";
        } else {
            return String.format("%02d", (secs % 3600) / 60) + "m "+ String.format("%02d",  secs % 60)+"s";
        }
    }

    public void saveStartedQRCode(String qrCode) {
        editor.putString(UtilStrings.PREFERENCES_QRCODE, qrCode).apply();
    }

    public String getStaredQRCode() {
        return prefs.getString(UtilStrings.PREFERENCES_QRCODE, null);
    }
}
