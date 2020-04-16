package com.seachange.healthandsafty.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seachange.healthandsafty.model.DBCheckComplete;
import com.seachange.healthandsafty.model.DBTourCheckModel;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.model.ZoneCheckHazard;
import com.seachange.healthandsafty.utils.UtilStrings;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DBTourCheckHelper {

    private Context mCtx;
    private SharedPreferences prefs;

    public DBTourCheckHelper() {

    }

    public DBTourCheckHelper(Context c) {
        this.mCtx = c;
        prefs = mCtx.getSharedPreferences("DB_prefs", Context.MODE_PRIVATE);
    }

    public void saveCurrentTourCheck(DBTourCheckModel model) {
        Gson gson = new Gson();
        prefs.edit().putString(UtilStrings.PREFERENCES_TOUR_CURRENT_DB_CHECK, gson.toJson(model)).commit();
    }

    public DBTourCheckModel getCurrentTourCheck() {
        Gson gson = new Gson();
        Type type = new TypeToken<DBTourCheckModel>() {}.getType();
        String modelStr = prefs.getString(UtilStrings.PREFERENCES_TOUR_CURRENT_DB_CHECK, null);
        if (modelStr !=null) {
            Logger.info("DB SITE TOUR: " + modelStr);
            return gson.fromJson(modelStr,type);
        }
        return null;
    }

    public void addZoneCheckComplete(String qrCode, Integer mZoneId, boolean synced) {
        if (qrCode == null) return;
        DBTourCheckModel mCurrentDBTourCheck = getCurrentTourCheck();
        DBZoneCheckModel tmpModel = mCurrentDBTourCheck.getExistingTourCheck(mZoneId);
        if (tmpModel != null) {
            for (int i = 0; i < mCurrentDBTourCheck.getZoneChecks().size(); i++) {
                DBZoneCheckModel tmp = mCurrentDBTourCheck.getZoneChecks().get(i);
                if (tmp.getZoneId() == tmpModel.getZoneId()) {
                    DBCheckComplete tmpDBComplete = new DBCheckComplete();
                    tmpDBComplete.setQrCodeId(qrCode);
                    tmpDBComplete.setTimeCompleted(getDateString());
                    mCurrentDBTourCheck.getZoneChecks().get(i).setSync(synced);
                    mCurrentDBTourCheck.getZoneChecks().get(i).setCompleteZoneCheckCommand(tmpDBComplete);
                }
            }
        }
        saveCurrentTourCheck(mCurrentDBTourCheck);
    }

    public void addZoneCheckUnsynced(Integer mZoneId) {
        DBTourCheckModel mCurrentDBTourCheck = getCurrentTourCheck();
        DBZoneCheckModel tmpModel = mCurrentDBTourCheck.getExistingTourCheck(mZoneId);
        if (tmpModel != null) {
            for (int i = 0; i < mCurrentDBTourCheck.getZoneChecks().size(); i++) {
                DBZoneCheckModel tmp = mCurrentDBTourCheck.getZoneChecks().get(i);
                if (tmp.getZoneId() == tmpModel.getZoneId()) {
                    mCurrentDBTourCheck.getZoneChecks().get(i).setSync(false);
                }
            }
        }
        saveCurrentTourCheck(mCurrentDBTourCheck);
    }

    public String getCurrentTourCheckStr() {
        return prefs.getString(UtilStrings.PREFERENCES_TOUR_CURRENT_DB_CHECK, null);
    }

    public DBZoneCheckModel getTourCheckModel (DBTourCheckModel dbTourCheckModel, Integer mZoneId ) {
        for (int i =0; i< dbTourCheckModel.getZoneChecks().size();i++) {
            if (dbTourCheckModel.getZoneChecks().get(i).getZoneId().equals(mZoneId)) {
                return dbTourCheckModel.getZoneChecks().get(i);
            }
        }
        return null;
    }

    private String getDateString(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return df.format(Calendar.getInstance().getTime());
    }

    public ArrayList<DBTourCheckModel> getUnsyncedTourChecks(String data) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DBTourCheckModel>>() {}.getType();
        ArrayList<DBTourCheckModel> arrayList = new ArrayList<>();
        Logger.info("DB SITE TOUR: get unsynced" + data);

        if (data!=null) {
            try {
                arrayList = gson.fromJson(data, type);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        return arrayList;
    }
}
