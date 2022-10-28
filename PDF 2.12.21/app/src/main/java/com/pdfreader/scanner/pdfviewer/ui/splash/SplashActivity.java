package com.pdfreader.scanner.pdfviewer.ui.splash;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.ads.control.Admod;
import com.ads.control.funtion.AdCallback;
import com.pdfreader.scanner.pdfviewer.BuildConfig;
import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.databinding.ActivitySplashBinding;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseBindingActivity;
import com.pdfreader.scanner.pdfviewer.ui.main.MainActivity;
import com.pdfreader.scanner.pdfviewer.ui.viewpdf.ViewPdfActivity;
import com.pdfreader.scanner.pdfviewer.utils.FirebaseUtils;
import com.pdfreader.scanner.pdfviewer.utils.NetworkUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.RealPathUtil;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseBindingActivity<ActivitySplashBinding, SplashViewModel> implements SplashNavigator {
    private SplashViewModel mSplashViewModel;
    private ActivitySplashBinding mActivitySplashBinding;

    private boolean mIsFromOpenPdf = false;
    private String mFilePdfPath = null;

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public SplashViewModel getViewModel() {
        mSplashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mIsNeedSetTheme = false;
        super.onCreate(savedInstanceState);

        mActivitySplashBinding = getViewDataBinding();
        mSplashViewModel.setNavigator(this);

        precheckIntentFilter();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setClick() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    private void gotoTargetActivity() {
        Intent intent;
        if (!mIsFromOpenPdf || mFilePdfPath == null) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            FirebaseUtils.sendEventFunctionUsed(this, "Open file from other app", "From splash");

            intent = new Intent(SplashActivity.this, ViewPdfActivity.class);
            intent.putExtra(EXTRA_FILE_PATH, mFilePdfPath);
            intent.putExtra(EXTRA_FROM_FIRST_OPEN, true);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void precheckIntentFilter() {
        Intent intent = getIntent();

        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();
            String filepath = null;

            if (Intent.ACTION_VIEW.equals(action) && type != null && type.endsWith("pdf")) {
                Uri fileUri = intent.getData();
                if (fileUri != null) {
                    filepath = RealPathUtil.getInstance().getRealPath(this, fileUri, FileUtils.FileType.type_PDF);
                }

                mIsFromOpenPdf = true;
            } else if (Intent.ACTION_SEND.equals(action) && type != null && type.endsWith("pdf")) {
                Uri fileUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (fileUri != null) {
                    filepath = RealPathUtil.getInstance().getRealPath(this, fileUri, FileUtils.FileType.type_PDF);
                }

                mIsFromOpenPdf = true;
            }

            if (filepath != null) {
                mFilePdfPath = filepath;
            }
        } else {
            mIsFromOpenPdf = false;
        }

        prepareShowAds();
    }

    private void prepareShowAds() {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    gotoTargetActivity();
                }
            }, 1000);
            return;
        }

        Admod.getInstance().loadSplashInterstitalAds(this,
                BuildConfig.full_splash_id,
                30000,
                new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        gotoTargetActivity();
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        gotoTargetActivity();
                    }
                });
    }
}
