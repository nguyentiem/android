package com.example.rxretrofit.viewmodel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rxretrofit.api.RetrofitAPI;
import com.example.rxretrofit.data.User;
import com.example.rxretrofit.view.MainActivity;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainVewmodel extends ViewModel {
    RetrofitAPI retrofitAPI;
    public final String TAG = "GETDATA";
    public MainVewmodel(RetrofitAPI retrofitAPI) {
        this.retrofitAPI = retrofitAPI;
    }

    MutableLiveData<List<User>> list = new MutableLiveData<>();
    public LiveData<List<User>> getUser(){
          retrofitAPI.getUser().subscribeOn(Schedulers.io())
                               .observeOn(AndroidSchedulers.mainThread())
                               .subscribe(new Observer<List<User>>() {
                                   @Override
                                   public void onSubscribe(@NonNull Disposable d) {
                                       Log.d(TAG, "onSubscribe: ");
                                       MainActivity.disposable =d ;
                                   }

                                   @Override
                                   public void onNext(@NonNull List<User> user) {
                                       Log.d(TAG, "onNext: "+user.size());
                                       list.setValue(user);
                                   }

                                   @Override
                                   public void onError(@NonNull Throwable e) {
                                       Log.d(TAG, "onError: "+e.getMessage());
//                                      showError();
                                   }

                                   @Override
                                   public void onComplete() {
                                       Log.d(TAG, "onComplete: ");
                                   }
                               });
          return list;
    }


}
