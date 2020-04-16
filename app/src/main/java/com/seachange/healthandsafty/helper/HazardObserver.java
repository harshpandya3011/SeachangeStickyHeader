package com.seachange.healthandsafty.helper;

import java.util.Observable;

/**
 * Created by kevinsong on 25/09/2017.
 */

public class HazardObserver extends Observable {

    private static HazardObserver mInstance = null;
    private boolean hazardChanged;
    private boolean sourceDataReceived;
    private boolean frequentlyDataReceived;
    private boolean siteDataReceived;
    private boolean scheduledReceived;
    private boolean statsReceived;
    private boolean jsaReceived;
    private boolean tokenValid;
    private boolean qrCodeFailed;
    private boolean checkEnded;
    private boolean checkEndedFailed;
    private boolean imagesChanged;
    private boolean raAdded;
    private boolean syncInProgress;
    private boolean syncComplete;
    private boolean syncEndWithError;
    private boolean syncTourInProgress;
    private boolean scheduleUpdated;

    private HazardObserver(){

    }

    public static HazardObserver getInstance(){
        if(mInstance == null){
            mInstance = new HazardObserver();
        }
        return mInstance;
    }

    public boolean isHazardChanged() {
        return hazardChanged;
    }

    public void setHazardChanged(boolean changed) {
        this.hazardChanged = changed;
        setChanged();
        if(hazardChanged){
            notifyObservers();
        }
    }

    public boolean isSourceDataReceived() {
        return sourceDataReceived;
    }

    public void setSourceDataReceived(boolean data) {
        this.sourceDataReceived = data;
        setChanged();
        if (sourceDataReceived){
            notifyObservers();
        }
    }

    public boolean isScheduledReceived() {
        return scheduledReceived;
    }

    public void setScheduledReceived(boolean data) {
        this.scheduledReceived = data;
        setChanged();
        if (scheduledReceived){
            notifyObservers();
        }
    }

    public boolean isSiteDataReceived() {
        return siteDataReceived;
    }

    public void setSiteDataReceived(boolean data) {
        this.siteDataReceived = data;
        setChanged();
        if (siteDataReceived){
            notifyObservers();
        }
    }

    public boolean isStatsReceived() {
        return statsReceived;
    }

    public void setStatsReceived(boolean data) {
        this.statsReceived = data;
        setChanged();
        if (statsReceived){
            notifyObservers();
        }
    }

    public boolean isJsaReceived() {
        return jsaReceived;
    }

    public void setJsaReceived(boolean data) {
        this.jsaReceived = data;
        setChanged();
        if (jsaReceived){
            notifyObservers();
        }
    }


    public boolean isFrequentlyDataReceived() {
        return frequentlyDataReceived;
    }

    public void setFrequentlyDataReceived(boolean data) {
        this.frequentlyDataReceived = data;
        setChanged();
        if (frequentlyDataReceived){
            notifyObservers();
        }
    }

    public boolean isTokenValid() {
        return tokenValid;
    }

    public void setTokenValid(boolean token) {
        this.tokenValid = token;
        setChanged();
        notifyObservers();
    }

    public boolean isQrCodeFailed() {
        return qrCodeFailed;
    }

    public void setQrCodeFailed (boolean qrCode) {
        this.qrCodeFailed = qrCode;
        setChanged();
        if (qrCodeFailed) {
            notifyObservers();
        }
    }

    public boolean isCheckEnded() {
        return checkEnded;
    }

    public void setCheckEnded (boolean end) {
        this.checkEnded = end;
        setChanged();
        if (checkEnded) {
            notifyObservers();
        }
    }

    public boolean isCheckEndedFailed() {
        return checkEndedFailed;
    }

    public void setCheckEndedFailed (boolean end) {
        this.checkEndedFailed = end;
        setChanged();
        if (checkEndedFailed) {
            notifyObservers();
        }
    }

    public boolean isImagesChanged() {
        return imagesChanged;
    }

    public void setImagesChanged(boolean imagesChanged) {
        this.imagesChanged = imagesChanged;
        setChanged();
        if (imagesChanged) {
            notifyObservers();
        }
    }

    public boolean isRaAdded() {
        return raAdded;
    }

    public void setRaAdded(boolean raAdded) {
        this.raAdded = raAdded;
        setChanged();
        if (raAdded) {
            notifyObservers();
        }
    }

    //sync

    public boolean isSyncTourInProgress() {
        return syncTourInProgress;
    }

    public void setSyncTourInProgress(boolean changed) {
        this.syncTourInProgress = changed;
        setChanged();
        if(syncTourInProgress){
            notifyObservers();
        }
    }

    public boolean isSyncInProgress() {
        return syncInProgress;
    }

    public void setSyncInProgress(boolean changed) {
        this.syncInProgress = changed;
        setChanged();
        if(syncInProgress){
            notifyObservers();
        }
    }

    public boolean isSyncComplete() {
        return syncComplete;
    }

    public void setSyncComplete(boolean changed) {
        if (syncTourInProgress) return;
        this.syncComplete = changed;
        this.syncTourInProgress = false;
        setChanged();
        if(syncComplete){
            notifyObservers();
        }
    }

    public boolean isScheduleUpdated() {
        return scheduleUpdated;
    }

    public void setScheduleUpdated(boolean changed) {
        this.scheduleUpdated = changed;
        setChanged();
        if(scheduleUpdated){
            notifyObservers();
        }
    }

    public boolean isSyncEndWithError() {
        return syncEndWithError;
    }

    public void setSyncEndWithError(boolean changed) {
        this.syncEndWithError = changed;
        setChanged();
        if(syncEndWithError){
            notifyObservers();
        }
    }

    public void resetSyncObserver() {
        syncInProgress = false;
        syncComplete = false;
        syncEndWithError = false;
        syncTourInProgress = false;
    }
}
