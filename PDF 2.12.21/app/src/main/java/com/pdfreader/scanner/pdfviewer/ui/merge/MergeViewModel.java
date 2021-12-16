package com.pdfreader.scanner.pdfviewer.ui.merge;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.pdfreader.scanner.pdfviewer.constants.DataConstants;
import com.pdfreader.scanner.pdfviewer.data.model.BookmarkData;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseViewModel;
import com.pdfreader.scanner.pdfviewer.ui.browser.BrowserFileAsyncTask;
import com.pdfreader.scanner.pdfviewer.ui.browser.BrowserNavigator;
import com.pdfreader.scanner.pdfviewer.ui.lib.LibFileAsyncTask;
import com.pdfreader.scanner.pdfviewer.ui.search.SearchFileAsyncTask;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class MergeViewModel extends BaseViewModel<MergeNavigator> {

    private int mTypeSearch;
    private String mKeyword;

    private String mCurrentKeyword = null;
    private boolean mIsLoadingList = false;

    private ArrayList<FileData> mListFile =new ArrayList<>();
    private List<FileData> allFile ;
    private MutableLiveData<List<FileData>> mListFileLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mLoadListLiveData = new MutableLiveData<>();
    private MergeAsyncTask mergeAsyncTask;
    public MergeViewModel(@NonNull Application application) {
        super(application);
    }



    public void setTypeSearch(int typeSearch, String keyword) {
        mTypeSearch = typeSearch;
        if (keyword != null) {
            mKeyword = keyword.toLowerCase().trim();
        } else {
            mKeyword = "";
        }
    }

    public List<FileData> getListFile() {
        return mListFile;
    }

    public MutableLiveData<List<FileData>> getListFileLiveData() {
        return mListFileLiveData;
    }

    public MutableLiveData<Boolean> getLoadListLiveData() {
        return mLoadListLiveData;
    }

    public void startSeeding( boolean pullRefresh) {
        if (mIsLoadingList) return;
            pushResult();
    }

    private void pushResult() {
        if(allFile==null||allFile.size()==0||mListFile.size()==0){
            getAllList(2) ;
        }


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mKeyword);
        Log.d("TAG", stringBuilder.toString());
        mCurrentKeyword = stringBuilder.toString();

        ArrayList<FileData> outputList = new ArrayList<>();

        if (mListFile.size() > 0) {
            for (FileData fileData : mListFile) {
                if (mCurrentKeyword.length() > 0 && fileData.getDisplayName().toLowerCase().equals("no name")) {
                    continue;
                }
                if (fileData.getDisplayName().toLowerCase().contains(mCurrentKeyword)) {
                    outputList.add(fileData);
                }
            }
        }

        Log.d("TAG" + mTypeSearch, "pushResult - " + outputList.size());

        mListFileLiveData.postValue(outputList);


        if (!mCurrentKeyword.equals(mKeyword)) {
            pushResult();
        }
    }
    public void getAllList(int order) {
        mergeAsyncTask = new MergeAsyncTask(getApplication(), result -> {
            allFile = result;
            mListFile =new ArrayList<>(allFile);
            mListFileLiveData.postValue(allFile);
        }, order);
        mergeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}