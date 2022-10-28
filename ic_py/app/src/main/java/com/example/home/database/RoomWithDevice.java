package com.example.home.database;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.home.data.Device;
import com.example.home.data.Room;

import java.util.List;

public class RoomWithDevice {
    @Embedded
    public Room room;
    @Relation(
            entity = Device.class,
            parentColumn = "roomId",
            entityColumn = "roomroomId") // vi them tien to o truoc nen can them room vao
    public List<Device> devices;
}
