package com.example.in4code.utils.file;

import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;

import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.example.in4code.constances.Constance;
import com.example.in4code.repos.ImageQR;
import com.example.in4code.utils.date.DateTimeUtlis;

import java.io.File;
import java.util.ArrayList;

public class FileUtils {
    public enum FileType {
        type_JPG,
        type_PNG
    }

    public static File getNewFileName() {
        String nameFile = getDefaultOutputName(Constance.IMG_PREFIX_NAME) + Constance.IMG_EXPAND_NAME;
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + Constance.Dir);
        if (!dir.exists()) {
            Log.d("TAG", "getNewFileName:");
            boolean isDirectoryCreated = dir.mkdir();
            if (!isDirectoryCreated) {
                Log.e("Error", "Directory could not be created");
            }
        }
        File file = new File(dir, nameFile);
        Log.d("TAG", "file image name: " + nameFile);
        Log.d("TAG", "file image name: " + Uri.fromFile(file));
        return file;
    }

    public static String getDefaultOutputName(String fileType) {
        return fileType + "_" + DateTimeUtlis.currentTimeToNaming();
    }

    public static String getFileName(String path) {
        if (path == null)
            return "File name";

        int index = path.lastIndexOf("/");
        return index < path.length() ? path.substring(index + 1) : "File name";
    }



    public static ImageQR getImageQRFromFilePath(String filePath,String fileType){
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        int size = Integer.parseInt(String.valueOf(file.length()/1024));
        ImageQR fileData = new ImageQR(getFileName(filePath),  uri,filePath,  fileType);

        return fileData;
    }
}
