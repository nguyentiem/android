package com.example.rxretrofit.adapter;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rxretrofit.api.RetrofitAPI;
import com.example.rxretrofit.viewmodel.MainVewmodel;

public class MainViewModelProvider implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass==MainVewmodel.class){
        return (T) new MainVewmodel(RetrofitAPI.getApi());
        }
        else{
           throw new ClassCastException("class is unknow");
        }
    }
}
