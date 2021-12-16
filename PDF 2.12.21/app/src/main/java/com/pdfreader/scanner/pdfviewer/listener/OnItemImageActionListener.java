package com.pdfreader.scanner.pdfviewer.listener;

public interface OnItemImageActionListener {
    void onClick(int position);
    void onDownload(int position);
    void onShare(int position);
    void onDelete(int position);
}
