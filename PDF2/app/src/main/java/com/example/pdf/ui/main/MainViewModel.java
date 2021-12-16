package com.pdfreader.scanner.pdfviewer.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;

import com.pdfreader.scanner.pdfviewer.ui.base.BaseViewModel;

public class MainViewModel extends BaseViewModel<MainNavigator> {
    public MainViewModel(@NonNull Application application) {
        super(application);
    }
}
