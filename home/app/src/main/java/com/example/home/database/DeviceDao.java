package com.example.home.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.home.data.Device;
import com.example.home.data.Home;
import com.example.home.data.Room;

import java.util.List;

@Dao
public interface DeviceDao {
    @Query("SELECT * FROM Device")
    List<Device> getAll();

    @Query("SELECT * FROM Device WHERE deviceID IN (:deviceIds)")
    List<Device> loadAllByIds(int[] deviceIds);

    @Query("SELECT * FROM Device WHERE deviceID = (:deviceId)")
    Device getDevice(int deviceId);

    @Query("SELECT * FROM Device ORDER BY deviceID DESC LIMIT 1")
    Device getLastDevice();

    @Insert
    void insertAll(Device... devices);

    @Insert
    void insert(Device device);

    @Delete
    void delete(Device device);

    @Update
    void update(Device device);
}
