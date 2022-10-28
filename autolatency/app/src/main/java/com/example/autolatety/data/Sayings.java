package com.example.autolatety.data;

import com.example.autolatety.utils.TimeUtil;

public class Sayings {
    public Long begin= Long.valueOf(0);
    public String beginStr;
    public String endStr;
    public String command;
    public long finish=0;
    public String getBeginStr() {
        return beginStr;
    }
    public String getEndStr() {
        return endStr;
    }

    public void setBegin(Long begin) {
        this.begin = begin;
    }

    public void setBeginStr(String beginStr) {
        this.beginStr = beginStr;
    }

    public Long getBegin() {
        return begin;
    }

    public String getCommand() {
        return command;
    }

    public long getFinish() {
        return finish;
    }

    public void setEndStr(String endStr) {
        this.endStr = endStr;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setFinish(long finish) {
        this.finish = finish;
    }

    public Sayings(String command) {
        this.command = command;
    }

    public Sayings() {
    }

    public void callStart(){
        begin = TimeUtil.getMillis();
        beginStr = TimeUtil.getStringTime();
    }
    public void callEnd(){
        finish = TimeUtil.getMillis();
        endStr = TimeUtil.getStringTime();
    }

}
