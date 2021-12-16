package com.example.in4code.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;

import com.example.in4code.R;
import com.example.in4code.databinding.ActivityMainBinding;
import com.example.in4code.ui.favorite.FavoriteFragment;
import com.example.in4code.ui.home.HomeFragment;
import com.example.in4code.ui.list.ListFragment;
import com.example.in4code.ui.recent.RecentFragment;
import com.example.in4code.ui.scan.ScanActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainActivityNavigation{
    public static final String TAG = "log";

    public ActivityMainBinding getBinding() {
        return binding;
    }

    private ActivityMainBinding binding;
    private FragmentViewPagerAdapter fragmentViewPagerAdapter;
    public static final String HOME = "HOME";
    public static final String RECENT = "RECENT";
    public static final String FAVORITE = "FAVORITE";
    public static final String LIST = "LIST";
    private final int framelayout = R.id.frag_main;
    public static int mCurrentScreen = 0;
    private List<Fragment> fragmentList =new ArrayList<>();
    public static final List<String> mListScreenId = Arrays.asList(HOME, RECENT, FAVORITE, LIST);
    FragmentManager fragmentManager;
    MainViewModel mainViewModel;

    public MainViewModel getViewModel(){
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        return mainViewModel;
    }



    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (mCurrentScreen > 0 && mCurrentScreen < 5 && mCurrentScreen != 2) {
                //goto home
                goToScreen(HOME);
            } else {
                super.onBackPressed();
            }
        }
    }
public void initView(){
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    fragmentManager =getSupportFragmentManager();
    fragmentList.add(new HomeFragment((MainActivityNavigation) this));
    fragmentList.add(new RecentFragment(this));
    fragmentList.add(new FavoriteFragment(this));
    fragmentList.add(new ListFragment(this));
    goToScreen(HOME);
    binding.mainLayout.appBarLayout.menuNavigation.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           binding.drawerLayout.openDrawer(Gravity.LEFT);
       }
   });
   binding.mainLayout.appBarLayout.profile.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {

       }
   });
        binding.mainLayout.appBarLayout.searchToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
      binding.mainLayout.bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              switch (item.getItemId()) {
                  case R.id.bottom_navigation_home:

                       goToScreen(HOME);
                      return true;
                  case R.id.bottom_navigation_recent:

                       goToScreen(RECENT);
                      return true;

                  case R.id.bottom_navigation_favorite:

                       goToScreen(FAVORITE);
                      return true;
                  case R.id.bottom_navigation_list:

                      goToScreen(LIST);
                      return true;
              }
              return false;
          }
      });
    binding.mainLayout.fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, ScanActivity.class);
            startActivity(intent);
        }
    });
    binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_theme:

                    return true;

                case R.id.nav_share_app:

                    return true;
                case R.id.nav_rate:

                    return true;
                case R.id.nav_feedback:

                    return true;

                case R.id.nav_more:

                    return true;
                case R.id.nav_policy:

                    return true;
            }
            return false;
        }
    });
    binding.mainLayout.fabCreate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            animationCreate();

        }
    });
}
public void animationCreate(){
    Animation mFabOpenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.creatte_open);
    Animation mFabCloseAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.create_close);
    binding.mainLayout.fabCreate.startAnimation(mFabOpenAnimation);
    //binding.mainLayout.fabCreate.startAnimation();
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }



    public void goToScreen(String screenId) {
        int indexScreen = mListScreenId.indexOf(screenId);
        if (indexScreen == -1) return;

        if (indexScreen < mListScreenId.size()) {
            mCurrentScreen = indexScreen;
            fragmentManager.beginTransaction().replace(framelayout,fragmentList.get(indexScreen),null).commit();
            binding.mainLayout.appBarLayout.titleToolbar.setText(mListScreenId.get(indexScreen));
        }
    }
}