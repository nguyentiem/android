package com.pdfreader.scanner.pdfviewer;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

public class PdfApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(
                this,
                initializationStatus -> {});
    }
}
