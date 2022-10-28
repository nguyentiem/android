package com.example.home.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.home.data.Device;
import com.example.home.data.Home;
import com.example.home.data.Room;

@Database(entities = {Home.class, Room.class, Device.class,}, version = 1)
public abstract class AppDatabase  extends RoomDatabase {
    public abstract HomeRoomDAO homeRoomDAO();
    public abstract HomeDao homeDao();
    public abstract RoomDao roomDao();
    public abstract DeviceDao deviceDao();
}
