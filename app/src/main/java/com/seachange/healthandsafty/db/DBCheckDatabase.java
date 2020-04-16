package com.seachange.healthandsafty.db;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import android.content.Context;
import androidx.annotation.NonNull;

import com.seachange.healthandsafty.BuildConfig;

import org.jetbrains.annotations.Contract;


//need to take care of migration
//version 3 added column sync...

@Database(entities = {DBCheck.class, DBTourCheck.class}, version = BuildConfig.DATABASE_VERSION)
public abstract class DBCheckDatabase extends RoomDatabase {

    public abstract DBCheckDao checkDao();

    public abstract DBTourCheckDao tourCheckDao();

    // marking the instance as volatile to ensure atomic access to the variable
    private static volatile DBCheckDatabase INSTANCE;

    public static DBCheckDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DBCheckDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        DBCheckDatabase.class, "caygo_zone_check_database")
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        // Migration is not part of this codelab.
                        .fallbackToDestructiveMigration()
//                        .addMigrations(getMigrations())
                        .addMigrations(MIGRATION_2_3)
                         .build();

                }
            }
        }
        return INSTANCE;
    }

    @Contract(pure = true)
    private static Migration[] getMigrations() {
        Migration[] migrations = new Migration[BuildConfig.DATABASE_VERSION];

        for (int i = 0; i < BuildConfig.DATABASE_VERSION; i++) {
            Migration migration = new Migration(i, BuildConfig.DATABASE_VERSION) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {

                    //add sync column on database version 3...
//                    if (BuildConfig.DATABASE_VERSION == 4) {
                        database.execSQL("ALTER TABLE DBCheck ADD COLUMN sync INTEGER NOT NULL default 0");
//                    }
                }
            };

            migrations[i] = migration;
        }

        return migrations;
    }


    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE DBCheck ADD COLUMN sync INTEGER NOT NULL default 0");
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS DBTourCheck (id INTEGER, tourCheck TEXT, synced INTEGER NOT NULL default 0, PRIMARY KEY(id))");

        }
    };
}
