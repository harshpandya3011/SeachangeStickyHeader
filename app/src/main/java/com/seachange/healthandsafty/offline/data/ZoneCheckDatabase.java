package com.seachange.healthandsafty.offline.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.seachange.healthandsafty.offline.model.ModelConstants;
import com.seachange.healthandsafty.offline.model.ZoneCheck;

@Database(entities = {ZoneCheck.class}, version = 1, exportSchema = false)
public abstract class ZoneCheckDatabase extends RoomDatabase {
    private static ZoneCheckDatabase instance;

    public static synchronized ZoneCheckDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room
                    .databaseBuilder(context.getApplicationContext(), ZoneCheckDatabase.class, ModelConstants.DB_NAME)
                    .build();
        }
        return instance;
    }

    public abstract ZoneCheckDao zoneCheckDao();

}
