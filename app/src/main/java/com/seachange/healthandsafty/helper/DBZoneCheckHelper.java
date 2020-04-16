package com.seachange.healthandsafty.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.seachange.healthandsafty.model.DBCheckCancel;
import com.seachange.healthandsafty.model.DBCheckPause;
import com.seachange.healthandsafty.model.DBCheckResume;
import com.seachange.healthandsafty.model.DBCheckStart;
import com.seachange.healthandsafty.model.DBZoneCheckModel;
import com.seachange.healthandsafty.model.ZoneCheckHazard;
import com.seachange.healthandsafty.utils.UtilStrings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DBZoneCheckHelper {

    private Context mCtx;
    private JSONObject checkObject;
    private SharedPreferences prefs;
    private ArrayList<ZoneCheckHazard> mSelectHazards;
    private ArrayList<ZoneCheckHazard> mSelectHazardsV2;
    private ArrayList<DBCheckPause> mPauseList;
    private ArrayList<DBCheckResume> mReumeList;
    private ArrayList<DBCheckCancel> mCancelList;
    private ArrayList<DBCheckStart> mStartList;


    public DBZoneCheckHelper() {
        this.checkObject = new JSONObject();
        this.mSelectHazards = new ArrayList<>();
        this.mSelectHazardsV2 = new ArrayList<>();
        this.mStartList = new ArrayList<>();
        this.mReumeList = new ArrayList<>();
        this.mPauseList = new ArrayList<>();
        this.mCancelList = new ArrayList<>();

        addHazardsToObject();
        addHazardsToObjectV2();
        addStartCheckToObject();
        addResumeCheckToObject();
        addPauseCheckToObject();
        addCancelCheckToObject();
    }

    public DBZoneCheckHelper(Context c) {
        this.mCtx = c;
        prefs = mCtx.getSharedPreferences("DB_prefs", Context.MODE_PRIVATE);

        this.checkObject = getCurrentDBCheck();
        if (checkObject.has("addressHazardCommandModels")) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<ZoneCheckHazard>>() {
                }.getType();
                mSelectHazards = gson.fromJson(checkObject.getString("addressHazardCommandModels"), type);
            } catch (JSONException e) {
                e.printStackTrace();
                this.mSelectHazards = new ArrayList<>();
                addHazardsToObject();
            }
        } else {
            this.mSelectHazards = new ArrayList<>();
            addHazardsToObject();
        }

        if (checkObject.has("addressHazardCommandModelsV2")) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<ZoneCheckHazard>>() {
                }.getType();
                mSelectHazardsV2 = gson.fromJson(checkObject.getString("addressHazardCommandModelsV2"), type);
            } catch (JSONException e) {
                e.printStackTrace();
                this.mSelectHazardsV2 = new ArrayList<>();
                addHazardsToObjectV2();
            }
        } else {
            this.mSelectHazardsV2 = new ArrayList<>();
            addHazardsToObjectV2();
        }

        //init start objects
        if (checkObject.has("startZoneCheckCommands")) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<DBCheckStart>>() {
                }.getType();
                mStartList = gson.fromJson(checkObject.getString("startZoneCheckCommands"), type);
            } catch (JSONException e) {
                e.printStackTrace();
                this.mStartList = new ArrayList<>();
                addStartCheckToObject();
            }
        } else {
            this.mStartList = new ArrayList<>();
            addStartCheckToObject();
        }

        //init start objects
        if (checkObject.has("pauseZoneCheckCommands")) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<DBCheckPause>>() {
                }.getType();
                mPauseList = gson.fromJson(checkObject.getString("pauseZoneCheckCommands"), type);
            } catch (JSONException e) {
                e.printStackTrace();
                this.mPauseList = new ArrayList<>();
                addPauseCheckToObject();
            }
        } else {
            this.mPauseList = new ArrayList<>();
            addPauseCheckToObject();
        }

