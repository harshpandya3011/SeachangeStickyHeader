package com.seachange.healthandsafty.offline.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.seachange.healthandsafty.offline.model.ZoneCheck;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ZoneCheckDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long add(ZoneCheck zoneCheck);

    @Update
    void update(ZoneCheck zoneCheck);

    @Delete
    void delete(ZoneCheck zoneCheck);

    @Query("SELECT * FROM zoneCheck")
    Flowable<List<ZoneCheck>> getZoneChecks();
}
