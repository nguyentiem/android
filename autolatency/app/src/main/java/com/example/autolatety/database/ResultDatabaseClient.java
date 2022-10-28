package com.example.autolatety.database;

import android.content.Context;

import androidx.room.Room;

public class ResultDatabaseClient {
    private Context mCtx;
    private static ResultDatabaseClient mInstance;

    //our app database object
    private ResultDatabase appDatabase;

    private ResultDatabaseClient(Context mCtx) {
        this.mCtx = mCtx;
        appDatabase = Room.databaseBuilder(mCtx, ResultDatabase.class, "myDb").build();

    }

    public static synchronized ResultDatabaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new ResultDatabaseClient(mCtx);
        }
        return mInstance;
    }

    public ResultDatabase getAppDatabase() {
        return appDatabase;
    }
}
