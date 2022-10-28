package com.example.home.database;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
@Dao
public interface RoomDeviceDao {
    @Transaction
    @Query("SELECT * FROM Room WHERE roomId LIKE :id")
    RoomWithDevice getDeviceInRoom(int id);
}
