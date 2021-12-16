package com.pdfreader.scanner.pdfviewer.data.local.preferences;

import com.pdfreader.scanner.pdfviewer.data.model.ImageToPDFOptions;
import com.pdfreader.scanner.pdfviewer.data.model.ViewPdfOption;

public interface PreferencesHelperInterface {
    int getOpenBefore();
    void setOpenBefore(int opened);

    int getRatingUs();
    void setRatingUs(int rated);

    void setShowGuideConvert(int shown);
    int getShowGuideConvert();

    void setShowGuideSelectMulti(int shown);
    int getShowGuideSelectMulti();

    int getBackTime();
    void setBackTime(int time);

    String getAppLocale();
    void setAppLocale(String locale);

    ImageToPDFOptions getImageToPDFOptions();
    void saveImageToPDFOptions(ImageToPDFOptions imageToPDFOptions);

    ViewPdfOption getViewPDFOptions();
    void saveViewPDFOptions(ViewPdfOption viewPdfOption);

    void setTheme(int theme);
    int getTheme();
}
