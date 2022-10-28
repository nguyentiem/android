package com.pdfreader.scanner.pdfviewer.ui.merge;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.ads.control.Admod;
import com.pdfreader.scanner.pdfviewer.BuildConfig;
import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.constants.AppConstants;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.databinding.ActivityMergerBinding;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseBindingActivity;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseFragment;
import com.pdfreader.scanner.pdfviewer.ui.component.ToolTipDialog;
import com.pdfreader.scanner.pdfviewer.ui.imagetopdf.ImageToPdfActivity;
import com.pdfreader.scanner.pdfviewer.utils.DialogFactory;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MergerActivity extends BaseBindingActivity<ActivityMergerBinding, MergeViewModel> implements MergeNavigator {
    ViewPager viewPager;
    ViewPageMergeAdapter viewPagerAdapter;
    int funtion =0;
    MergeViewModel mergeViewModel;
    ActivityMergerBinding mActivityMainBinding;
    private Boolean mIsFabOpen = false;
    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_merger;
    }

    @Override
    public MergeViewModel getViewModel() {
        mergeViewModel = ViewModelProviders.of(this).get(MergeViewModel.class);
        return mergeViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        funtion = getIntent().getExtras().getInt("funtion");
        mActivityMainBinding = getViewDataBinding();
        viewPager = findViewById(R.id.viewpager_merge);
        viewPagerAdapter = new ViewPageMergeAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(MergeFragment.newInstance());
        viewPager.setAdapter(viewPagerAdapter);
       // mergeViewModel.setNavigator(this);
        Log.d("TAG", "onCreate: "+mergeViewModel.toString());
        initView();
    }


    @Override
    protected void setClick() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }
    @Override
    protected void initView() {
        preloadMyPdfAdsIfInit();
        preloadDoneAdsIfInit();
        switch (funtion){
            case 1:
                mActivityMainBinding.titleToolbarMerge.setText("Merge PDFs");
                mActivityMainBinding.merge.setText("Merge");
                mActivityMainBinding.merge.setOnClickListener(view -> {

                });
                break;
            case 2:
                mActivityMainBinding.titleToolbarMerge.setText("Upload File");
                mActivityMainBinding.merge.setText("Upload");
                mActivityMainBinding.merge.setOnClickListener(view -> {
                    MergeFragment fragment = viewPagerAdapter.getMerge();
                    FileUtils.uploadFiles(this,fragment.getmFileListAdapter().getSelectedList());
                });
                break;
        }
        mActivityMainBinding.selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call to fragmetn
                MergeFragment fragment = viewPagerAdapter.getMerge();
                fragment.getmFileListAdapter().addAll();
                fragment.LogFile();
            }
        });



        mActivityMainBinding.backButtonMerge.setOnClickListener(view ->{
            onBackPressed();
        });

        setForFabOpenPdf();
    }


    private void setForFabOpenPdf() {
        Animation mFabClockAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_lock);
        Animation mFabAnticlockAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_antilock);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}