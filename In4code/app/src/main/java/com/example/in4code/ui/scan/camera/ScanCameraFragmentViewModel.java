package com.example.in4code.ui.scan.camera;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class ScanCameraFragmentViewModel extends AndroidViewModel {

    private MutableLiveData<ProcessCameraProvider>  liveDataCamera =new MutableLiveData<>();



    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    private Context mContext;




    public MutableLiveData<ProcessCameraProvider> getLiveDataCamera() {

             cameraProviderFuture = ProcessCameraProvider.getInstance(getApplication());
             cameraProviderFuture.addListener(
                   ()->{
            try {
                cameraProvider=   cameraProviderFuture.get();
                liveDataCamera.setValue(cameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            },
            ContextCompat.getMainExecutor(getApplication()));


        return liveDataCamera;
    }

    public ListenableFuture<ProcessCameraProvider> getCameraProviderFuture() {
        if(cameraProviderFuture==null){
            cameraProviderFuture = ProcessCameraProvider.getInstance(getApplication());
        }
        return cameraProviderFuture;
    }

    public void setCameraProviderFuture(ListenableFuture<ProcessCameraProvider> cameraProviderFuture) {
        this.cameraProviderFuture = cameraProviderFuture;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }



    public ScanCameraFragmentViewModel(@NonNull Application application) {
        super(application);
    }

}
