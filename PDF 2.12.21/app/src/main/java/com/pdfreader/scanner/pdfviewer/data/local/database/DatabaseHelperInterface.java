package com.pdfreader.scanner.pdfviewer.data.local.database;

import com.pdfreader.scanner.pdfviewer.data.model.BookmarkData;
import com.pdfreader.scanner.pdfviewer.data.model.RecentData;

import java.util.List;

import io.reactivex.Observable;

public interface DatabaseHelperInterface {
    // recent
    Observable<Boolean> saveRecent(RecentData recentData);
    Observable<List<RecentData>> getListRecent();
    Observable<Boolean> clearRecent(String filePath);
    Observable<RecentData> getRecentByPath(String filePath);
    // book mark
    Observable<Boolean> saveBookmark(BookmarkData bookmarkData);
    Observable<List<BookmarkData>> getListBookmark();
    Observable<BookmarkData>  getBookmarkByPath(String path);
    Observable<Boolean> clearBookmarkByPath(String path);
    Observable<Boolean> clearAllBookmark();
}
