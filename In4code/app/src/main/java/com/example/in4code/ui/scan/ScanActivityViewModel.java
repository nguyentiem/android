package com.example.in4code.ui.scan;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.in4code.repos.ImageQR;

import java.util.ArrayList;
import java.util.List;

public class ScanActivityViewModel extends AndroidViewModel {
    MutableLiveData<List<ImageQR>> obList =new MutableLiveData<>();

    public ScanActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public List<ImageQR> getListImage() {
        return listImage;
    }

    public void setListImage(List<ImageQR> listImage) {
        this.listImage = listImage;
        obList.postValue(listImage);
    }

    List<ImageQR> listImage = new ArrayList<>();

    public MutableLiveData<List<ImageQR>> getObList() {
        return obList;
    }

    public boolean getAllImage(){
        // doc tat ca cac image lay thong tin tra ve cho list
    return false;
    }
    public void startGetLocalImage() {
        AsyncTask.execute(() -> {
            Cursor cursor = null;

            try {
                Uri uri;
                int column_index_data;
                ArrayList<ImageQR> listOfAllImages = new ArrayList<ImageQR>();
                String absolutePathOfImage;
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                String[] projection = { MediaStore.MediaColumns.DATA};

                String orderBy = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC";

                cursor = getApplication().getContentResolver().query(uri, projection, null,
                        null, orderBy);

                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                while (cursor.moveToNext()) {
                    absolutePathOfImage = cursor.getString(column_index_data);
                    ImageQR ImageQR = new ImageQR();
                    ImageQR.setFilePath(absolutePathOfImage);
                    listOfAllImages.add(ImageQR);
                }

                obList.postValue(listOfAllImages);
            } catch (Exception e) {
                obList.postValue(new ArrayList<>());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        });
    }
}
