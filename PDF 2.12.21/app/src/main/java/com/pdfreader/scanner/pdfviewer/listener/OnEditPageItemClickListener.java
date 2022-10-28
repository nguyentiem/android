package com.pdfreader.scanner.pdfviewer.listener;

public interface OnEditPageItemClickListener {
    void onDeleteItem(int position);
    void onSwap(int oldPosition, int newPosition);
}
