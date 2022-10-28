package com.pdfreader.scanner.pdfviewer.data.model;

import java.io.Serializable;

public class ViewPdfOption implements Serializable {
    private int mViewMode;
    private int mOrientation;
    private int mSingle;
    public int getmSingle() {
        return mSingle;
    }

    public void setmSingle(int mSingle) {
        this.mSingle = mSingle;
    }




    public ViewPdfOption(int mViewMode, int mOrientation, int mSingle) {
        this.mViewMode = mViewMode;
        this.mOrientation = mOrientation;
        this.mSingle = mSingle;
    }

    public int getViewMode() {
        return mViewMode;
    }

    public void setViewMode(int mViewMode) {
        this.mViewMode = mViewMode;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setOrientation(int mOrientation) {
        this.mOrientation = mOrientation;
    }
}
