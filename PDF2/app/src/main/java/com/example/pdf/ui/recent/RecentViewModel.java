package com.pdfreader.scanner.pdfviewer.ui.recent;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.pdfreader.scanner.pdfviewer.data.model.RecentData;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecentViewModel extends BaseViewModel<RecentNavigator> {

    private ArrayList<RecentData> mListFile = new ArrayList<>();
    private MutableLiveData<List<RecentData>> mListFileLiveData = new MutableLiveData<>();

    public RecentViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<RecentData>> getListFileLiveData() {
        return mListFileLiveData;
    }

    public void getFileList() {
        getCompositeDisposable().add(getDataManager()
                .getListRecent()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    if (response != null && response.size() > 0) {
                        mListFileLiveData.postValue(response);
                    } else {
                        mListFileLiveData.postValue(new ArrayList<>());
                    }
                }, throwable -> {
                    mListFileLiveData.postValue(new ArrayList<>());
                })
        );
    }
}
