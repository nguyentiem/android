package com.pdfreader.scanner.pdfviewer.ui.search;

import com.pdfreader.scanner.pdfviewer.data.model.FileData;

import java.util.List;

public interface SearchFileListener {
    public void loadDone(List<FileData> result);
}
