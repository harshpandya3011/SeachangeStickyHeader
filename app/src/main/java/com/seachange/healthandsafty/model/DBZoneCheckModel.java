package com.seachange.healthandsafty.model;


import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class DBZoneCheckModel {

    Integer zoneId, groupId;
    String zoneCheckId, scheduledZoneCheckTimeId, siteTourId;
    ArrayList<ZoneCheckHazard> addressHazardCommandModels =  new ArrayList<>();
    ArrayList<ZoneCheckHazard> addressHazardCommandModelsV2 =  new ArrayList<>();
    ArrayList<DBCheckPause> pauseZoneCheckCommands;
    ArrayList<DBCheckResume> resumeZoneCheckCommands;
    ArrayList<DBCheckCancel> cancelZoneCheckCommands;
    ArrayList<DBCheckStart> startZoneCheckCommands;

    DBCheckComplete completeZoneCheckCommand;
    boolean sync;

    public DBZoneCheckModel() {

    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getZoneCheckId() {
        return zoneCheckId;
    }

    public void setZoneCheckId(String zoneCheckId) {
        this.zoneCheckId = zoneCheckId;
    }

    public String getScheduledZoneCheckTimeId() {
        return scheduledZoneCheckTimeId;
    }

    public void setScheduledZoneCheckTimeId(String scheduledZoneCheckTimeId) {
        this.scheduledZoneCheckTimeId = scheduledZoneCheckTimeId;
    }


    public String getSiteTourId() {
        return siteTourId;
    }

    public void setSiteTourId(String siteTourId) {
        this.siteTourId = siteTourId;
    }

    public DBCheckComplete getCompleteZoneCheckCommand() {
        return completeZoneCheckCommand;
    }

    public void setCompleteZoneCheckCommand(DBCheckComplete completeZoneCheckCommand) {
        this.completeZoneCheckCommand = completeZoneCheckCommand;
    }

    public boolean isCurrentZoneCheck(String mZoneId, String mScheduledZoneCheckTimeId) {
        if(zoneId == null || scheduledZoneCheckTimeId == null ) return false;
        return zoneId.toString().equals(mZoneId) && scheduledZoneCheckTimeId.equals(mScheduledZoneCheckTimeId);
    }


    public ArrayList<ZoneCheckHazard> getAddressHazardCommandModels() {
        return addressHazardCommandModels;
    }

    public ArrayList<DBCheckPause> getPauseZoneCheckCommands() {
        return pauseZoneCheckCommands;
    }

    public void setPauseZoneCheckCommands(ArrayList<DBCheckPause> pauseZoneCheckCommands) {
        this.pauseZoneCheckCommands = pauseZoneCheckCommands;
    }

    public ArrayList<DBCheckResume> getResumeZoneCheckCommands() {
        return resumeZoneCheckCommands;
    }

    public void setResumeZoneCheckCommands(ArrayList<DBCheckResume> resumeZoneCheckCommands) {
        this.resumeZoneCheckCommands = resumeZoneCheckCommands;
    }

    public ArrayList<DBCheckCancel> getCancelZoneCheckCommands() {
        return cancelZoneCheckCommands;
    }

    public void setCancelZoneCheckCommands(ArrayList<DBCheckCancel> cancelZoneCheckCommands) {
        this.cancelZoneCheckCommands = cancelZoneCheckCommands;
    }

    public ArrayList<DBCheckStart> getStartZoneCheckCommands() {
        return startZoneCheckCommands;
    }

    public void setStartZoneCheckCommands(ArrayList<DBCheckStart> startZoneCheckCommands) {
        this.startZoneCheckCommands = startZoneCheckCommands;
    }

    //add events
    public void addStartZoneCheckCommands(DBCheckStart startCommands) {
        if (startZoneCheckCommands == null) startZoneCheckCommands = new ArrayList<>();
        this.startZoneCheckCommands.add(startCommands);
    }


    public void addResumeZoneCheckCommands(DBCheckResume mResumeZoneCheckCommands) {
        if (resumeZoneCheckCommands == null) resumeZoneCheckCommands = new ArrayList<>();
        this.resumeZoneCheckCommands.add(mResumeZoneCheckCommands);
    }

    public void addPauseZoneCheckCommands(DBCheckPause pauseCommands) {
        if (pauseZoneCheckCommands == null) pauseZoneCheckCommands = new ArrayList<>();
        this.pauseZoneCheckCommands.add(pauseCommands);
    }

    public void addCancelZoneCheckCommands(DBCheckCancel cancelCommands) {
        if (cancelZoneCheckCommands == null) cancelZoneCheckCommands = new ArrayList<>();
        this.cancelZoneCheckCommands.add(cancelCommands);
    }

    public void addHazardsZoneCheckCommands(ArrayList<ZoneCheckHazard> hazardZoneCheckCommands) {
        if (addressHazardCommandModelsV2 == null) addressHazardCommandModelsV2 = new ArrayList<>();
        this.addressHazardCommandModelsV2.addAll(hazardZoneCheckCommands);
    }

    public void convertHazardsZoneCheckCommandsV2() {
        addressHazardCommandModelsV2 = new ArrayList<>();
        this.addressHazardCommandModelsV2.addAll(addressHazardCommandModels);
        addressHazardCommandModels= new ArrayList<>();
    }

    public boolean isComplete() {
        if (completeZoneCheckCommand!=null) {
            return true;
        }
        return false;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public ArrayList<ZoneCheckHazard> getAddressHazardCommandModelsV2() {
        return addressHazardCommandModelsV2;
    }
}
