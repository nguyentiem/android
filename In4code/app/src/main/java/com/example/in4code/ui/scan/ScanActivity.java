package com.example.in4code.ui.scan;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.in4code.R;
import com.example.in4code.databinding.ActivityScanBinding;
import com.example.in4code.ui.scan.camera.ScanCameraFragment;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ScanActivity extends AppCompatActivity implements ScanActivityNavigation {

    private FragmentManager fragmentManager;
    private List<Fragment> listFrag = new ArrayList<>();
    private ScanActivityViewModel scanActivityViewModel;
    private ActivityScanBinding binding;
    private int layoutId;
    private static final int MY_CAMERA_REQUEST_CODE = 100;


    public ScanActivityViewModel getViewModel() {
        scanActivityViewModel = new ViewModelProvider(this).get(ScanActivityViewModel.class);
        return scanActivityViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        requestCameraPermission(this);
    }

    public void initView() {
        binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        layoutId= binding.scanContent.getId();
        listFrag.add(new ScanCameraFragment(this,this));

        getViewModel();
    fragmentManager= getSupportFragmentManager();
        binding.toolbarBackScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.toolbarImageScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        goToScreen(0);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void requestCameraPermission(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }




    public void goToScreen (int i){
        if(i>=0&&i<listFrag.size()) {
            fragmentManager.beginTransaction().replace(layoutId,listFrag.get(i),null ).commit();
        }
    }

    @Override
    public void finishScan() {
//        onBackPressed();
        finish();
    }
}