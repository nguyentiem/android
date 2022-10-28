package com.example.home.data;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;

/*
* @Entity(tableName = "Room",
        foreignKeys = {@ForeignKey(entity = Home.class,
                parentColumns = "addr",
                childColumns = "addrHome",
                onDelete = ForeignKey.CASCADE)
        })
* */
@Entity(tableName = "Room")
public class Room {
    @PrimaryKey(autoGenerate = true)
    public int roomId;
    //     @Embedded//(prefix = "String")
    public String roomType;
    @Embedded(prefix = "home")
    @NotNull
    public Home home;
    public String name;
    public int floor;

    public Room( String roomType, @NotNull Home home, String name, int floor) {

        this.roomType = roomType;
        this.home = home;
        this.name = name;
        this.floor = floor;
    }

    @NotNull
    public int getRoomId() {
        return roomId;
    }

    public String getType() {
        return roomType;
    }

    public void setType(String roomType) {
        this.roomType = roomType;
    }

    @NotNull
    public Home getAddrHome() {
        return home;
    }

    public void setAddrHome(@NotNull Home home) {
        this.home = home;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
