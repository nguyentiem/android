package com.pdfreader.scanner.pdfviewer.ui.more;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseViewModel;
import com.pdfreader.scanner.pdfviewer.ui.lib.LibFileAsyncTask;
import com.pdfreader.scanner.pdfviewer.ui.lib.LibNavigator;

import java.util.ArrayList;
import java.util.List;

public class MoreViewModel  extends BaseViewModel<MoreNavigator> {


    private List<FileData> mListFile;
    private MutableLiveData<List<FileData>> mListFileLiveData = new MutableLiveData<>();
    public MutableLiveData<List<FileData>> getListFileLiveData() {
        return mListFileLiveData;
    }

    public MoreViewModel(@NonNull Application application) {
        super(application);
    }



}
