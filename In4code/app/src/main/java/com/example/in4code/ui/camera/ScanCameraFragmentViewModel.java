package com.example.in4code.ui.camera;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.example.in4code.databinding.LayoutCameraScanningBinding;
import com.example.in4code.utils.file.FileUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
