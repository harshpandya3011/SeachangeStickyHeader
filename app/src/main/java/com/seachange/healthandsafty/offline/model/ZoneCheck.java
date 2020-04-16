package com.seachange.healthandsafty.offline.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ZoneCheck implements Serializable {

    private static int TAG_START_CHECK = 1;
    private static int TAG_END_CHECK = 2;
    private static int TAG_START_TOUR = 3;
    private static int TAG_END_TOUR = 4;
    private static int TAG_ADD_HAZARDS = 5;

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "check_id")
    private long checkId;

    @ColumnInfo(name = "sync_pending")
    private boolean syncPending;

    public long getId() {
        return id;
    }

    public long getCheckId() {
        return checkId;
    }

    public void setCheckId(long checkId) {
        this.checkId = checkId;
    }

    public boolean isSyncPending() {
        return syncPending;
    }

    public void setSyncPending(boolean syncPending) {
        this.syncPending = syncPending;
    }

    @Ignore
    public ZoneCheck() {

    }

    public ZoneCheck(long id) {
        this.id = id;
    }
}
