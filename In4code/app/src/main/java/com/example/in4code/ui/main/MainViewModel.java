package com.example.in4code.ui.main;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;

import com.example.in4code.ui.main.favorite.FavoriteFragment;
import com.example.in4code.ui.main.home.HomeFragment;
import com.example.in4code.ui.main.list.ListFragment;
import com.example.in4code.ui.main.recent.RecentFragment;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {


    public void setContext(Context context) {
        this.context = context;
        fragmentList.add(new HomeFragment(this.context));
        fragmentList.add(new RecentFragment());
        fragmentList.add(new FavoriteFragment());
        fragmentList.add(new ListFragment());
    }

    Context context;

    public List<Fragment> getFragmentList() {
        return fragmentList;
    }
    public Context getContext() {
        return context;
    }




    List<Fragment> fragmentList = new ArrayList<>();
    public MainViewModel(@NonNull Application application) {

        super(application);



    }
}
