package com.pdfreader.scanner.pdfviewer.data.local.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.pdfreader.scanner.pdfviewer.data.local.database.dao.BookmarkDataDao;
import com.pdfreader.scanner.pdfviewer.data.local.database.dao.RecentDataDao;
import com.pdfreader.scanner.pdfviewer.data.model.BookmarkData;
import com.pdfreader.scanner.pdfviewer.data.model.RecentData;

@Database(entities = {RecentData.class, BookmarkData.class}, version = 1, exportSchema = false)
public abstract class ApplicationDatabase extends RoomDatabase {
    public abstract RecentDataDao recentDataDao();
    public abstract BookmarkDataDao bookmarkDataDao();
}
