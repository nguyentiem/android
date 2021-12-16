package com.pdfreader.scanner.pdfviewer.ui.copy;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseViewModel;
import com.pdfreader.scanner.pdfviewer.ui.browser.BrowserFileAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class CopyViewModel extends BaseViewModel<CopyNavigation> {
    private FileData mCurrentPath;
    private CopyFolderAsyncTask mAsyncTask;
    private List<FileData> mListFolder;
    private MutableLiveData<List<FileData>> mListFolderLiveData = new MutableLiveData<>();
    public MutableLiveData<List<FileData>> getmListFolderLiveData() {
        return mListFolderLiveData;
    }
    public CopyViewModel(@NonNull Application application) {
        super(application);
    }

    public void setCurrentPath(FileData currentPath) {
        this.mCurrentPath = currentPath;
    }

    public void getFolderList() {
        mAsyncTask = new CopyFolderAsyncTask(getApplication(), result -> {
            mListFolder = new ArrayList<>(result);
            mListFolderLiveData.postValue(mListFolder);
        }, mCurrentPath);
        mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
