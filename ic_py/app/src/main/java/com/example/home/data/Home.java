package com.example.home.data;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;

@Entity(tableName = "Home")
public class Home {
    @PrimaryKey(autoGenerate = true)
    public int homeID=0;
    public String addr;
    public String homeType;
    public String name;


    public Home( String addr, String homeType, String name) {
        this.addr = addr;
        this.homeType = homeType;
        this.name = name;

    }

    @NotNull
    public String getAddr() {
        return addr;
    }

    public void setAddr(@NotNull String addr) {
        this.addr = addr;
    }

    public String getType() {
        return homeType;
    }

    public void setType(String homeType) {
        this.homeType = homeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
