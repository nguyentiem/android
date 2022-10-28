package com.example.autolatety.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.autolatety.data.Result;


@Database(entities = {Result.class}, version = 1)
public abstract class ResultDatabase extends RoomDatabase {
        public abstract ResultDao resultDao();
}
