package com.example.in4code.utils.file;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.example.in4code.constances.Constance;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DirectoryUtils {
    private Context mContext;
    private ArrayList<String> mFilePaths;

    public DirectoryUtils(Context context) {
        mContext = context;
        mFilePaths = new ArrayList<>();
    }









    private void walkDir(File dir, List<String> extensions) {
        File[] listFile = dir.listFiles();
        if (listFile != null) {
            for (File aListFile : listFile) {

                if (aListFile.isDirectory()) {
                    walkDir(aListFile, extensions);
                } else {
                    for (String extension: extensions) {
                        if (aListFile.getName().toLowerCase().endsWith(extension)) {
                            //Do what ever u want
                            mFilePaths.add(aListFile.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }


    public static String getImageStorageLocation(Context context, String folderName) {
        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), Constance.IMAGE_DIRECTORY);
        if (!dir.exists()) {
            boolean isDirectoryCreated = dir.mkdir();
            if (!isDirectoryCreated) {
                Log.e("Error", "Directory could not be created");
            }
        }
        return dir.getAbsolutePath() + "/";
    }

    public static void cleanImageStorage(Context context) {
        try {
            File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), Constance.IMAGE_DIRECTORY);
            if (folder.isDirectory())
            {
                String[] children = folder.list();
                for (String child : children) {
                    new File(folder, child).delete();
                }
            }
        } catch (Exception ignored) {

        }
    }
}
