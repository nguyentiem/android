package com.example.rxretrofit.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.rxretrofit.R;
import com.example.rxretrofit.adapter.MainViewModelProvider;
import com.example.rxretrofit.adapter.RvAdapter;
import com.example.rxretrofit.data.User;
import com.example.rxretrofit.viewmodel.MainVewmodel;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
     Button get, next;
     RecyclerView recyclerView;
     MainVewmodel mainVewmodel;
     MainViewModelProvider mainViewModelProvider;
    RvAdapter adapter;
    public static Disposable disposable;
    public static Context context;
//    @Override
//    public Context getApplicationContext() {
//        return super.getApplicationContext();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        get =findViewById(R.id.button);
        next =findViewById(R.id.button1);
        recyclerView = findViewById(R.id.rv);

        context =getApplicationContext();

         adapter = new RvAdapter();
         recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
         mainViewModelProvider = new MainViewModelProvider();
         mainVewmodel = new ViewModelProvider(this,mainViewModelProvider).get(MainVewmodel.class);
//        mainVewmodel =
         mainVewmodel.getUser().observe(this, new Observer<List<User>>() {
             @Override
             public void onChanged(List<User> user) {
                 adapter.setUsers(user);
                 adapter.notifyDataSetChanged();
             }
         });
         get.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

             }
         });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable!=null) disposable.dispose();
    }
}