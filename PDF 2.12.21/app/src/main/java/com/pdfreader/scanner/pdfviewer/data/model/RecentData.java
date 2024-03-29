package com.pdfreader.scanner.pdfviewer.data.model;

import androidx.room.Entity;

@Entity(
        tableName = "recent_data"
)
public class RecentData extends SavedData {
    public RecentData() {

    }

    public RecentData(FileData copy) {
        this.displayName = copy.getDisplayName();
        this.filePath = copy.getFilePath();
    }
}

