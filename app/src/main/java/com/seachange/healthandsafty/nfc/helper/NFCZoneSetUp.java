package com.seachange.healthandsafty.nfc.helper;

import com.seachange.healthandsafty.model.SiteZone;

public class NFCZoneSetUp {

    private static NFCZoneSetUp mNfcZoneSetUp;
    private boolean pointASetup, pointBSetup, tagSet;
    private String point;

    private NFCZoneSetUp(){}

    public static NFCZoneSetUp getInstance(){
        if (mNfcZoneSetUp == null){
            mNfcZoneSetUp = new NFCZoneSetUp();
        }
        return mNfcZoneSetUp;
    }

    public void initSetUp(SiteZone zone) {
        setPointASetup(zone.isNfcPointASet());
        setPointBSetup(zone.isNfcPointBSet());
    }

    public void setUpReset() {
        setPointASetup(false);
        setPointBSetup(false);
    }

    public boolean isTagSet() {
        return tagSet;
    }

    public void setTagSet(boolean tagSet) {
        this.tagSet = tagSet;
    }

    public boolean isPointASetup() {
        return pointASetup;
    }

    public void setPointASetup(boolean pointASetup) {
        this.pointASetup = pointASetup;
    }

    public boolean isPointBSetup() {
        return pointBSetup;
    }

    public void setPointBSetup(boolean pointBSetup) {
        this.pointBSetup = pointBSetup;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
