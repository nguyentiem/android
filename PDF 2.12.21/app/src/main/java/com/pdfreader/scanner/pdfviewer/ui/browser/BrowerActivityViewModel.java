package com.pdfreader.scanner.pdfviewer.ui.browser;

import android.app.Application;

import androidx.annotation.NonNull;

import com.pdfreader.scanner.pdfviewer.ui.base.BaseViewModel;
import com.pdfreader.scanner.pdfviewer.ui.main.MainNavigator;

public class BrowerActivityViewModel extends BaseViewModel<MainNavigator> {
    public BrowerActivityViewModel(@NonNull Application application) {
        super(application);
    }
}
