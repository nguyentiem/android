package com.example.home.data;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/*
,
foreignKeys = {@ForeignKey(entity = Room.class,
                parentColumns = "roomId",
                childColumns = "roomId",
                onDelete = ForeignKey.CASCADE)
        }
* */
@Entity(tableName = "Device")
public class Device {
    @PrimaryKey(autoGenerate = true)
    public int deviceID;
    @Embedded(prefix = "home")
    @NotNull
    public Home home;
    @Embedded(prefix = "room")
    @NotNull
    public Room room;
    public String deviceType;
    public String name;

    public long timeStart;
    public long timeEnd;
    public int status; // 0-255
    public int parameter;

    public Device( String deviceType, @NotNull Room room, @NotNull Home home,String name) {
        this.deviceType = deviceType;
        this.room = room;
        this.home = home;
        this.name = name;
        status =0;
        timeEnd =0;
        timeStart =0;
        parameter =0;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    public int getDeviceID() {
        return deviceID;
    }

    public String getType() {
        return deviceType;
    }

    public void setType(String deviceType) {
        this.deviceType = deviceType;
    }

    @NotNull
    public Home getHome() {
        return home;
    }

    public void setHome(@NotNull Home home) {
        this.home = home;
    }

    @NotNull
    public Room getRoom() {
        return room;
    }

    public void setRoom(@NotNull Room room) {
        this.room = room;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getParameter() {
        return parameter;
    }

    public void setParameter(int parameter) {
        this.parameter = parameter;
    }
}
