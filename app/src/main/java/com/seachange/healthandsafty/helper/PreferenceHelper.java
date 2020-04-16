package com.seachange.healthandsafty.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seachange.healthandsafty.View.RequestErrorView;
import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.CheckTime;
import com.seachange.healthandsafty.model.GalleryImage;
import com.seachange.healthandsafty.model.HazardSource;
import com.seachange.healthandsafty.model.JSAModel;
import com.seachange.healthandsafty.model.SiteCheck;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.model.Stats;
import com.seachange.healthandsafty.network.NetworkHelper;
import com.seachange.healthandsafty.utils.UtilStrings;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kevinsong on 12/09/2017.
 */

public class PreferenceHelper {

    private static PreferenceHelper instance;
    private Context mCtx;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private NetworkHelper mNetworkHelper;
//    private CaygoSite caygoSite;

    private PreferenceHelper() {

    }

    public static PreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceHelper();
            instance.init(context);
        }
        return instance;
    }

    private void init(Context c) {
        mCtx = c;
        prefs = mCtx.getSharedPreferences(UtilStrings.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        mNetworkHelper = new NetworkHelper(mCtx);
    }

    public PreferenceHelper(Context c) {
        mCtx = c;
        prefs = mCtx.getSharedPreferences(UtilStrings.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        mNetworkHelper = new NetworkHelper(mCtx);
    }

    private void updatePrefs() {
        prefs = mCtx.getSharedPreferences(UtilStrings.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        mNetworkHelper = new NetworkHelper(mCtx);
        Logger.info("update Prefs");
    }

    public void addHazardsWithNumber(int hazards) {
        prefs.edit().putInt(UtilStrings.PREFERENCES_CURRENT_HAZARDS, getCurrentHazards() + hazards).apply();
    }

    public int getCurrentHazards() {
        return prefs.getInt(UtilStrings.PREFERENCES_CURRENT_HAZARDS, 0);
    }

    public void setCurrentHazards(int hazards) {
        prefs.edit().putInt(UtilStrings.PREFERENCES_CURRENT_HAZARDS, hazards).apply();
    }

    public int getScheduleStatus() {
        return prefs.getInt(UtilStrings.PREFERENCES_SCHEDULE_STATUS, 0);
    }

    public void setScheduleStatus(int status) {
        prefs.edit().putInt(UtilStrings.PREFERENCES_SCHEDULE_STATUS, status).apply();
    }

    public void resetCurrentHazards() {
        prefs.edit().putInt(UtilStrings.PREFERENCES_CURRENT_HAZARDS, 0).apply();
    }

    public void fetchDataFromServer() {
        mNetworkHelper.fetchSourceDataFromServer(false);
        mNetworkHelper.fetchSiteScheduleFromServer(false);
        mNetworkHelper.fetchStatsDataFromServer(false);
//        mNetworkHelper.fetchFrequentlyFoundFromServer(false);
//        mNetworkHelper.fetchSiteJSAFromServer(false);
    }

    public void fetchScheduledData() {
        mNetworkHelper.fetchSiteScheduleFromServer(false);
    }

    public void fetchScheduledDataNotify() {
        mNetworkHelper.fetchSiteScheduleFromServer(true);
    }

    public void fetchScheduledDataNotifyWithCallBack(RequestErrorView mView) {
        mNetworkHelper.fetchSiteScheduleFromServerWithCallBack(true, mView);
    }

    private String AssetJSONFile() {
        String json;
        try {
            InputStream is = mCtx.getAssets().open("library.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void saveSourceAndTypeData(String hazardCategoriesResponses) {
        editor.putString(UtilStrings.PREFERENCES_SOURCE_AND_TYPE, hazardCategoriesResponses).apply();
    }

    public void saveFrequentHazards(ArrayList<SiteZone> arrayList) {
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(UtilStrings.PREFERENCES_FREQUENT_HAZARDS, json).apply();
    }

    public ArrayList<HazardSource> getSourceAndTypeData() {
        String json = prefs.getString(UtilStrings.PREFERENCES_SOURCE_AND_TYPE, null);
        //if couldn't find any data saved on device, need to send request to sever to get data.
        if (json == null) {
            mNetworkHelper.fetchSourceDataFromServer(true);
            Logger.info("Hazard Source no data found on device, need to get up to server get data");
            return null;
        } else {
            Logger.info("Hazard Source data found on device");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<HazardSource>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
    }

    public SiteZone getFrequentlyFoundByZone(int zId) {
        ArrayList<SiteZone> arrayList;
        Gson gson = new Gson();
        String json = prefs.getString(UtilStrings.PREFERENCES_FREQUENT_HAZARDS, null);
        Logger.info("this is the zone data: " + json);
        if (json == null) {
            mNetworkHelper.fetchFrequentlyFoundFromServer(true);
            Logger.info("no frequently data found on device, need to get up to server get data");
        } else {
            Type type = new TypeToken<ArrayList<SiteZone>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);

            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).getZone_id() == zId) {
                    return arrayList.get(i);
                }
            }
            Logger.info("frequently data found on device");
        }
        return null;
    }

    public void saveSiteData(CaygoSite caygoSite) {
        Gson gson = new Gson();
        String json = gson.toJson(caygoSite);
        editor.putString(UtilStrings.PREFERENCES_CAYGO_SITE, json).apply();
    }

    public CaygoSite getSiteData() {
        CaygoSite caygoSite;
        Gson gson = new Gson();
        String json = prefs.getString(UtilStrings.PREFERENCES_CAYGO_SITE, null);

        //if couldn't find any data saved on device, need to send request to sever to get data.
        if (json == null) {
            Logger.info("no site data found on device, need to get up to server get data");
            return null;
        } else {
            Logger.info("site data found on device");
        }
        Type type = new TypeToken<CaygoSite>() {
        }.getType();
        caygoSite = gson.fromJson(json, type);

        return caygoSite;
    }

    public void saveStatsData(Stats stats) {
        Gson gson = new Gson();
        String json = gson.toJson(stats);
        editor.putString(UtilStrings.PREFERENCES_STATS, json).apply();
    }

    public Stats getStatsData() {
        Stats stats = null;
        Gson gson = new Gson();
        String json = prefs.getString(UtilStrings.PREFERENCES_STATS, null);

        //if couldn't find any data saved on device, need to send request to sever to get data.
        if (json == null) {
            mNetworkHelper.fetchStatsDataFromServer(true);
            Logger.info("no stats data found on device, need to get up to server get data");
        } else {
            Type type = new TypeToken<Stats>() {
            }.getType();
            stats = gson.fromJson(json, type);
            Logger.info("stats data found on device");
        }
        return stats;
    }

    public void saveSiteJsaData(JSAModel data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(UtilStrings.PREFERENCES_JSA, json).apply();
    }

    public JSAModel getSiteJsaData() {
        JSAModel jsa = null;
        Gson gson = new Gson();
        String json = prefs.getString(UtilStrings.PREFERENCES_JSA, null);

        //if couldn't find any data saved on device, need to send request to sever to get data.
        if (json == null) {
            mNetworkHelper.fetchSiteJSAFromServer(true);
            Logger.info("no jsa data found on device, need to get up to server get data");
        } else {
            Type type = new TypeToken<JSAModel>() {
            }.getType();
            jsa = gson.fromJson(json, type);
            Logger.info("jsa data found on device");
        }
        return jsa;
    }


    public void saveSiteScheduleData(ArrayList<SiteCheck> array) {
        Gson gson = new Gson();
        String json = gson.toJson(array);
        editor.putString(UtilStrings.PREFERENCES_SITE_SCHEDULE, json).commit();
    }

    public ArrayList<SiteCheck> getSiteScheduleData() {
        ArrayList<SiteCheck> arrayList = null;
        Gson gson = new Gson();
        String json = prefs.getString(UtilStrings.PREFERENCES_SITE_SCHEDULE, null);

        //if couldn't find any data saved on device, need to send request to sever to get data.
        if (json == null) {
            mNetworkHelper.fetchSiteScheduleFromServer(true);
            Logger.info("SCHEDULE + no site data found on device, need to get up to server get data" + json);
        } else {
            Type type = new TypeToken<ArrayList<SiteCheck>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
            Logger.info("SCHEDULE + site data found on device" + json);
        }
        return arrayList;
    }

    public ArrayList<SiteCheck> getSiteScheduleDataWithOutRefresh() {
        ArrayList<SiteCheck> arrayList = null;
        Gson gson = new Gson();
        String json = prefs.getString(UtilStrings.PREFERENCES_SITE_SCHEDULE, null);
        Type type = new TypeToken<ArrayList<SiteCheck>>() {}.getType();
        arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    public void fetchStatsDataWithCallBack(RequestErrorView mView) {
        mNetworkHelper.fetchStatsDataFromServerWithCallBack(true, mView);
    }

    //
    //handles for all end points eTag
    //
    public void saveSourceAndTypeETag(String eTag) {
        editor.putString(UtilStrings.PREFERENCES_SOURCE_ETAG, eTag).apply();
    }

    public String getSourceAndTypeETag() {
        return prefs.getString(UtilStrings.PREFERENCES_SOURCE_ETAG, null);
    }

    public void saveFrequentFoundETag(String eTag) {
        editor.putString(UtilStrings.PREFERENCES_FREQUENT_ETAG, eTag).apply();
    }

    public String getFrequentlyFoundETag() {
        return prefs.getString(UtilStrings.PREFERENCES_FREQUENT_ETAG, null);
    }

    public void saveSiteETag(String eTag) {
        editor.putString(UtilStrings.PREFERENCES_SITE_ETAG, eTag).apply();
    }

    public String getSiteETag() {
        return prefs.getString(UtilStrings.PREFERENCES_SITE_ETAG, null);
    }

    public void saveScheduleETag(String eTag) {
        editor.putString(UtilStrings.PREFERENCES_SCHEDULE_ETAG, eTag).apply();
    }

    public String getScheduleETag() {
        return prefs.getString(UtilStrings.PREFERENCES_SCHEDULE_ETAG, null);
    }

    public void saveStatsETag(String eTag) {
        editor.putString(UtilStrings.PREFERENCES_STATS_ETAG, eTag).apply();
    }

    public String getStatsETag() {
        return prefs.getString(UtilStrings.PREFERENCES_STATS_ETAG, null);
    }

    public void saveJsaETag(String eTag) {
        editor.putString(UtilStrings.PREFERENCES_JSA_ETAG, eTag).apply();
    }

    public String getJsaETag() {
        return prefs.getString(UtilStrings.PREFERENCES_JSA_ETAG, null);
    }

    public void saveToken(String token) {
        editor.putString(UtilStrings.PREFERENCES_Token, token).commit();
        updatePrefs();
    }

    public void saveUpdatedToken(String token) {
        editor.putString(UtilStrings.PREFERENCES_Token, token).commit();
        updatePrefs();
    }

    public String getToken() {
        return prefs.getString(UtilStrings.PREFERENCES_Token, null);
    }

    public void saveRefreshToken(String token) {
        editor.putString(UtilStrings.PREFERENCES_REFRESH_TOKEN, token).commit();
        updatePrefs();
    }

    public String getRefreshToken() {
        return prefs.getString(UtilStrings.PREFERENCES_REFRESH_TOKEN, null);
    }

    public void saveUserEmail(String email) {
        editor.putString(UtilStrings.USER_EMAIL, email).apply();
    }

    public String getUserEmail() {
        return prefs.getString(UtilStrings.USER_EMAIL, null);
    }

    public String requestToken() {
        return "bearer " + PreferenceHelper.getInstance(mCtx).getToken();
    }

    public void clearPreference() {
        editor.clear();
        editor.commit();
    }

    public SiteZone getSiteZoneById(Integer zone_id) {

        SiteZone temp = new SiteZone();
        ArrayList<SiteZone> zones = getSiteData().siteZones;
        for (SiteZone zone : zones) {
            if (zone.getZone_id() == zone_id)
                return zone;
        }
        return temp;
    }

    public void saveRAFlow(Integer flow) {
        editor.putInt(UtilStrings.RA_FLOW, flow).apply();
    }

    public Integer getRAFlow() {
        return prefs.getInt(UtilStrings.RA_FLOW, 0);
    }

    public void saveImageTakenData(ArrayList<GalleryImage> array) {
        Gson gson = new Gson();
        String json = gson.toJson(array);
        editor.putString(UtilStrings.RA_IMAGES, json).apply();
    }

    public ArrayList<GalleryImage> getSavedImages() {
        Gson gson = new Gson();
        String json = prefs.getString(UtilStrings.RA_IMAGES, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<GalleryImage>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void saveShowTourQRFailed(boolean result) {
        editor.putBoolean(UtilStrings.TOUR_QR_CODE, result).commit();
    }

    public boolean getShowTourQRFailed() {
        return prefs.getBoolean(UtilStrings.TOUR_QR_CODE, false);
    }

    public void saveUserSettingOption(int tmp) {
        editor.putInt(UtilStrings.PREFERENCES_USER_OPTION, tmp).commit();
    }

    public Integer getuserSettingOption() {
        return prefs.getInt(UtilStrings.PREFERENCES_USER_OPTION, 1);
    }

    public void saveUserName(String name) {
        editor.putString(UtilStrings.PREFERENCES_USER_NAME, name).commit();
    }

    public String getUserName() {
        return prefs.getString(UtilStrings.PREFERENCES_USER_NAME, null);
    }

    public void setHomeScreenRefresh(boolean refresh) {
        editor.putBoolean(UtilStrings.HOME_REFRESH, refresh).commit();
    }

    public boolean getHomeScreenRefresh() {
        return prefs.getBoolean(UtilStrings.HOME_REFRESH, false);
    }

    public void setZoneCheckedInScheduleInPrefs(CheckTime mCurrentCheck, SiteZone mZone) {
        ArrayList<SiteCheck> siteChecks = PreferenceHelper.getInstance(mCtx).getSiteScheduleData();
        if (siteChecks != null && mCurrentCheck != null) {
            for (int i = 0; i < siteChecks.size(); i++) {
                if (siteChecks.get(i).isToday()) {
                    for (CheckTime c : siteChecks.get(i).getCheckTimes()) {
                        if (c.getCheck_id().equals(mCurrentCheck.getCheck_id())) {
                            c.setScheduledCheckses(mCurrentCheck.getScheduledCheckses());
                            for (SiteZone zone : c.getScheduledCheckses()) {
                                if (zone.getZone_id() == mZone.getZone_id()) {
                                    zone.setChecked(true);
                                    zone.setZoneCheckId(mZone.getZoneCheckId());
                                    zone.setHazards(mZone.getHazards());
                                }
                            }
                            Logger.info("CHECK: update prefs found");
                        }
                    }
                }
            }
        }

        PreferenceHelper.getInstance(mCtx).saveSiteScheduleData(siteChecks);
    }

    public void setLastSyncedTime() {
        try {
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm a", Locale.getDefault());
            String time = sdf.format(date);
            editor.putString(UtilStrings.OFFLINE_SYNC_TIME, time).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLastSyncedTime() {
        return prefs.getString(UtilStrings.OFFLINE_SYNC_TIME, null);
    }

    public void saveNFCSetupSyncFailed(boolean requestFailed) {
        editor.putBoolean(UtilStrings.NFC_SETUP_FAILED, requestFailed).apply();
    }

    public boolean getNFCSetupSyncStatus() {
        return prefs.getBoolean(UtilStrings.NFC_SETUP_FAILED, false);
    }
}
