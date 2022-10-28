package com.pdfreader.scanner.pdfviewer.ui.copy;

import com.pdfreader.scanner.pdfviewer.data.model.FileData;

import java.util.List;

public interface SearchFolderListener {
    public void loadDone(List<FileData> result);
}
