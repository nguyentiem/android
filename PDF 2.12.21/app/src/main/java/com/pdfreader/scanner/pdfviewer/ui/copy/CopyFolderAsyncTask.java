package com.pdfreader.scanner.pdfviewer.ui.copy;

import android.content.Context;
import android.os.AsyncTask;

import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.ui.search.SearchFileListener;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;

import java.util.ArrayList;

public class CopyFolderAsyncTask  extends AsyncTask<Object, Object, Object> {
    private FileData mCurrentPath;
    private final Context mContext;
    private SearchFolderListener mListener;

    public CopyFolderAsyncTask(Context context, SearchFolderListener listener, FileData currentPath) {
        mContext = context;
        mListener = listener;
        mCurrentPath = currentPath;
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
            ArrayList<FileData> allData = FileUtils.getFolderListOfDirectory(mCurrentPath);

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
