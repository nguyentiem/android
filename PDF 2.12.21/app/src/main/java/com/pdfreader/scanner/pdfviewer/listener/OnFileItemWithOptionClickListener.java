package com.pdfreader.scanner.pdfviewer.listener;

public interface OnFileItemWithOptionClickListener {
    void onClickItem(int position);
    void onMainFunctionItem(int position);
    void onOptionItem(int position);
    void onBookMarkItem(int posision,boolean add);
    /*
        void openFile(int position);
        void optionBookmark(int position, boolean isAdd);
        void shareFile(int position);
        void copyFile(int position);
        void mergeFile(int position);
        void renameFile(int position);
        void saveDrive(int position);
        void printFile(int position);
        void deleteFile(int position);
        */
}
