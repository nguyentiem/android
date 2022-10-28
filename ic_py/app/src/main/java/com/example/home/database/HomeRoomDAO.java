package com.example.home.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.home.data.Device;
import com.example.home.data.Home;
import com.example.home.data.Room;

import java.util.List;

@Dao
public interface HomeRoomDAO {
    @Transaction
    @Query("SELECT * FROM Home WHERE homeID = :idHome")
    HomeWithRoom getRoomInHome(int idHome);

}
