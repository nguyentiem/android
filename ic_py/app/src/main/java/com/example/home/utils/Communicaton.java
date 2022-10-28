package com.example.home.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Communicaton {
    private static Communicaton instance;
    private  final String TAG ="Communication";
    private  final String SERVER ="http://chat.socket.io";

    private  Socket mSocket;
    {
        try {
            mSocket = IO.socket(SERVER);
        } catch (URISyntaxException e) {}
    }

    private Communicaton( ){

    }

    public static synchronized Communicaton getInstance(){
        if(instance==null){
            instance =new Communicaton();
        }
        return instance;
    }

    private  void connect() {
        mSocket.connect();
    }

    public  void sendMess(String subject, String message){
        if(!mSocket.connected())
            connect();
        mSocket.emit(subject, message);
    }

    public  void  listenEvent(String event){
        mSocket.on(event, onNewMessage);
        mSocket.connect();
    }

    public void disconnect(String event){
        mSocket.disconnect();
        mSocket.off(event, onNewMessage);
    }

    public boolean login(){

        return true;
    }

    public boolean logout(){

        return true;
    }

    private   Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }
                    Log.d(TAG, username+" "+message);
                }
            });
        }
    };
}
