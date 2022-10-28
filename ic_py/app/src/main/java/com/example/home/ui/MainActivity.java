package com.example.home.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.home.R;
import com.example.home.ui.dashboard.DashboardFragment;
import com.example.home.ui.dashboard.DashboardViewModel;
import com.example.home.ui.home.HomeFragment;
import com.example.home.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.home.databinding.ActivityMainBinding;

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
        return super.onOptionsItemSelected(item);
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                Toast.makeText(getApplicationContext(), "환경설정 버튼 클릭됨", Toast.LENGTH_LONG).show();
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                Toast.makeText(getApplicationContext(), "나머지 버튼 클릭됨", Toast.LENGTH_LONG).show();
//                return super.onOptionsItemSelected(item);
//
//        }
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