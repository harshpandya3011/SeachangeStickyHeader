package com.seachange.healthandsafty.model;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.annotations.SerializedName;
import com.seachange.healthandsafty.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevinsong on 13/10/2017.
 */

//scan_type = 1, qrcode
//scan_type = 2, nfc

public class CaygoSite {

    @SerializedName("id")
    private int site_id;

    @SerializedName("name")
    private String site_name;

    @SerializedName("groupName")
    private String group_name;

    @SerializedName("logoUrl")
    private String icon_url;

    private int siteToursPerDay;

    @SerializedName("zones")
    public ArrayList<SiteZone> siteZones;

    @SerializedName("users")
    public List<UserData> siteUsers;

    @SerializedName("zoneCheckSettings")
    private ZoneCheckSettings checkSettings;

    @SerializedName("zoneCheckSettingsListV3")
    private ArrayList<ScheduleSetting> scheduleSetting;

    private int groupId;

    private SettingToggles toggles;

    @SerializedName("sector")
    private Sector sector;

    private Integer scan_type = 2;

    private boolean isUsingNfc;

    public CaygoSite() {

    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public ZoneCheckSettings getCheckSettings() {
        return checkSettings;
    }

    public void setCheckSettings(ZoneCheckSettings checkSettings) {
        this.checkSettings = checkSettings;
    }

    public ArrayList<ScheduleSetting> getScheduleSetting() {
        return scheduleSetting;
    }

    public void setScheduleSetting(ArrayList<ScheduleSetting> scheduleSetting) {
        this.scheduleSetting = scheduleSetting;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public boolean siteTypeNFC(Context mCtx) {
        if (BuildConfig.DEBUG) {
            boolean nfc = PreferenceManager.getDefaultSharedPreferences(mCtx).getBoolean("nfc_switch", false);
            if (nfc) return scan_type == 2;
            else return scan_type == 1;
        } else {
            return isUsingNfc;
        }
    }

    public SettingToggles getToggles() {
        return toggles;
    }

    public void setToggles(SettingToggles toggles) {
        this.toggles = toggles;
    }

    public boolean isUsingNfc() {
        return isUsingNfc;
    }

    public void setUsingNfc(boolean usingNfc) {
        isUsingNfc = usingNfc;
    }

    public boolean goToNFCSetupFlow(Context mCtx) {
        if (siteTypeNFC(mCtx)) {
            for (SiteZone zone : siteZones) {
                if (!zone.isTagSetup()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public class SettingToggles {

        boolean canManageUsers, canManageZoneSettings;
        public SettingToggles(){}

        public boolean isCanManageUsers() {
            return canManageUsers;
        }

        public void setCanManageUsers(boolean canManageUsers) {
            this.canManageUsers = canManageUsers;
        }

        public boolean isCanManageZoneSettings() {
            return canManageZoneSettings;
        }

        public void setCanManageZoneSettings(boolean canManageZoneSettings) {
            this.canManageZoneSettings = canManageZoneSettings;
        }
    }

    public ScheduleSetting getScheduleSettingByZoneId(int zoneId) {
        if (getScheduleSetting() == null) return null;
        if (getScheduleSetting().size() == 0) return null;
        else {
            for (ScheduleSetting tmp : getScheduleSetting()) {
                if (tmp.getZoneId() ==  zoneId) {
                    return tmp;
                }
            }
        }
        return null;
    }
}
