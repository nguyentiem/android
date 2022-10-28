package com.pdfreader.scanner.pdfviewer.ui.lib;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.pdfreader.scanner.pdfviewer.data.model.BookmarkData;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LibViewModel extends BaseViewModel<LibNavigator> {
    private final String TAG = "libFragment";

    private LibFileAsyncTask mAsyncTask;
    private List<FileData> mListFile;
    private MutableLiveData<List<FileData>> mListFileLiveData = new MutableLiveData<>();

    public MutableLiveData<List<FileData>> getListFileLiveData() {
        return mListFileLiveData;
    }

    Disposable disposable;
    private List<FileData> allFile = new ArrayList<>();
    private List<BookmarkData> bookmarkDataList = new ArrayList<>();

    public LibViewModel(@NonNull Application application) {
        super(application);
    }

    public void getAllList(int order) {


        mAsyncTask = new LibFileAsyncTask(getApplication(), result -> {

            allFile = result;
            getBookMarkList();

        }, order);

        mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    public void getBookMarkList() {
        getCompositeDisposable().add(getDataManager()
                .getListBookmark()
                .subscribeOn(Schedulers.newThread())
                .observeOn(getSchedulerProvider().io())
                .subscribe(response -> {

                    if (response != null && response.size() > 0) {
                        bookmarkDataList = response;
                         merge(allFile,bookmarkDataList);
                    } else {
                        bookmarkDataList = new ArrayList<>();

                    }
                    mListFile =new ArrayList<>(allFile);
                    mListFileLiveData.postValue(mListFile);
                }, throwable -> {
                    bookmarkDataList = new ArrayList<>();

                })
        );
    }


    public void getFileList(int order) {

        getAllList(order);


    }
}
