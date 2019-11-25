package com.example.checkbud.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import timber.log.Timber;

@Database(entities = {CheckEntry.class}, version = 1, exportSchema = false)
public abstract class CheckDb extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DB_NAME = "journal";
    private static CheckDb instance;

    public static CheckDb getInstance(Context ctxt) {
        if (instance == null) {
            synchronized (LOCK) {
                Timber.d("creating new DB instance");
                instance = Room.databaseBuilder(ctxt.getApplicationContext(), CheckDb.class,
                        CheckDb.DB_NAME).build();
            }
        }
        Timber.d("getting DB instance");
        return instance;
    }

    public abstract CheckDao checkDao();
}
