package com.example.in4code.ui.main;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.in4code.BuildConfig;
import com.example.in4code.R;
import com.example.in4code.databinding.ActivityMainBinding;
import com.example.in4code.ui.main.favorite.FavoriteFragment;
import com.example.in4code.ui.main.home.HomeFragment;
import com.example.in4code.ui.main.list.ListFragment;
import com.example.in4code.ui.main.recent.RecentFragment;
import com.example.in4code.ui.qrgallary.GallaryQRCodeActivity;
import com.example.in4code.ui.scan.DialogFactory;
import com.example.in4code.ui.scan.ScanActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements MainActivityNavigation {
    public static final String TAG = "log";
    private final int REQUEST_EXTERNAL_PERMISSION_FOR_CREATE_FILE = 1;

    public ActivityMainBinding getBinding() {
        return binding;
    }

    private SweetAlertDialog mRequestPermissionDialog;
    private ActivityMainBinding binding;

    public static final String HOME = "HOME";
    public static final String RECENT = "RECENT";
    public static final String FAVORITE = "FAVORITE";
    public static final String LIST = "LIST";
    private final int framelayout = R.id.frag_main;
    public static int mCurrentScreen = 0;
//    private List<Fragment> fragmentList = new ArrayList<>();
    public static final List<String> mListScreenId = Arrays.asList(HOME, RECENT, FAVORITE, LIST);
    FragmentManager fragmentManager;
    MainViewModel mainViewModel;

    public MainViewModel getViewModel() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        return mainViewModel;
    }

    public void requestPermission() {


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

    public void initView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fragmentManager = getSupportFragmentManager();
        mainViewModel =getViewModel();
        mainViewModel.setContext(this);
        fragmentManager.beginTransaction().add(framelayout, mainViewModel.getFragmentList().get(0), null).commit();
        binding.mainLayout.appBarLayout.searchToolbar.setVisibility(View.GONE);
        binding.mainLayout.appBarLayout.menuNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        binding.mainLayout.appBarLayout.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GallaryQRCodeActivity.class);
                startActivity(intent);
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
                switch (item.getItemId()) {
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

    public void animationCreate() {
        Animation mFabOpenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.creatte_open);
        Animation mFabCloseAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.create_close);
        binding.mainLayout.fabCreate.startAnimation(mFabOpenAnimation);
        //binding.mainLayout.fabCreate.startAnimation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        checkPermissionBeforeCreateFile();
    }

    public boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public boolean notHaveStoragePermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            return (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && !hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
        } else {
            return (!Environment.isExternalStorageManager());
        }
    }

    public void requestReadStoragePermissionsSafely(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    private void checkPermissionBeforeCreateFile() {
        if (notHaveStoragePermission()) {
            mRequestPermissionDialog = DialogFactory.getDialogRequestSomething(this, getString(R.string.title_need_permission), getString(R.string.need_permission_to_create_file));
            mRequestPermissionDialog.setConfirmClickListener(sweetAlertDialog -> {
                requestReadStoragePermissionsSafely(REQUEST_EXTERNAL_PERMISSION_FOR_CREATE_FILE);
                sweetAlertDialog.dismiss();
            });
            mRequestPermissionDialog.setCancelClickListener(sweetAlertDialog -> {
                sweetAlertDialog.dismiss();
                finish();
            });
            mRequestPermissionDialog.show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_PERMISSION_FOR_CREATE_FILE:
                if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mRequestPermissionDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    mRequestPermissionDialog.setTitleText(getString(R.string.thankyou_text));
                    mRequestPermissionDialog.setContentText(getString(R.string.create_file_now));
                    mRequestPermissionDialog.showCancelButton(false);
                    mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                    mRequestPermissionDialog.setConfirmClickListener(sweetAlertDialog -> {
                        //   startCreatePdfActivity();
                        sweetAlertDialog.dismiss();
                    });
                } else {
                    mRequestPermissionDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    mRequestPermissionDialog.setTitleText(getString(R.string.title_need_permission_fail));
                    mRequestPermissionDialog.setContentText(getString(R.string.couldnt_create_file_now));
                    mRequestPermissionDialog.showCancelButton(false);
                    mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                    mRequestPermissionDialog.setConfirmClickListener(Dialog::dismiss);
                    finish();
                }
                break;

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    public void goToScreen(String screenId) {
        int indexScreen = mListScreenId.indexOf(screenId);
        if (indexScreen == -1) return;

        if (indexScreen>=0&&indexScreen < mListScreenId.size()) {
            mCurrentScreen = indexScreen;
            fragmentManager.beginTransaction().replace(framelayout,mainViewModel.getFragmentList().get(mCurrentScreen), null).commit();
            switch (indexScreen) {
                case 0:
//                    fragmentManager.beginTransaction().replace(framelayout, new HomeFragment(this), null).commit();
                    binding.mainLayout.appBarLayout.searchToolbar.setVisibility(View.GONE);
                    binding.mainLayout.appBarLayout.titleToolbar.setText(mListScreenId.get(indexScreen));
                    break;
                case 1:
//                    fragmentManager.beginTransaction().replace(framelayout, new RecentFragment(this), null).commit();
                    binding.mainLayout.appBarLayout.searchToolbar.setVisibility(View.VISIBLE);
                    binding.mainLayout.appBarLayout.titleToolbar.setText(mListScreenId.get(indexScreen));
                    break;
                case 2:
//                    fragmentManager.beginTransaction().replace(framelayout, new FavoriteFragment(this), null).commit();
                    binding.mainLayout.appBarLayout.searchToolbar.setVisibility(View.VISIBLE);
                    binding.mainLayout.appBarLayout.titleToolbar.setText(mListScreenId.get(indexScreen));
                    break;
                case 3:
//                    fragmentManager.beginTransaction().replace(framelayout, new ListFragment(this), null).commit();
                    binding.mainLayout.appBarLayout.searchToolbar.setVisibility(View.VISIBLE);
                    binding.mainLayout.appBarLayout.titleToolbar.setText(mListScreenId.get(indexScreen));
                    break;

            }
        }
    }
}