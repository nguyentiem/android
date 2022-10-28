package com.example.autolatety.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.autolatety.data.Result;

import java.util.List;

@Dao
public interface ResultDao {
    @Query("SELECT * FROM result")
    List<Result> getAll();

//    @Query("SELECT * FROM result ")
//    List<Result> loadAllByIds();

//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    Result findByName(String first, String last);

    @Insert
    void insertAll(Result... users);

    @Insert
    void insert(Result users);

    @Delete
    void delete(Result user);

    @Query("DELETE FROM result")
    public void deleteAll();
}