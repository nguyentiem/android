package com.pdfreader.scanner.pdfviewer.ui.splash;

import android.app.Application;

import androidx.annotation.NonNull;

import com.pdfreader.scanner.pdfviewer.ui.base.BaseViewModel;

public class SplashViewModel extends BaseViewModel<SplashNavigator> {
    private static final String TAG = "SplashViewModel";

    public SplashViewModel(@NonNull Application application) {
        super(application);
    }
}
