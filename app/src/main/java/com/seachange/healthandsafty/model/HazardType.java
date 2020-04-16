package com.seachange.healthandsafty.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by kevinsong on 12/09/2017.
 */
@Parcel
public class HazardType {

    @SerializedName("id")
    String type_id;

    @SerializedName("name")
    String type_name;

    @SerializedName("categoryName")
    String category;

    String riskName;

    boolean selected;

    public HazardType() {

    }

    public HazardType(String type_id, String type_name, String category) {
        this.type_id = type_id;
        this.type_name = type_name;
        this.category = category;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getRiskName() {
        return riskName;
    }

    public void setRiskName(String riskName) {
        this.riskName = riskName;
    }
}
