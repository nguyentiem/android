package com.example.in4code.ui.qrgallary.listimage;

import android.annotation.SuppressLint;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.in4code.repos.image.ImageQR;

import java.util.ArrayList;

public class ListImageFragmentViewModel extends AndroidViewModel {

    public ListImageFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    private static final String TAG = "ImageViewModel";
//    private MutableLiveData<ImageQR> mListImage = new MutableLiveData<>();
//    public MutableLiveData<ImageQR> getListImage() {
//        return mListImage;
//    }

    private MutableLiveData<ArrayList<ImageQR>> mListLocalImage = new MutableLiveData<>();
    public MutableLiveData<ArrayList<ImageQR>> getListLocalImage() {
        return mListLocalImage;
    }

    @SuppressLint("Recycle")
    public void startGetLocalImage() {
        AsyncTask.execute(() -> {
            Cursor cursor = null;

            try {
                Uri uri;
                int column_index_data;
                ArrayList<ImageQR> listOfAllImages = new ArrayList<ImageQR>();
                String absolutePathOfImage;
                uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                String[] projection = { MediaStore.MediaColumns.DATA};

                String orderBy = MediaStore.Images.ImageColumns.DATE_ADDED + " DESC";

                cursor = getApplication().getContentResolver().query(uri, projection, null,
                        null, orderBy);

                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                while (cursor.moveToNext()) {
                    absolutePathOfImage = cursor.getString(column_index_data);
                    ImageQR imageData = new ImageQR();
                    imageData.setFilePath(absolutePathOfImage);
                    listOfAllImages.add(imageData);
                }
//                Log.d(TAG, ": "+listOfAllImages.size());
                mListLocalImage.postValue(listOfAllImages);
            } catch (Exception e) {
//                Log.d(TAG, ": "+e.getMessage());
                mListLocalImage.postValue(new ArrayList<>());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        });
    }
}
