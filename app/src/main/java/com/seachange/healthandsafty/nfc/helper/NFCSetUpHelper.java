package com.seachange.healthandsafty.nfc.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seachange.healthandsafty.helper.Logger;
import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.model.CaygoSite;
import com.seachange.healthandsafty.model.NFCZoneTag;
import com.seachange.healthandsafty.model.SiteZone;
import com.seachange.healthandsafty.model.TagPoint;
import com.seachange.healthandsafty.nfc.model.NFCZoneData;
import com.seachange.healthandsafty.utils.Util;
import com.seachange.healthandsafty.utils.UtilStrings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class NFCSetUpHelper {

    private Context mCtx;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private CaygoSite mSite;

    public NFCSetUpHelper(Context c) {
        mCtx = c;
        prefs = mCtx.getSharedPreferences(UtilStrings.PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        mSite = PreferenceHelper.getInstance(mCtx).getSiteData();
    }

    public ArrayList<NFCZoneTag> getTagListToSync() {
        ArrayList<NFCZoneTag> arrayList = new ArrayList<>();

        ArrayList<SiteZone> mZones = getSiteZones();
        for (SiteZone siteZone : mZones) {
            NFCZoneTag nfcZoneTag = new NFCZoneTag();
            nfcZoneTag.setZoneId(siteZone.getZone_id());
            TagPoint a = new TagPoint();
            TagPoint b = new TagPoint();

            if (siteZone.isNfcPointASet()) {
                a = siteZone.getNfcPointA();
                b = siteZone.getNfcPointB();
            }

            nfcZoneTag.getSetupTagPoints().add(a);
            nfcZoneTag.getSetupTagPoints().add(b);

            arrayList.add(nfcZoneTag);
        }
        return arrayList;
    }

    public void setNFCSetUpChanged(boolean changed) {
        editor.putBoolean(UtilStrings.PREFERENCES_NFC_STATUS_CHANGED, changed).commit();
    }

    public boolean getNFCStatus() {
        return prefs.getBoolean(UtilStrings.PREFERENCES_NFC_STATUS_CHANGED, false);
    }

    public void setSiteZones(ArrayList<SiteZone> zones) {
        Gson gson = new Gson();
        String json = gson.toJson(zones);
        editor.putString(UtilStrings.PREFERENCES_NFC_ZONES, json).commit();
    }

    public ArrayList<SiteZone> getSiteZones() {
        ArrayList<SiteZone> arrayList = new ArrayList<>();
        Gson gson = new Gson();
        String json = prefs.getString(UtilStrings.PREFERENCES_NFC_ZONES, null);
        if (json !=null) {
            Type type = new TypeToken<ArrayList<SiteZone>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
        }
        return arrayList;
    }

    public void setCurrentZonePoint(SiteZone zone, Boolean isA, NFCZoneData zoneData) {
        ArrayList<SiteZone> zones = getSiteZones();
        if (zones == null) return;
        for (int i = 0; i < zones.size(); i++) {
            if (zones.get(i).getZone_id() == zone.getZone_id()) {
                if (isA) {
                    zones.get(i).setNfcPointASet(true);
                    TagPoint tagPoint = new TagPoint();
                    tagPoint.setTagSummaryId(zoneData.getId());
                    tagPoint.setSetupDate(zoneData.getDate());
                    zones.get(i).setNfcPointA(tagPoint);
                } else {
                    zones.get(i).setNfcPointBSet(true);
                    TagPoint tagPoint = new TagPoint();
                    tagPoint.setTagSummaryId(zoneData.getId());
                    tagPoint.setSetupDate(zoneData.getDate());
                    zones.get(i).setNfcPointB(tagPoint);
                }
            }
        }
        setSiteZones(zones);
    }

    public int getCurrentSetupZones(ArrayList<SiteZone> zones) {
        if (zones == null) return 0;
        int checked = 0;
        for (int i = 0; i < zones.size(); i++) {
            if ((zones.get(i).isNfcPointASet() && zones.get(i).isNfcPointBSet()) || zones.get(i).isTagSetup()) {
                checked++;
            }
        }
        return checked;
    }

    public NFCZoneData getDataFromNFCTag(String message) {
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<NFCZoneData>() {
            }.getType();
            return gson.fromJson(message, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public NFCZoneData getDataFromNFCTagURL(String message) {
        try {
//            if (isJSONValid(message)) {
//                parseDataFromTag(message);
//            } else {
//                Logger.info("NFC, not json format");
//            }

            message = message.trim();
            if (message.startsWith("seachange/caygo") || message.startsWith("enseachange/caygo")){
                Logger.info("readFromNFC: old tag url");
                String[] items = message.split("/");
                if (items.length == 8) {
                    NFCZoneData zoneData = new NFCZoneData();
                    zoneData.setSiteId(Integer.parseInt(items[2]));
                    zoneData.setSiteName(items[3]);
                    zoneData.setZoneId(Integer.parseInt(items[4]));
                    zoneData.setDate(new Util().getTodayDate());
                    zoneData.setZoneName(items[5]);
                    zoneData.setPoint(items[6]);
                    zoneData.setId(items[7]);
                    return zoneData;
                } else {
                    return null;
                }
            } else if(message.startsWith("sc//caygo") || message.startsWith("ensc//caygo")) {
                Logger.info("readFromNFC: new tag url");
                String[] items = message.split("//");
                if (items.length == 8) {
                    NFCZoneData zoneData = new NFCZoneData();
                    zoneData.setSiteId(Integer.parseInt(items[2]));
                    zoneData.setSiteName(items[3]);
                    zoneData.setZoneId(Integer.parseInt(items[4]));
                    zoneData.setDate(new Util().getTodayDate());
                    zoneData.setZoneName(items[5]);
                    zoneData.setPoint(items[6]);
                    zoneData.setId(items[7]);
                    return zoneData;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.info("readFromNFCexc: " + message);
        }
        return null;
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private void parseDataFromTag(String message) {
        Gson g = new Gson();
        Object p = g.fromJson(message, Object.class);
        if (p instanceof NFCZoneData) {
            Logger.info("NFC, our data tag");
        } else {
            Logger.info("NFC, not our data tag");
        }
    }

    public String createNFCPointJsonObject(SiteZone zone, String point) {
        if (mSite == null) return null;
        String id = zone.getQrCodeIdA();
        if (point.equals("B")){
            id = zone.getQrCodeIdB();
        }
        NFCZoneData mData = new NFCZoneData();
        mData.setSiteId(mSite.getSite_id());
        mData.setSiteName(mSite.getSite_name());
        mData.setZoneId(zone.getZone_id());
        mData.setZoneName(zone.getZone_name());
        mData.setPoint(point);
        mData.setDate(new Util().getTodayDate());
        mData.setId(id);
//        Gson gson = new Gson();
//        String t = gson.toJson(mData);
//        String number = String.valueOf(mData);
//        return t;
        return "sc//caygo//" + mSite.getSite_id() + "//" + mSite.getSite_name() + "//" + zone.getZone_id() + "//" + zone.getZone_name() + "//" + point + "//" + id;
    }


    public ArrayList<NFCZoneData> getSiteNFCZones() {
        ArrayList<NFCZoneData> arrayList = new ArrayList<>();
        Gson gson = new Gson();
        String json = prefs.getString(UtilStrings.PREFERENCES_NFC_ZONES_DATA, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<NFCZoneData>>() {
            }.getType();
            arrayList = gson.fromJson(json, type);
        }
        return arrayList;
    }
}
