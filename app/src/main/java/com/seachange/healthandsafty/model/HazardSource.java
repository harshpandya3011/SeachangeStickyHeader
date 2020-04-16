package com.seachange.healthandsafty.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by kevinsong on 12/09/2017.
 */
@Parcel
public class HazardSource {

    @SerializedName("id")
    String source_id;

    @SerializedName("name")
    String source_name;

    public ArrayList<HazardType> hazardTypes;

    public HazardSource() {

    }

    public HazardSource(String source_id, String source_name, ArrayList<HazardType> hazardTypes) {
        this.source_id = source_id;
        this.source_name = source_name;
        this.hazardTypes = hazardTypes;
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public ArrayList<HazardType> getHazardTypes() {
        return hazardTypes;
    }

    public void setHazardTypes(ArrayList<HazardType> hazardTypes) {
        this.hazardTypes = hazardTypes;
    }
}
