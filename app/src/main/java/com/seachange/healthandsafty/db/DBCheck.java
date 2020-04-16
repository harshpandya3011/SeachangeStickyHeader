package com.seachange.healthandsafty.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DBCheck {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "check")
    private String zoneCheck;

    private boolean sync;

    public DBCheck(String check, boolean sync) {
        this.zoneCheck = check;
        this.sync = sync;
    }

    public DBCheck() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getZoneCheck() {
        return zoneCheck;
    }

    public void setZoneCheck(String zoneCheck) {
        this.zoneCheck = zoneCheck;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }
}
