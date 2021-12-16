package com.pdfreader.scanner.pdfviewer.listener;

public interface OnFileItemWithOptionClickListener {
    void onClickItem(int position);
    void onMainFunctionItem(int position);
    void onOptionItem(int position);
    void onBookMarkItem(int posision,boolean add);
}
