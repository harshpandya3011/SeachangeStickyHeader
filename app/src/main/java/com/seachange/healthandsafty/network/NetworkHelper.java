package com.seachange.healthandsafty.network;

import android.content.Context;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.seachange.healthandsafty.View.RequestErrorView;
import com.seachange.healthandsafty.application.SCApplication;
import com.seachange.healthandsafty.db.DBCheck;
import com.seachange.healthandsafty.db.DBCheckDatabase;
import com.seachange.healthandsafty.helper.HazardObserver;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.CheckTime;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.model.JSAModel;
import com.seachange.healthandsafty.model.SiteCheck;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.model.Stats;
import com.seachange.healthandsafty.utils.UtilStrings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;


/**
 * Created by kevinsong on 13/10/2017.
 */

public class NetworkHelper {

    private Context mCtx;
    private static String SITE_TAG = "site_tag";
    private static String SCHEDULE_TAG = "schedule_tag";
    private static String SOURCE_TAG = "source_tag";
    private static String STATS_TAG = "stats_tag";
    private static String FREQUENTLY_FOUND_TAG = "frequently_tag";
    private static String JSA_TAG = "jsa_tag";
    private int SCHEDULE_STATUS_NO_DATA = 1;
    private int SCHEDULE_STATUS_ERROR = 2;


    private String token;
    private JsonParser jsonParser;

    public NetworkHelper() {

    }

    public NetworkHelper(Context c) {
        this.mCtx = c;
        jsonParser = new JsonParser();
        token = "bearer " + PreferenceHelper.getInstance(mCtx).getToken();
        Logger.info("CURRENT TOKEN: " + token);
    }

    /**
     * * get site schedulesdata
     **/
    public void fetchSiteScheduleFromServer(final boolean notify) {
        JsonCallBack callBack = new JsonCallBack() {

            @Override
            public void callbackJSONObject(JSONObject result) {
                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        int statusCode = result.getInt(UtilStrings.STATUS_CODE);
                        if (statusCode == 304) {
                            Logger.info(SCHEDULE_TAG + ": statusCode 304 returned, no changes from server");
                            return;
                        } else if (statusCode == 200) {
                            if (result.has(UtilStrings.ETag)) {
                                PreferenceHelper.getInstance(mCtx).saveScheduleETag(result.getString(UtilStrings.PREFERENCES_SCHEDULE_ETAG));
                                Logger.info(SCHEDULE_TAG + ": have updates, response returned, update eTag and response");
                            }
                            if (result.has(UtilStrings.RESPONSE)) {
                                ArrayList<SiteCheck> array = jsonParser.getSiteTourChecks(result.getJSONObject(UtilStrings.RESPONSE));
                                SCApplication scApplication = (SCApplication) mCtx.getApplicationContext();
                                DBCheckDatabase database = DBCheckDatabase.getDatabase(scApplication);
                                if (array != null) {
                                    deleteSyncCompletedChecks(array, database);
                                }
                                PreferenceHelper.getInstance(mCtx).saveSiteScheduleData(array);
                                if (notify) {
                                    HazardObserver.getInstance().setScheduledReceived(true);
                                }
                                Logger.info(SCHEDULE_TAG + ": " + result.toString());
                            }
                        } else {
                            PreferenceHelper.getInstance(mCtx).setScheduleStatus(SCHEDULE_STATUS_NO_DATA);
                        }
                    } catch (JSONException e) {
                        Logger.info(SCHEDULE_TAG + ": " + e.toString());
                    }
                }
            }

            @Override
            public void callbackJsonArray(JSONArray result) {

            }

