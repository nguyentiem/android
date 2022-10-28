package com.example.autolatety.data;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.autolatety.utils.TimeUtil;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "result")
public class Result {
    @PrimaryKey
    @NotNull
    String time;
    @Embedded(prefix = "hi")
    Sayings hi;
    @Embedded(prefix = "tingtong")
    Sayings ting;

    @Embedded(prefix = "command")
    Sayings command;
    @Embedded(prefix = "respone")
    Sayings bixbyRespone;
    float latety;
   public   boolean acc;
    public boolean isDone;
    public Sayings getTing() {
        return ting;
    }

    public void setTing(Sayings ting) {
        this.ting = ting;
    }

    public void setAcc(boolean acc) {
        this.acc = acc;
    }


    public boolean isAcc() {
        return acc;
    }

    public Result() {
        this.time = TimeUtil.getMillisString();
        hi = new Sayings("Hi Bixby!");
        ting =new Sayings("Wakeup sound");
        command =new CommandSaying();
        bixbyRespone =new BixbyResponeSaying();
        acc = false;
        isDone =false;
    }

    public Sayings getHi() {
        return hi;
    }

    public void setHi(Sayings hi) {
        this.hi = hi;
    }
    private void caculateLatety() {
        latety = bixbyRespone.finish - command.finish;
        latety/=1000.0;

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Sayings getCommand() {
        return command;
    }

    public void setCommand(Sayings command) {
        this.command = command;
    }

    public Sayings getBixbyRespone() {
        return bixbyRespone;
    }

    public void setBixbyRespone(Sayings bixbyRespone) {
        this.bixbyRespone = bixbyRespone;
    }

    public float getLatety() {
        caculateLatety();
        return latety;
    }

    public void setLatety(float latety) {
        this.latety = latety;
    }
}
