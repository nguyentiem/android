package com.example.pdf.utils.pdftoimage;

import com.pdfreader.scanner.pdfviewer.data.model.ImageExtractData;

public interface PdfToImageListener {
    void onCreateStart(int type);
    void onCreateFinish(int numberSuccess, int numberError, int type);
    void onUpdateProcess(int numberSuccess, int numberError, ImageExtractData output, int type);
}
