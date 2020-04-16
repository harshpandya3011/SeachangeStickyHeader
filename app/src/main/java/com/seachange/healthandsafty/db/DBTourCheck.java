package com.seachange.healthandsafty.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//
//status value, default to zone...
// 0 is not synced...
// 1 is synced...

@Entity
public class DBTourCheck {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "tourCheck")
    private String tourZoneCheck;

    @ColumnInfo(name = "synced")
    private Integer status = 1;


    public DBTourCheck(String check) {
        this.tourZoneCheck = check;
    }

    public DBTourCheck() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTourZoneCheck() {
        return tourZoneCheck;
    }

    public void setTourZoneCheck(String tourZoneCheck) {
        this.tourZoneCheck = tourZoneCheck;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
