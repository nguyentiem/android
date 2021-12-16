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

    private InputImage inputImage;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private BarcodeScannerOptions options;
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private LayoutCameraScanningBinding binding;
    private Context mContext;
    ImageAnalysis analysisUseCase;


    public InputImage getInputImage() {
        return inputImage;
    }

    public void setInputImage(InputImage inputImage) {
        this.inputImage = inputImage;
    }

    public ListenableFuture<ProcessCameraProvider> getCameraProviderFuture() {
        return cameraProviderFuture;
    }

    public InputImage getImageFromUri(Uri uri) {
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(mContext, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(mContext);
        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindImageAnalisis(cameraExecutor);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(mContext));

    }

    public void bindImageAnalisis(ExecutorService executor){
        Preview preview = new Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).
                setTargetRotation(previewView.getDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
        CameraSelector cameraSelector =
                new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();
                if (mediaImage != null) {

                    InputImage image =
                            InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                    scanBarcodes( image);
                }else{
                    Toast.makeText(mContext.getApplicationContext(),"image null",Toast.LENGTH_SHORT).show();
                }
                imageProxy.close();
            }
        });

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
        } catch (Exception exc) {
            Log.e("TAG", "Use case binding failed", exc);
        }
    }

    private void scanBarcodes(InputImage image) {

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_AZTEC)
                .build();

        BarcodeScanner scanner = BarcodeScanning.getClient();
        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {

                        for (Barcode barcode : barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            switch (valueType) {
                                case Barcode.TYPE_WIFI:
                                    String ssid = barcode.getWifi().getSsid();
                                    String password = barcode.getWifi().getPassword();
                                    int type = barcode.getWifi().getEncryptionType();
                                    break;
                                case Barcode.TYPE_URL:
                                    String title = barcode.getUrl().getTitle();
                                    String url = barcode.getUrl().getUrl();
                                    break;
                                case Barcode.TYPE_TEXT:
//                                    Toast.makeText(mContext.getApplicationContext(),barcode.getDisplayValue(),Toast.LENGTH_SHORT).show();
//                                    listener.finishScan();
                                    break;
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "onFailure: " + e.getMessage());
                    }
                });
    }
    public void takePhoto() {
        File photoFile = FileUtils.getNewFileName();
        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(mContext), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = Uri.fromFile(photoFile);
                Log.d("TAG", "Photo capture succeeded: " + savedUri);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.d("TAG", "cannot take capture: " + exception.getMessage());
            }
        });
    }
    public InputImage getUriImage(Uri uri) {
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(mContext, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }



    public void setCameraProviderFuture(ListenableFuture<ProcessCameraProvider> cameraProviderFuture) {
        this.cameraProviderFuture = cameraProviderFuture;
    }

    public ProcessCameraProvider getCameraProvider() {
        return cameraProvider;
    }

    public void setCameraProvider(ProcessCameraProvider cameraProvider) {
        this.cameraProvider = cameraProvider;
    }

    public PreviewView getPreviewView() {
        return previewView;
    }

    public void setPreviewView(PreviewView previewView) {
        this.previewView = previewView;
    }

    public ImageCapture getImageCapture() {
        return imageCapture;
    }

    public void setImageCapture(ImageCapture imageCapture) {
        this.imageCapture = imageCapture;
    }

    public BarcodeScannerOptions getOptions() {
        return options;
    }

    public void setOptions(BarcodeScannerOptions options) {
        this.options = options;
    }

    public ExecutorService getCameraExecutor() {
        return cameraExecutor;
    }

    public void setCameraExecutor(ExecutorService cameraExecutor) {
        this.cameraExecutor = cameraExecutor;
    }

    public BarcodeScanner getScanner() {
        return scanner;
    }

    public void setScanner(BarcodeScanner scanner) {
        this.scanner = scanner;
    }

    public LayoutCameraScanningBinding getBinding() {
        return binding;
    }

    public void setBinding(LayoutCameraScanningBinding binding) {
        this.binding = binding;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public ImageAnalysis getAnalysisUseCase() {
        return analysisUseCase;
    }

    public void setAnalysisUseCase(ImageAnalysis analysisUseCase) {
        this.analysisUseCase = analysisUseCase;
    }

    public ScanCameraFragmentViewModel(@NonNull Application application) {
        super(application);
    }

}
