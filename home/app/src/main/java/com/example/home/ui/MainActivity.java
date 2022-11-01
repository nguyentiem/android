package com.example.home.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.home.R;
import com.example.home.ui.animation.MyBounceInterpolator;
import com.example.home.ui.dashboard.DashboardFragment;
import com.example.home.ui.home.HomeFragment;
import com.example.home.ui.notifications.NotificationsFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;


import com.example.home.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_add:

                return true;
            case R.id.menu_del:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    Fragment[] fragments = new Fragment[3];
    MainViewModel model;
    FragmentManager fragmentManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView(){
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        model =   new ViewModelProvider(this).get(MainViewModel.class);
        model.init(getApplicationContext(),this);
        fragments[0] = new HomeFragment(model);
        fragments[1] = new DashboardFragment(model);
        fragments[2] = new NotificationsFragment(model);
        fragmentManager = getSupportFragmentManager();
        switchFragment(0);
        binding.navView.setSelectedItemId(R.id.navigation_home);

        binding.toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                gan them ham giao dong cho animation
                final Animation myAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
                // Use bounce interpolator with amplitude 0.2 and frequency 20
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
                binding.toolbarBack.startAnimation(myAnim);
            }
        });
        binding.navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case  R.id.navigation_dashboard:
                        switchFragment(1);
                        return true;
                    case R.id.navigation_home:
                        switchFragment(0);
                        return true ;
                    case R.id.navigation_notifications:
                        switchFragment(2);
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    public void switchFragment(int i){

        Log.d(TAG, "switchFragment: "+binding.layoutFragment);
        switch (i){
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.layout_fragment, fragments[0])
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.layout_fragment, fragments[1])
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.layout_fragment, fragments[2])
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                break;
            default:
                break;
        }
    }
}