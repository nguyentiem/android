package com.pdfreader.scanner.pdfviewer.data;

import com.pdfreader.scanner.pdfviewer.data.model.BookmarkData;
import com.pdfreader.scanner.pdfviewer.data.model.ImageToPDFOptions;
import com.pdfreader.scanner.pdfviewer.data.model.RecentData;
import com.pdfreader.scanner.pdfviewer.data.model.ViewPdfOption;

import java.util.List;

import io.reactivex.Observable;

public interface DataManagerInterface {
    boolean isOpenBefore();
    void setOpenBefore();

    // rate :
    void setRatingUsDone();
    boolean checkRatingUsDone();

    void setShowGuideConvertDone();
    boolean getShowGuideConvert();

    void setShowGuideSelectMultiDone();
    boolean getShowGuideSelectMulti();

    int getBackTime();
    void increaseBackTime();

    String getAppLocale();
    void setAppLocale(String locale);

    ImageToPDFOptions getImageToPDFOptions();
    void saveImageToPDFOptions(ImageToPDFOptions imageToPDFOptions);

    ViewPdfOption getViewPDFOptions();
    void saveViewPDFOptions(ViewPdfOption viewPdfOption);

    void setTheme(int theme);
    int getTheme();

    Observable<Boolean> saveRecent(RecentData recentData);
    Observable<List<RecentData>> getListRecent();
    Observable<Boolean> saveRecent(String filePath, String type);
    Observable<Boolean> clearRecent(String filePath);
    Observable<RecentData> getRecentByPath(String filePath);

    Observable<Boolean> saveBookmark(BookmarkData bookmarkData);
    Observable<Boolean> saveBookmark(String filePath);
    Observable<List<BookmarkData>> getListBookmark();
    Observable<BookmarkData> getBookmarkByPath(String path);
    Observable<Boolean> clearBookmarkByPath(String path);
    Observable<Boolean> clearAllBookmark();
}
