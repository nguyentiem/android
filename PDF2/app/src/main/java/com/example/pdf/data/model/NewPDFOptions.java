package com.pdfreader.scanner.pdfviewer.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class NewPDFOptions implements Serializable {

    private int mSelectedType;
    private String mFileName;
    private String mPageSize;
    private int mNumberPage;

    public NewPDFOptions(int mSelectedType, String mFileName, String mPageSize, int mNumberPage) {
        this.mSelectedType = mSelectedType;
        this.mFileName = mFileName;
        this.mPageSize = mPageSize;
        this.mNumberPage = mNumberPage;
    }

    public int getSelectedType() {
        return mSelectedType;
    }

    public void setSelectedType(int mSelectedType) {
        this.mSelectedType = mSelectedType;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public String getPageSize() {
        return mPageSize;
    }

    public void setPageSize(String mPageSize) {
        this.mPageSize = mPageSize;
    }

    public int getNumberPage() {
        return mNumberPage;
    }

    public void setNumberPage(int mNumberPage) {
        this.mNumberPage = mNumberPage;
    }
}
