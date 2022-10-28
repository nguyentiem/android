package com.example.autolatety.utils;

import android.os.Environment;
import android.util.Log;

import com.example.autolatety.data.FileAudio;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileUtil {
   public static boolean createDir(String dir){
        File rootPath = new File(Environment.getExternalStorageDirectory(), dir);
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        File checkPath =  new File(Environment.getExternalStorageDirectory(), dir);
        return checkPath.exists();
    }

    public static List<FileAudio> getFiles(String dir){
        File rootPath = new File(Environment.getExternalStorageDirectory(), dir);
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
        Log.d("Files", "Path: " + rootPath.getAbsolutePath());
        List<FileAudio> list = new LinkedList<>();
        File[] files = rootPath.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            FileAudio newFile = new FileAudio(files[i].getName(),files[i].getAbsolutePath().toString());
            Log.d("Files", "FileName:" + files[i].getName());
            list.add(newFile);
        }
        return list;
    }

    public static void deleteFile(String path ){
        File fdelete = new File(path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d("File util", "deleteFile: ");
            } else {
                Log.d("can not delete file", "deleteFile: ");
            }
        }
    }

}
