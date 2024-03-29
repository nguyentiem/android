package com.pdfreader.scanner.pdfviewer.data.model;

import androidx.room.Entity;

@Entity(
        tableName = "bookmark_data"
)
public class BookmarkData extends SavedData {
    public BookmarkData() {

    }

    public BookmarkData(FileData copy) {
        this.displayName = copy.getDisplayName();
        this.filePath = copy.getFilePath();
    }
}
