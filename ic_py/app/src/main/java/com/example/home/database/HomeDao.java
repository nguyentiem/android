package com.example.home.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.home.data.Home;

import java.util.List;

@Dao
public interface HomeDao {
    @Query("SELECT * FROM Home")
    List<Home> getAll();

    @Query("SELECT * FROM Home WHERE homeID IN (:homeIDs)")
    List<Home> loadAllByIds(int[] homeIDs);

    @Query("SELECT * FROM Home WHERE homeID = (:homeID)")
    Home getHome(int homeID);

    @Query("SELECT * FROM Home ORDER BY homeID DESC LIMIT 1")
    Home getLastHome();

    @Insert
    void insertAll(Home... Homes);

    @Insert
    void insert(Home home);

    @Delete
    void delete(Home home);

    @Update
    void update(Home home);
}