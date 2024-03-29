package com.pdfreader.scanner.pdfviewer.ui.search;

import android.content.Context;
import android.os.AsyncTask;

import com.pdfreader.scanner.pdfviewer.constants.DataConstants;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;

import java.util.List;

public class SearchFileAsyncTask extends AsyncTask<Object, Object, Object> {

    private final Context mContext;
    private SearchFileListener mListener;
    private boolean mIsSearchAll = DataConstants.USE_DEEP_SEARCH;
    private String mFileType;

    public SearchFileAsyncTask(Context context, SearchFileListener listener, String fileType) {
        mContext = context;
        mListener = listener;
        mFileType = fileType;
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
            List<FileData> resultAll;
            if (mIsSearchAll) {
                resultAll = FileUtils.getAllExternalFileList(mContext, mFileType, FileUtils.SORT_BY_DATE_DESC);
            } else {
                resultAll = FileUtils.getExternalFileList(mContext, mFileType, FileUtils.SORT_BY_DATE_DESC);
            }

            if (!isCancelled() && mListener != null) {
                mListener.loadDone(resultAll);
            }
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
