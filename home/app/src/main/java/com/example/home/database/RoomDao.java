package com.example.home.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.home.R;
import com.example.home.data.Device;
import com.example.home.data.Home;
import com.example.home.data.Room;

import java.util.List;

@Dao
public interface RoomDao {
    @Query("SELECT * FROM Room")
    List<Room> getAll();

    @Query("SELECT * FROM Room WHERE roomId IN (:roomIds)")
    List<Room> loadAllByIds(int[] roomIds);

    @Query("SELECT * FROM Room WHERE roomId = (:roomId)")
    Room getRoom(int roomId);

    @Query("SELECT * FROM Room ORDER BY roomId DESC LIMIT 1")
    Room getLastRoom();

    @Insert
    void insertAll(Room... rooms);

    @Insert
    void insert(Room rooms);

    @Delete
    void delete(Room room);

    @Update
    void update(Room room);
}