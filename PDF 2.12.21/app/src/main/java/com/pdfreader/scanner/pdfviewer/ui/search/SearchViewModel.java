package com.pdfreader.scanner.pdfviewer.ui.search;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.pdfreader.scanner.pdfviewer.constants.DataConstants;
import com.pdfreader.scanner.pdfviewer.data.model.BookmarkData;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

public class SearchViewModel extends BaseViewModel<SearchNavigator> {
    private int mTypeSearch;
    private String mKeyword;
    private String mCurrentKeyword = null;
    private boolean mIsLoadingList = false;
    private boolean mIsPushingList = false;

    private ArrayList<FileData> mListFile = new ArrayList<>();
    private List<FileData> allFile = new ArrayList<>();
    private List<BookmarkData>bookmarkDataList =new ArrayList<>();

    private MutableLiveData<List<FileData>> mListFileLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> mLoadListLiveData = new MutableLiveData<>();

    private SearchFileAsyncTask mAsyncTask;

    public SearchViewModel(@NonNull Application application) {
        super(application);
    }
    public void merge(List<FileData> lfile1,List<BookmarkData> lfile2) {
        for(FileData file :lfile1){
            for(BookmarkData bm: lfile2){
                if(file.getFilePath().equals(bm.getFilePath())){
                    file.setBookMark(true);
                    // bookmarkDataList.remove(bm);
                }
            }
        }
    }
    public void getBookMarkList(boolean needReloadList, boolean pullRefresh) {
        getCompositeDisposable().add(getDataManager()
                .getListBookmark()
                .subscribeOn(Schedulers.newThread())
                .observeOn(getSchedulerProvider().io())
                .subscribe(response -> {

                    if (response != null && response.size() > 0) {
                        bookmarkDataList = response;
                       merge(allFile,bookmarkDataList);
                        mListFile = new ArrayList<>(allFile);
                        mLoadListLiveData.postValue(true);
                        mIsLoadingList = false;
                        pushResult(pullRefresh);
                    } else {
                        bookmarkDataList = new ArrayList<>();
                        mListFile = new ArrayList<>(allFile);
                        mLoadListLiveData.postValue(true);
                        mIsLoadingList = false;
                        pushResult(pullRefresh);

                    }

                }, throwable -> {
                    bookmarkDataList = new ArrayList<>();
                    mListFile = new ArrayList<>(allFile);
                    mLoadListLiveData.postValue(true);
                    mIsLoadingList = false;
                    pushResult(pullRefresh);

                })
        );
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

    public void startSeeding(boolean needReloadList, boolean pullRefresh) {

        if (mIsLoadingList) return;

        if (needReloadList) {
            String fileType;

            mIsLoadingList = true;
            mLoadListLiveData.postValue(true);

            if (mTypeSearch == DataConstants.INDEX_TYPE_PDF) {
                fileType = DataConstants.FILE_TYPE_PDF;
            } else if (mTypeSearch == DataConstants.INDEX_TYPE_WORD) {
                fileType = DataConstants.FILE_TYPE_WORD;
            } else {
                fileType = DataConstants.FILE_TYPE_EXCEL;
            }

            mAsyncTask = new SearchFileAsyncTask(getApplication(), result -> {
                allFile =result;
                getBookMarkList(needReloadList,  pullRefresh);


            }, fileType);

            mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            pushResult(pullRefresh);
        }
    }

    private void pushResult(boolean pullRefresh) {
        if (mIsPushingList && !pullRefresh) {
            return;
        }
        mIsPushingList = true;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mKeyword);

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

        Log.d("duynm" + mTypeSearch, "pushResult - " + outputList.size());

        mListFileLiveData.postValue(outputList);
        mIsPushingList = false;

        if (!mCurrentKeyword.equals(mKeyword)) {
            pushResult(pullRefresh);
        }
    }
}
