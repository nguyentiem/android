package com.example.autolatety.utils;

import android.os.SystemClock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
//   private Date currentTime ;

  public static String getStringTime(){
       Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd hh:mm:ss.SSS");
        String strDate = dateFormat.format(currentTime);
        return strDate;
    }

    public static String getMillisString(){
        return String.valueOf(SystemClock.uptimeMillis());
    }

    public static long getMillis(){
        return SystemClock.uptimeMillis();
    }

}
