package com.example.home.ui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.home.data.Device;
import com.example.home.data.Home;
import com.example.home.data.Room;
import com.example.home.database.AppDatabaseClient;
import com.example.home.database.HomeWithRoom;
import com.example.home.database.RoomWithDevice;
import com.example.home.utils.Communicaton;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";
    private static Context context;
    private static Activity activity;
    Communicaton communicaton;
    private MutableLiveData<List<Home>> listHomeLiveData = new MutableLiveData();
    private List<Home> listHome = new ArrayList<>();
    private MutableLiveData<List<Room>> listRoomLiveData = new MutableLiveData();
    private List<Room> listRoom = new ArrayList<>();
    private MutableLiveData<List<Device>> listDevLiveData = new MutableLiveData();
    private List<Device> listDev = new ArrayList<>();

    private MutableLiveData<HomeWithRoom> RoomCurrentHomeLiveData = new MutableLiveData();
    HomeWithRoom RoomCurrentHome;
    private MutableLiveData<RoomWithDevice> DevCurrentRoomLiveData = new MutableLiveData();
    RoomWithDevice DevCurrentRoom;

    private MutableLiveData<List<RoomWithDevice>> listDevCurrentRoomLiveData = new MutableLiveData();
    List<RoomWithDevice> listDevCurrentRoom = new ArrayList<>();

    private MutableLiveData<Home> currentHomeLiveData = new MutableLiveData();
    private MutableLiveData<Room> currentRoomLiveData = new MutableLiveData();
    private MutableLiveData<Device> currentDeviceLiveData = new MutableLiveData();
    Home currentHome;
    Room currentRoom;
    Device currentDevice;

    public void init(Context context, Activity ac) {
        this.context = context;
        this.activity = ac;
//        communicaton = new Communicaton(activity);
    }

    public void insertHome(Home home) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.d(TAG, "insert home: "+home.homeID+" "+home.addr+" - "+home.homeType+"-"+home.name);
                AppDatabaseClient.getInstance(context).getAppDatabase()
                        .homeDao().insert(home);
                Home h = AppDatabaseClient.getInstance(context).getAppDatabase()
                        .homeDao().getLastHome();
                home.homeID = h.homeID;
//                Log.d(TAG, "insert home: "+h.homeID+" "+h.addr+" - "+h.homeType+"-"+h.name);
                Log.d(TAG, "insert home: " + home.homeID + " " + home.addr + " - " + home.homeType + "-" + home.name);
            }
        }).start();
    }

    public void getAllHomes() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listHome = AppDatabaseClient.getInstance(context).getAppDatabase()
                        .homeDao().getAll();
                listHomeLiveData.postValue(listHome);
                for (Home h : listHome) {
                    Log.d(TAG, "run: " + h.homeID + " " + h.addr + " - " + h.homeType + "-" + h.name);
                }
            }
        }).start();
    }

    public void insertRoom(Room room) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                AppDatabaseClient.getInstance(context).getAppDatabase()
                        .roomDao().insert(room);
                room.roomId = AppDatabaseClient.getInstance(context).getAppDatabase()
                        .roomDao().getLastRoom().roomId;
            }
        }).start();
    }

    public void getAllRooms() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listRoom = AppDatabaseClient.getInstance(context).getAppDatabase()
                        .roomDao().getAll();

                listRoomLiveData.postValue(listRoom);
                for (Room r : listRoom) {
                    Log.d(TAG, "run: " + r.roomType + " - " + r.home.homeID + "-" + r.name + " " + r.floor);
                }
            }
        }).start();
    }

    public void insertDevice(Device device) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabaseClient.getInstance(context).getAppDatabase()
                        .deviceDao().insert(device);
                device.deviceID = AppDatabaseClient.getInstance(context).getAppDatabase()
                        .deviceDao().getLastDevice().deviceID;
            }
        }).start();
    }

    public void getAllDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDev = AppDatabaseClient.getInstance(context).getAppDatabase()
                        .deviceDao().getAll();

                listDevLiveData.postValue(listDev);
                for (Device d : listDev) {
                    Log.d(TAG, "run: " + d.deviceType + " - " + d.home.homeID + "-" + d.room.roomId + " " + d.name);
                }
            }
        }).start();
    }

    public void getRoomInHome(int homeId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RoomCurrentHome = AppDatabaseClient.getInstance(context).getAppDatabase()
                        .homeRoomDAO().getRoomInHome(homeId);

                RoomCurrentHomeLiveData.postValue(RoomCurrentHome);
//                Log.d(TAG, "home: "+RoomCurrentHome.home.homeType+" - "+RoomCurrentHome.home.name+"-"+RoomCurrentHome.home.addr+" "+RoomCurrentHome.home.homeID);
                listDevCurrentRoom = RoomCurrentHome.rooms;
//                for(RoomWithDevice r : listDevCurrentRoom){
//                    Log.d(TAG, "room: "+r.room.roomType+" - "+r.room.home.homeID+"-"+r.room.roomId+" "+r.room.floor);
//                    for (Device d :r.devices){
//                        Log.d(TAG, "run: "+d.deviceType+" - "+d.home.homeID+"-"+d.room.roomId+" "+d.name);
//                    }
//                }
            }
        }).start();
    }

    public void getRoomInHome(Home home) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RoomCurrentHome = AppDatabaseClient.getInstance(context).getAppDatabase()
                        .homeRoomDAO().getRoomInHome(home.homeID);
                RoomCurrentHomeLiveData.postValue(RoomCurrentHome);
                listDevCurrentRoom = RoomCurrentHome.rooms;
            }
        }).start();
    }

}