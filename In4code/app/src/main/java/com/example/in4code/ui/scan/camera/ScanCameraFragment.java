package com.example.in4code.ui.scan.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.in4code.R;
import com.example.in4code.databinding.LayoutCameraScanningBinding;
import com.example.in4code.ui.component.ResultScanQRDialog;
import com.example.in4code.ui.scan.ScanActivity;
import com.example.in4code.ui.scan.ScanActivityNavigation;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.disposables.Disposable;


public class ScanCameraFragment extends Fragment implements CameraFragmentNavigation {
    //// resize anh
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    private InputImage inputImage;
    private PreviewView previewView;
    private Preview preview;
    private ImageCapture imageCapture;
    private BarcodeScannerOptions options;
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private Context mContext;
    private ImageAnalysis analysisUseCase;
    private CameraSelector cameraSelector;
    private LayoutCameraScanningBinding binding;
    private ScanCameraFragmentViewModel viewModel;
    private ScanActivityNavigation listener;
    private View root;
    private CameraFragmentNavigation listen2;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    public ScanCameraFragment(Context context, ScanActivityNavigation mlisten) {
        this.mContext = context;
        this.listener = mlisten;
    }

    public ScanCameraFragmentViewModel getViewModel() {
        viewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(ScanCameraFragmentViewModel.class);
        return viewModel;
    }

    public void initView() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = LayoutCameraScanningBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        listen2 =this;
        setupCamera();

        return root;
    }



    private void setupCamera() {
        getViewModel();
        previewView = root.findViewById(R.id.preview);
        cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        viewModel.getLiveDataCamera().observe(getViewLifecycleOwner(), new Observer<ProcessCameraProvider>() {
            @Override
            public void onChanged(ProcessCameraProvider processCameraProvider) {
                cameraProvider = processCameraProvider;
                if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    listener.finishScan();
                }
                bindCameraUseCases();

            }
        });

    }

    private void bindCameraUseCases() {
        bindPreviewUseCase();
        bindAnalyseUseCase();
    }

    private void bindPreviewUseCase() {
        if (cameraProvider == null) {
            return;
        }
        if (preview != null) {
            cameraProvider.unbind(preview);
        }

        preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(previewView.getDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
        } catch (IllegalStateException illegalStateException) {
            Log.e("TAG", illegalStateException.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e("TAG", illegalArgumentException.getMessage());
        }
    }

    Disposable disposable;

    private void bindAnalyseUseCase() {

        scanner = BarcodeScanning.getClient();

        if (cameraProvider == null) {
            return;
        }
        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }

        analysisUseCase = new ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(previewView.getDisplay().getRotation())
                .build();
        cameraExecutor = Executors.newSingleThreadExecutor();
        analysisUseCase.setAnalyzer(
                cameraExecutor, new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {

                        processImageProxy(imageProxy);

                    }
                }
        );

        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase);
        } catch (IllegalStateException illegalStateException) {
            Log.e("TAG", illegalStateException.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e("TAG", illegalArgumentException.getMessage());
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private void processImageProxy(ImageProxy imageProxy) {

        @SuppressLint("UnsafeOptInUsageError") InputImage inputImage =
                InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
         scanner.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(@NonNull List<Barcode> barcodes) {

                        if(barcodes!=null&&barcodes.size()>0){

                            new ResultScanQRDialog(mContext,barcodes.get(0),listener,listen2).show();
//                            scanner.close();

                             analysisUseCase.clearAnalyzer();

                        }
                        }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Log.d("TAG", "onSuccess: " + exception.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
             @Override
             public void onComplete(@NonNull Task<List<Barcode>> task) {

                 imageProxy.close();
             }
         });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void continueProcess() {
        if(scanner==null)
        scanner = BarcodeScanning.getClient();
if(cameraExecutor==null){
    cameraExecutor = Executors.newSingleThreadExecutor();
}
        if (cameraProvider == null) {
            return;
        }
        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);


        analysisUseCase = new ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(previewView.getDisplay().getRotation())
                .build();
            try {
                cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase);
            } catch (IllegalStateException illegalStateException) {
                Log.e("TAG", illegalStateException.getMessage());
            } catch (IllegalArgumentException illegalArgumentException) {
                Log.e("TAG", illegalArgumentException.getMessage());
            }
        }

        analysisUseCase.setAnalyzer(
                cameraExecutor, new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {

                        processImageProxy(imageProxy);

                    }
                }
        );
    }
}