//        init resume
        if (checkObject.has("resumeZoneCheckCommands")) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<DBCheckResume>>() {
                }.getType();
                mReumeList = gson.fromJson(checkObject.getString("resumeZoneCheckCommands"), type);
            } catch (JSONException e) {
                e.printStackTrace();
                this.mReumeList = new ArrayList<>();
                this.addResumeCheckToObject();
            }
        } else {
            this.mReumeList = new ArrayList<>();
            this.addResumeCheckToObject();
        }


        if (checkObject.has("cancelZoneCheckCommands")) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<DBCheckCancel>>() {
                }.getType();
                mCancelList = gson.fromJson(checkObject.getString("cancelZoneCheckCommands"), type);
            } catch (JSONException e) {
                e.printStackTrace();
                this.mCancelList = new ArrayList<>();
                this.addCancelCheckToObject();
            }
        } else {
            this.mCancelList = new ArrayList<>();
            this.addCancelCheckToObject();
        }

        Logger.info("DB -> CHECKER DATA: " + this.checkObject.toString());
    }

    public void onStartCheck(Map<String, String> params) {
        addHashMapToObject(params);
    }

    public void onAddHazards(ArrayList<ZoneCheckHazard> hazards) {
        //new app will use mSelectHazardsV2
        for (ZoneCheckHazard hazard : hazards) {
            //check if type id is UUID
            if (hazard.getTypeId() != null && hazard.getTypeId().split("-").length == 5) {
                this.mSelectHazardsV2.add(hazard);
            } else {
                this.mSelectHazards.add(hazard);
            }
        }

        Logger.info("DB -> hazards " + this.mSelectHazards.toString());
        Logger.info("DB -> hazards " + this.mSelectHazardsV2.toString());
        addHazardsToObject();
        addHazardsToObjectV2();
    }

    public void onAddCancelEvent(DBCheckCancel cancel) {
        this.mCancelList.add(cancel);
        addCancelCheckToObject();
    }

    public void onAddResumeEvent(DBCheckResume resume) {
        this.mReumeList.add(resume);
        addResumeCheckToObject();
    }

    public void onAddPauseEvent(DBCheckPause pause) {
        this.mPauseList.add(pause);
        addPauseCheckToObject();
    }

    public void onAddStartEvent(DBCheckStart start) {
        this.mStartList.add(start);
        addStartCheckToObject();
    }

    public void onEndCheck(Map<String, String> params) {
        this.addHashMapToObject(params);
    }

    public void onAddData(Map<String, String> params) {
        this.addHashMapToObject(params);
    }

    private void addHashMapToObject(Map<String, String> params) {
        try {
            for (Object o : params.entrySet()) {
                Map.Entry pairs = (Map.Entry) o;
                checkObject.put(pairs.getKey().toString(), pairs.getValue().toString());
            }
            saveDBCheckToPref();
        } catch (Exception e) {
            Logger.info("DB -> start exception" + checkObject.toString());
            e.printStackTrace();
        }
        Logger.info("DB -> ADD DATA " + checkObject.toString());
    }

    private void addHazardsToObject() {
        try {
            Gson gson = new GsonBuilder().create();
            String listString = gson.toJson(mSelectHazards, new TypeToken<ArrayList<ZoneCheckHazard>>() {
            }.getType());
            JSONArray jsArray = new JSONArray(listString);
            checkObject.put("addressHazardCommandModels", jsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveDBCheckToPref();
        Logger.info("DB -> ADD HAZARDS " + checkObject.toString());
    }

    private void addHazardsToObjectV2() {
        try {
            Gson gson = new GsonBuilder().create();
            String listString = gson.toJson(mSelectHazardsV2, new TypeToken<ArrayList<ZoneCheckHazard>>() {
            }.getType());
            JSONArray jsArray = new JSONArray(listString);
            checkObject.put("addressHazardCommandModelsV2", jsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveDBCheckToPref();
        Logger.info("DB -> ADD HAZARDS V2" + checkObject.toString());
    }

    public void addStartCheckToObject() {
        try {
            Gson gson = new GsonBuilder().create();
            String listString = gson.toJson(mStartList, new TypeToken<ArrayList<DBCheckStart>>() {
            }.getType());
            JSONArray jsArray = new JSONArray(listString);
            checkObject.put("startZoneCheckCommands", jsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveDBCheckToPref();
        Logger.info("DB -> ADD Start " + checkObject.toString());
    }

    public void addPauseCheckToObject() {
        try {
            Gson gson = new GsonBuilder().create();
            String listString = gson.toJson(mPauseList, new TypeToken<ArrayList<DBCheckPause>>() {
            }.getType());
            JSONArray jsArray = new JSONArray(listString);
            checkObject.put("pauseZoneCheckCommands", jsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveDBCheckToPref();
        Logger.info("DB -> ADD Pause " + checkObject.toString());
    }

    public void addResumeCheckToObject() {
        try {
            Gson gson = new GsonBuilder().create();
            String listString = gson.toJson(mReumeList, new TypeToken<ArrayList<DBCheckResume>>() {
            }.getType());
            JSONArray jsArray = new JSONArray(listString);
            checkObject.put("resumeZoneCheckCommands", jsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveDBCheckToPref();
        Logger.info("DB -> ADD Resume " + checkObject.toString());
    }

    private void addCancelCheckToObject() {
        try {
            Gson gson = new GsonBuilder().create();
            String listString = gson.toJson(mCancelList, new TypeToken<ArrayList<DBCheckCancel>>() {
            }.getType());
            JSONArray jsArray = new JSONArray(listString);
            checkObject.put("cancelZoneCheckCommands", jsArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveDBCheckToPref();
        Logger.info("DB -> ADD Cancel " + checkObject.toString());
    }

    public void addComplete(String qrcode) {
        try {
            JSONObject tmp = new JSONObject();
            tmp.put("timeCompleted", getDateString());
            tmp.put("qrCodeId", qrcode);

            checkObject.put("completeZoneCheckCommand", tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveDBCheckToPref();
        Logger.info("DB -> ADD Complete " + checkObject.toString());

    }

    public String getCheckForDB() {
        return checkObject.toString();
    }

    public void resetInitCheckObject(String data) {
        setCurrentDBCheck(data);
        resetCurrentCheck();
    }

    public void saveDBCheckToPref() {
        setCurrentDBCheck(checkObject.toString());
    }

    public void resetDBCheck() {
        setCurrentDBCheck(null);
    }

    private JSONObject getCurrentDBCheck() {
        String tmp = prefs.getString(UtilStrings.PREFERENCES_CURRENT_DB_CHECK, null);
        Logger.info("current check string: " + tmp);
        if (tmp == null) return new JSONObject();
        else {
            try {
                return new JSONObject(tmp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new JSONObject();
    }

    private void setCurrentDBCheck(String check) {
        prefs.edit().putString(UtilStrings.PREFERENCES_CURRENT_DB_CHECK, check).commit();
        resetCurrentCheck();
    }

    public void resetCurrentCheck() {
        checkObject = getCurrentDBCheck();
    }

    public ArrayList<DBZoneCheckModel> getUnsyncedChecks(String data) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DBZoneCheckModel>>() {
        }.getType();
        ArrayList<DBZoneCheckModel> arrayList = new ArrayList<>();

        if (data != null) {
            try {
                arrayList = gson.fromJson(data, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return arrayList;
    }

    public ArrayList<ZoneCheckHazard> getZoneHazards(String data) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ZoneCheckHazard>>() {
        }.getType();
        ArrayList<ZoneCheckHazard> arrayList = new ArrayList<>();
        if (data != null) {
            arrayList = gson.fromJson(data, type);
        }
        return arrayList;
    }

    private String getDateString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return df.format(Calendar.getInstance().getTime());
    }

    public ArrayList<DBCheckStart> getmStartList() {
        return mStartList;
    }

    public void setmStartList(ArrayList<DBCheckStart> mStartList) {
        this.mStartList = mStartList;
    }
}
