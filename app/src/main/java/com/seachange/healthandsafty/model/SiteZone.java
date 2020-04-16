package com.seachange.healthandsafty.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by kevinsong on 05/10/2017.
 */
@Parcel
public class SiteZone {


    @SerializedName("id")
    int zone_id;

    @SerializedName("name")
    String zone_name;

    @SerializedName("qrCodePointAId")
    String qrCodeIdA;

    @SerializedName("qrCodePointBId")
    String qrCodeIdB;

    @SerializedName(value = "frequentlyFoundHazardsV2", alternate = "frequentlyFoundHazards")
    ArrayList<HazardType> hazardTypes;

    boolean checked, isScheduled;
    int hazards = 0;
    int checkId, status;
    String timeCompleted, zoneCheckId;
    boolean nfcPointASet;
    boolean nfcPointBSet;
    boolean isTagSetup;
    Boolean hasRegularSchedule;

    TagPoint nfcPointA, nfcPointB;

    public SiteZone() {

    }

    public SiteZone(boolean check) {
        this.checked = check;
    }

    public SiteZone(int tmp) {
        this.checked = true;
        this.hazards = tmp;
    }

    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public String getZone_name() {
        return zone_name;
    }

    public void setZone_name(String zone_name) {
        this.zone_name = zone_name;
    }

    public ArrayList<HazardType> getHazardTypes() {
        return hazardTypes;
    }

    public void setHazardTypes(ArrayList<HazardType> hazardTypes) {
        this.hazardTypes = hazardTypes;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getHazards() {
        return hazards;
    }

    public void setHazards(int hazards) {
        this.hazards = hazards;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public String getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(String timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public String getQrCodeIdA() {
        return qrCodeIdA;
    }

    public void setQrCodeIdA(String qrCodeIdA) {
        this.qrCodeIdA = qrCodeIdA;
    }

    public String getQrCodeIdB() {
        return qrCodeIdB;
    }

    public void setQrCodeIdB(String qrCodeIdB) {
        this.qrCodeIdB = qrCodeIdB;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getZoneCheckId() {
        return zoneCheckId;
    }

    public void setZoneCheckId(String zoneCheckId) {
        this.zoneCheckId = zoneCheckId;
    }

    public boolean isNfcPointASet() {
        return nfcPointASet;
    }

    public void setNfcPointASet(boolean nfcPointASet) {
        this.nfcPointASet = nfcPointASet;
    }

    public boolean isNfcPointBSet() {
        return nfcPointBSet;
    }

    public void setNfcPointBSet(boolean nfcPointBSet) {
        this.nfcPointBSet = nfcPointBSet;
    }

    public TagPoint getNfcPointA() {
        return nfcPointA;
    }

    public void setNfcPointA(TagPoint nfcPointA) {
        this.nfcPointA = nfcPointA;
    }

    public TagPoint getNfcPointB() {
        return nfcPointB;
    }

    public void setNfcPointB(TagPoint nfcPointB) {
        this.nfcPointB = nfcPointB;
    }

    public boolean isTagSetup() {
        return isTagSetup;
    }

    public void setTagSetup(boolean tagSetup) {
        isTagSetup = tagSetup;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public Boolean getHasRegularSchedule() {
        return hasRegularSchedule;
    }

    public void setHasRegularSchedule(Boolean hasRegularSchedule) {
        this.hasRegularSchedule = hasRegularSchedule;
    }
}
