package com.example.home.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.home.data.Device;
import com.example.home.data.Home;
import com.example.home.data.Room;

import java.util.List;

public class HomeWithRoom {
    @Embedded
    public Home home;
    @Relation(
            entity = Room.class,
            parentColumn = "homeID",
            entityColumn = "homehomeID")
    public List<RoomWithDevice> rooms;

}
