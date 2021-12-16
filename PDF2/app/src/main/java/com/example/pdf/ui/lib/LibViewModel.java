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
import java.util.Optional;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LibViewModel extends BaseViewModel<LibNavigator>  {
    private final String TAG = "model";
    boolean flag1 =false;
    boolean flag2 = false;
    private LibFileAsyncTask mAsyncTask;

    private List<FileData> mListFile;
    private MutableLiveData<List<FileData>> mListFileLiveData = new MutableLiveData<>();
    public MutableLiveData<List<FileData>> getListFileLiveData() {
        return mListFileLiveData;
    }

//    Optional<FileData> matchingObject = objects.stream().
//            filter(p -> p.email().equals("testemail")).
//            findFirst();

    private List<FileData> allFile=new ArrayList<>();
    private List<BookmarkData> bookmarkDataList = new ArrayList<>();
    public LibViewModel(@NonNull Application application) {
        super(application);
    }

    public void getAllList(int order) {


        mAsyncTask = new LibFileAsyncTask(getApplication(), result -> {

            allFile = result;
            flag1 =true;
            if(flag2==true){
                flag1 = false;
                merge();
            }
            }, order);

        mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
public  void merge(){
    Completable.create(new CompletableOnSubscribe() {
        @Override
        public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
            for(FileData filei : allFile) {
                for (BookmarkData filej : bookmarkDataList) {
                    if (filei.getFilePath().equals(filej.getFilePath())) {
                        filei.setBookMark(true);
                        bookmarkDataList.remove(filej);
                    }
                }
            }
            emitter.onComplete();
        }
    }).subscribeOn(Schedulers.io())
            .observeOn(getSchedulerProvider().ui())
            .subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onComplete() {
                    Log.d(TAG, "onComplete: ");
                    mListFile = new ArrayList<>(allFile);
                    mListFileLiveData.postValue(mListFile);
                }

                @Override
                public void onError(@NonNull Throwable e) {

                }
            });
}
    public void getBookMarkList() {
        getCompositeDisposable().add(getDataManager()
                .getListBookmark()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    if (response != null && response.size() > 0) {
                        bookmarkDataList = response;
                        flag2 = true;
                        if(flag1==true){
                            flag2 = false;
                           merge();
                        }
                    } else {
                    bookmarkDataList =new ArrayList<>();

                        flag2 = true;
                        if(flag1==true){
                            merge();
                        }
                    }
                }, throwable -> {
                    bookmarkDataList =new ArrayList<>();
                    flag2 = true;
                    if(flag1==true){
                        merge();
                    }
                })
        );
    }



    public void getFileList(int order){
        getAllList( order);
        getBookMarkList();
        flag1 =false;
        flag2 = false;

    }
}
