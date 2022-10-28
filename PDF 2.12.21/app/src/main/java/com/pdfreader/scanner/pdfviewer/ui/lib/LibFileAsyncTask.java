package com.pdfreader.scanner.pdfviewer.ui.lib;

import android.content.Context;
import android.os.AsyncTask;

import com.pdfreader.scanner.pdfviewer.constants.DataConstants;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.ui.search.SearchFileListener;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;

import java.util.ArrayList;

public class LibFileAsyncTask extends AsyncTask<Object, Object, Object> {

    private  Context mContext;
    private SearchFileListener mListener;
    private int mOrder;

    public LibFileAsyncTask(Context context, SearchFileListener listener, int order) {
        mContext = context;
        mListener = listener;
        this.mOrder = order;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Object doInBackground(Object... objects) {
        try {
            ArrayList<FileData> allData = FileUtils.getAllExternalFileList(mContext, DataConstants.FILE_TYPE_PDF, mOrder);
            // lay danh sach bookmark kiem tra
            if (!isCancelled() && mListener != null) {

                mListener.loadDone(allData);
            }
        } catch (Exception e) {
            mListener.loadDone(new ArrayList<>());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
