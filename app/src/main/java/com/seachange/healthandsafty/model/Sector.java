package com.seachange.healthandsafty.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sector {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("label")
    @Expose
    private String label;

    /**
     * No args constructor for use in serialization
     */
    public Sector() {
    }

    /**
     * @param id
     * @param label
     */
    public Sector(String id, String label) {
        super();
        this.id = id;
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}