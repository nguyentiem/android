package com.example.home.database;

import android.content.Context;

import androidx.room.Room;

public class AppDatabaseClient {
    private Context mCtx;
    private static AppDatabaseClient mInstance;

    //our app database object
    private AppDatabase appDatabase;

    private AppDatabaseClient(Context mCtx) {
        this.mCtx = mCtx;
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "homeDb").build();

    }

    public static synchronized AppDatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new AppDatabaseClient(mCtx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