            @Override
            public void callbackErrorCalled(VolleyError result) {
                PreferenceHelper.getInstance(mCtx).setScheduleStatus(SCHEDULE_STATUS_ERROR);
                Logger.info(result.toString());
            }
        };

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String date = format.format(currentDate);
        CaygoSite caygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();
        if (caygoSite != null) {
            new VolleyJsonHelper(UtilStrings.CAYGO_ROOT_API + caygoSite.getSite_id() + "/schedule/" + date, SITE_TAG, callBack, mCtx).getJsonObjectFromVolleyHelper(token, PreferenceHelper.getInstance(mCtx).getScheduleETag());
        }
    }

    private void deleteSyncCompletedChecks(ArrayList<SiteCheck> array, DBCheckDatabase database) {
        Executors.newSingleThreadExecutor().submit(() -> {
            List<DBCheck> dbChecks = database.checkDao().getAllChecksSync();
            if (dbChecks == null) return;
            database.runInTransaction(() -> {
                Gson gson = new Gson();

                Iterator<DBCheck> iterator = dbChecks.iterator();
                while (iterator.hasNext()) {
                    DBCheck dbCheck = iterator.next();
                for (SiteCheck siteCheck : array) {
                    ArrayList<CheckTime> checkTimes = siteCheck.getCheckTimes();
                    if (checkTimes == null) continue;
                    for (CheckTime checkTime : checkTimes) {
                        ArrayList<SiteZone> scheduledCheckses = checkTime.getScheduledCheckses();
                        if (scheduledCheckses == null) continue;
                            DBZoneCheckModel dbZoneCheckModel = gson.fromJson(dbCheck.getZoneCheck(), DBZoneCheckModel.class);
                            if (Objects.equals(checkTime.getCheck_id(), dbZoneCheckModel.getScheduledZoneCheckTimeId())) {
                                for (SiteZone scheduledChecks : scheduledCheckses) {
                                    if (scheduledChecks.isChecked() && dbCheck.isSync() && dbZoneCheckModel.isComplete()
                                        && Objects.equals(dbZoneCheckModel.getZoneId(), scheduledChecks.getZone_id())) {
                                        Logger.info(SCHEDULE_TAG + ": deleting check id "+checkTime.getCheck_id()+" zone id "+dbZoneCheckModel.getZoneId());
                                        iterator.remove();
                                        database.checkDao().delete(dbCheck);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        });
    }

    public void fetchSiteScheduleFromServerWithCallBack(final boolean notify, RequestErrorView
        mView) {
        JsonCallBack callBack = new JsonCallBack() {

            @Override
            public void callbackJSONObject(JSONObject result) {
                mView.requestSucceed();
                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        int statusCode = result.getInt(UtilStrings.STATUS_CODE);
                        if (statusCode == 304) {
                            Logger.info(SCHEDULE_TAG + ": statusCode 304 returned, no changes from server");
                            return;
                        } else if (statusCode == 200) {
                            if (result.has(UtilStrings.ETag)) {
                                PreferenceHelper.getInstance(mCtx).saveScheduleETag(result.getString(UtilStrings.PREFERENCES_SCHEDULE_ETAG));
                                Logger.info(SCHEDULE_TAG + ": have updates, response returned, update eTag and response");
                            }
                            if (result.has(UtilStrings.RESPONSE)) {
                                ArrayList<SiteCheck> array = jsonParser.getSiteTourChecks(result.getJSONObject(UtilStrings.RESPONSE));
                                PreferenceHelper.getInstance(mCtx).saveSiteScheduleData(array);
                                if (notify) {
                                    HazardObserver.getInstance().setScheduledReceived(true);
                                }
                                Logger.info(SCHEDULE_TAG + ": " + result.toString());
                            }
                        } else {
                            PreferenceHelper.getInstance(mCtx).setScheduleStatus(SCHEDULE_STATUS_NO_DATA);
                        }
                    } catch (JSONException e) {
                        Logger.info(SCHEDULE_TAG + ": " + e.toString());
                    }
                }
            }

            @Override
            public void callbackJsonArray(JSONArray result) {

            }

            @Override
            public void callbackErrorCalled(VolleyError result) {
                PreferenceHelper.getInstance(mCtx).setScheduleStatus(SCHEDULE_STATUS_ERROR);
                mView.requestError(result);
            }
        };

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String date = format.format(currentDate);
        CaygoSite caygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();

        if (notify) {
            new VolleyJsonHelper(UtilStrings.CAYGO_ROOT_API + caygoSite.getSite_id() + "/schedule/" + date, SITE_TAG, callBack, mCtx, 20000).getJsonObjectFromVolleyHelper(token, PreferenceHelper.getInstance(mCtx).getScheduleETag());
        } else {
            new VolleyJsonHelper(UtilStrings.CAYGO_ROOT_API + caygoSite.getSite_id() + "/schedule/" + date, SITE_TAG, callBack, mCtx).getJsonObjectFromVolleyHelper(token, PreferenceHelper.getInstance(mCtx).getScheduleETag());
        }
    }

    public void fetchFrequentlyFoundFromServer(final boolean notify) {
        JsonCallBack callBack = new JsonCallBack() {

            @Override
            public void callbackJSONObject(JSONObject result) {
                HazardObserver.getInstance().setTokenValid(true);
                Logger.info(FREQUENTLY_FOUND_TAG + ": " + result.toString());

                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        int statusCode = result.getInt(UtilStrings.STATUS_CODE);
                        if (statusCode == 304) {
                            Logger.info(FREQUENTLY_FOUND_TAG + ": statusCode 304 returned, no changes from server");
                        } else if (statusCode == 200) {
                            if (result.has(UtilStrings.ETag)) {
                                PreferenceHelper.getInstance(mCtx).saveFrequentFoundETag(result.getString(UtilStrings.ETag));
                                Logger.info(FREQUENTLY_FOUND_TAG + ": have updates, response returned, update eTag and response");
                            }
                            if (result.has(UtilStrings.RESPONSE)) {
                                Gson gson = new Gson();
                                Type type = new TypeToken<ArrayList<SiteZone>>() {
                                }.getType();
                                ArrayList<SiteZone> array = gson.fromJson(result.getJSONArray(UtilStrings.RESPONSE).toString(), type);
                                PreferenceHelper.getInstance(mCtx).saveFrequentHazards(array);
                                if (notify) {
                                    HazardObserver.getInstance().setFrequentlyDataReceived(true);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void callbackJsonArray(JSONArray result) {

            }

            @Override
            public void callbackErrorCalled(VolleyError result) {
                Logger.info(result.toString());
                HazardObserver.getInstance().setTokenValid(false);
                if (result != null) {
                    if (result.networkResponse != null) {
                        if (result.networkResponse.statusCode == 401) {
                            HazardObserver.getInstance().setTokenValid(false);
                        }
                    }
                }
            }
        };
        CaygoSite caygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();
        new VolleyJsonHelper(UtilStrings.CAYGO_ROOT_API + caygoSite.getSite_id() + "/zones/?expand=frequentlyfoundhazards/v2", FREQUENTLY_FOUND_TAG, callBack, mCtx).getJsonObjectFromVolleyHelper(token, PreferenceHelper.getInstance(mCtx).getFrequentlyFoundETag());
    }

    /**
     * * get hazards library data
     **/
    public void fetchSourceDataFromServer(final boolean notify) {
        JsonCallBack callBack = new JsonCallBack() {

            @Override
            public void callbackJSONObject(JSONObject result) {
                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        int statusCode = result.getInt(UtilStrings.STATUS_CODE);
                        if (statusCode == 304) {
                            Logger.info(SOURCE_TAG + ": statusCode 304 returned, no changes from server");
                        } else if (statusCode == 200) {
                            if (result.has(UtilStrings.ETag)) {
                                PreferenceHelper.getInstance(mCtx).saveSourceAndTypeETag(result.getString(UtilStrings.ETag));
                                Logger.info(SOURCE_TAG + ": have updates, response returned, update eTag and response");
                            }
                            if (result.has(UtilStrings.RESPONSE)) {
                                PreferenceHelper.getInstance(mCtx).saveSourceAndTypeData(result.getJSONArray(UtilStrings.RESPONSE).toString());
                                if (notify) {
                                    HazardObserver.getInstance().setSourceDataReceived(true);
                                }
                                Logger.info(SOURCE_TAG + ": " + result.toString());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void callbackJsonArray(JSONArray result) {

            }

            @Override
            public void callbackErrorCalled(VolleyError result) {
                Logger.info(result.toString());
            }
        };
        //TODO set risk category risk id from api
        String riskCategoryId = "23d0b50f-36cf-452a-8753-088af86d1601";
        String riskId = "5ef08c37-58b1-4b20-8190-2fc280ab0c21";
        String sectorId = "59fa83e0-95b4-4c00-a817-eeb2b2e7c1af";
        CaygoSite siteData = PreferenceHelper.getInstance(mCtx).getSiteData();
        if (siteData != null && siteData.getSector() != null) {
            sectorId = siteData.getSector().getId();
        }

        new VolleyJsonHelper(
            UtilStrings.RA_RISK_CATEGORIES_API + riskCategoryId + "/risks/" + riskId + "/hazard-categories?sectorId=" + sectorId, SOURCE_TAG,
            callBack,
            mCtx
        ).getJsonObjectFromVolleyHelper(token, PreferenceHelper.getInstance(mCtx).getSourceAndTypeETag());
    }

    //
    //stats
    //
    public void fetchStatsDataFromServer(final boolean notify) {
        JsonCallBack callBack = new JsonCallBack() {

            @Override
            public void callbackJSONObject(JSONObject result) {
                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        int statusCode = result.getInt(UtilStrings.STATUS_CODE);
                        if (statusCode == 304) {
                            Logger.info(STATS_TAG + ": statusCode 304 returned, no changes from server");
                            return;
                        } else if (statusCode == 200) {
                            Logger.info(STATS_TAG + ": " + result.toString());
                            if (result.has(UtilStrings.ETag)) {
                                PreferenceHelper.getInstance(mCtx).saveStatsETag(result.getString(UtilStrings.PREFERENCES_STATS_ETAG));
                                Logger.info(STATS_TAG + ": have updates, response returned, update eTag and response");
                            }
                            if (result.has(UtilStrings.RESPONSE)) {
                                if (result.get(UtilStrings.RESPONSE) instanceof JsonObject) {
                                    if (result.getJSONObject(UtilStrings.RESPONSE) != null) {
                                        Stats mStats = jsonParser.getTourStats(result.getJSONObject(UtilStrings.RESPONSE));
                                        PreferenceHelper.getInstance(mCtx).saveStatsData(mStats);
                                        if (notify) {
                                            HazardObserver.getInstance().setStatsReceived(true);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Logger.info(SITE_TAG + e.toString());
                    }
                }
            }

            @Override
            public void callbackJsonArray(JSONArray result) {

            }

            @Override
            public void callbackErrorCalled(VolleyError result) {
                Logger.info(result.toString());
            }
        };
        CaygoSite caygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();
        String url = UtilStrings.CAYGO_ROOT_API + caygoSite.getSite_id() + "/statistics" + dateRange();
        Logger.info("STATS:" + url);

        new VolleyJsonHelper(url, STATS_TAG, callBack, mCtx).getJsonObjectFromVolleyHelper(token, PreferenceHelper.getInstance(mCtx).getStatsETag());
    }

    public void fetchStatsDataFromServerWithCallBack(final boolean notify, RequestErrorView
        mView) {
        JsonCallBack callBack = new JsonCallBack() {

            @Override
            public void callbackJSONObject(JSONObject result) {
                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        int statusCode = result.getInt(UtilStrings.STATUS_CODE);
                        if (statusCode == 304) {
                            Logger.info(STATS_TAG + ": statusCode 304 returned, no changes from server");
                            return;
                        } else if (statusCode == 200) {
                            Logger.info(STATS_TAG + ": " + result.toString());
                            if (result.has(UtilStrings.ETag)) {
                                PreferenceHelper.getInstance(mCtx).saveStatsETag(result.getString(UtilStrings.PREFERENCES_STATS_ETAG));
                                Logger.info(STATS_TAG + ": have updates, response returned, update eTag and response");
                            }
                            if (result.has(UtilStrings.RESPONSE)) {

                                if (result.getJSONObject(UtilStrings.RESPONSE) != null) {
                                    if (!result.getJSONObject(UtilStrings.RESPONSE).equals("null")) {
                                        Stats mStats = jsonParser.getTourStats(result.getJSONObject(UtilStrings.RESPONSE));
                                        PreferenceHelper.getInstance(mCtx).saveStatsData(mStats);
                                        if (notify) {
                                            HazardObserver.getInstance().setStatsReceived(true);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        Logger.info(SITE_TAG + ": " + e.toString());
                    }
                }
            }

            @Override
            public void callbackJsonArray(JSONArray result) {

            }

            @Override
            public void callbackErrorCalled(VolleyError result) {
                mView.requestError(result);
            }
        };
        CaygoSite caygoSite = PreferenceHelper.getInstance(mCtx).getSiteData();
        String url = UtilStrings.CAYGO_ROOT_API + caygoSite.getSite_id() + "/statistics" + dateRange();
        new VolleyJsonHelper(url, STATS_TAG, callBack, mCtx).getJsonObjectFromVolleyHelper(token, PreferenceHelper.getInstance(mCtx).getStatsETag());
    }

    //
    //sits data
    //
    public void fetchSiteJSAFromServer(final boolean notify) {
        JsonCallBack callBack = new JsonCallBack() {

            @Override
            public void callbackJSONObject(JSONObject result) {
                if (result.has(UtilStrings.STATUS_CODE)) {
                    try {
                        int statusCode = result.getInt(UtilStrings.STATUS_CODE);
                        if (statusCode == 304) {
                            Logger.info(JSA_TAG + ": statusCode 304 returned, no changes from server");
                            return;
                        } else if (statusCode == 200) {


                            if (result.has(UtilStrings.ETag)) {
                                PreferenceHelper.getInstance(mCtx).saveJsaETag(result.getString(UtilStrings.PREFERENCES_STATS_ETAG));
                                Logger.info(JSA_TAG + ": have updates, response returned, update eTag and response");
                            }
                            if (result.has(UtilStrings.RESPONSE)) {
                                JSAModel jsaModel = jsonParser.getSiteJsa(result.getJSONObject(UtilStrings.RESPONSE));
                                PreferenceHelper.getInstance(mCtx).saveSiteJsaData(jsaModel);
                                if (notify) {
                                    HazardObserver.getInstance().setJsaReceived(true);
                                }
                            }

                            Logger.info(JSA_TAG + ": " + result.toString());
                        }
                    } catch (JSONException e) {
                        Logger.info(JSA_TAG + ": " + e.toString());
                    }
                }
            }

            @Override
            public void callbackJsonArray(JSONArray result) {

            }

            @Override
            public void callbackErrorCalled(VolleyError result) {
                Logger.info(result.toString());
            }
        };

        new VolleyJsonHelper(UtilStrings.SITE_JSA_API, JSA_TAG, callBack, mCtx).getJsonObjectFromVolleyHelper(token, PreferenceHelper.getInstance(mCtx).getJsaETag());
    }

    private String dateRange() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        String max = format.format(today);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date date = calendar.getTime();
        String min = format.format(date);
        return "?timeMin=" + min + "&timeMax=" + max;
    }
}
