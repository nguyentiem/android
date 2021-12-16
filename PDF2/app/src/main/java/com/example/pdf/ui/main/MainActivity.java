package com.pdfreader.scanner.pdfviewer.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.ads.control.Admod;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.pdfreader.scanner.pdfviewer.BuildConfig;
import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.constants.AppConstants;
import com.pdfreader.scanner.pdfviewer.constants.DataConstants;
import com.pdfreader.scanner.pdfviewer.data.DataManager;
import com.pdfreader.scanner.pdfviewer.data.model.NewPDFOptions;
import com.pdfreader.scanner.pdfviewer.databinding.ActivityMainBinding;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseBindingActivity;

import com.pdfreader.scanner.pdfviewer.ui.component.DataOptionDialog;

import com.pdfreader.scanner.pdfviewer.ui.component.SettingSortDialog;
import com.pdfreader.scanner.pdfviewer.utils.DialogFactory;
import com.pdfreader.scanner.pdfviewer.utils.FirebaseUtils;
import com.pdfreader.scanner.pdfviewer.utils.ToastUtils;
import com.pdfreader.scanner.pdfviewer.utils.adapter.FragmentViewPagerAdapter;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;
import com.pdfreader.scanner.pdfviewer.utils.pdf.ImageToPdfConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends BaseBindingActivity<ActivityMainBinding, MainViewModel> implements MainNavigator {

    public static final String HOME = "HOME";
    public static final String BOOKMARK = "BOOKMARK";
    public static final String HISTORY = "HISTORY";
    public static final String BROWSES = "BROWSES";
    public static final String MORE = "MORE";

    public static final List<String> mListScreenId = Arrays.asList(HOME, BOOKMARK, HISTORY, BROWSES, MORE);
    private int mCurrentScreen;
    private MenuItem mPrevMenuItem;
    private MainViewModel mMainViewModel;
    private ActivityMainBinding mActivityMainBinding;
    private Boolean mIsFabOpen = false;
    private final int REQUEST_EXTERNAL_PERMISSION_FOR_MAIN = 1;
    private SweetAlertDialog mRequestPermissionDialog;
    private FragmentViewPagerAdapter mFragmentViewPagerAdapter;
    private SettingSortDialog settingSortDialog ;

    @SuppressLint("NonConstantResourceId")
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                // all file
                goToScreen(HOME, false);
                return true;
            case R.id.navigation_bookmark:
                // bookmark
                goToScreen(BOOKMARK, false);
                return true;
            case R.id.navigation_history:
                // recent
                goToScreen(HISTORY, false);
                return true;

            case R.id.navigation_more:
                // more
                goToScreen(MORE, false);
                return true;
        }

        return false;
    };

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public MainViewModel getViewModel() {
        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        return mMainViewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainViewModel.setNavigator(this);
        mActivityMainBinding = getViewDataBinding();

        initView();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentScreen != 0) {
            if (mCurrentScreen != 3) {
                goToScreen(HOME, false);
            } else {
//                BrowserFragment browserFragment = mFragmentViewPagerAdapter.getBrowserFragment();
//                if (browserFragment != null) {
//                    if (!browserFragment.isCurrentFolderRoot()) {
//                        browserFragment.onBackPress();
//                    } else {
//                        goToScreen(HOME, false);
//                    }
//                } else {
                    restartApp(false);
//                }
            }
        } else {
            DataManager dataManager = DataManager.getInstance(this);
            List<Integer> requestRateTimeList = Arrays.asList(0, 1, 2, 3, 5, 7, 9);
            if (requestRateTimeList.contains(dataManager.getBackTime())) {
                dataManager.increaseBackTime();
                if (!dataManager.checkRatingUsDone()) {
                    showRatingUsPopup(super::onBackPressed, super::onBackPressed);
                } else {
                    super.onBackPressed();
                }
            } else {
                dataManager.increaseBackTime();
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == BaseBindingActivity.RESULT_NEED_FINISH) {
                finish();
                setResult(RESULT_NEED_FINISH);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initView() {
        setTabView();
        checkPermissionOnMain();

        preloadMyPdfAdsIfInit();

//        remove done because don't have menu remove/add password in home anymore
//        preloadDoneAdsIfInit();

        Admod.getInstance().loadBanner(this, BuildConfig.banner_home_id);

        mActivityMainBinding.searchToolbar.setOnClickListener(view -> gotoActivityWithFlag(AppConstants.FLAG_SEARCH_PDF));

        setForFabOpenPdf();
    }

    private void setForFabOpenPdf() {
        Animation mFabClockAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_lock);
        Animation mFabAnticlockAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_antilock);


    }

    private void updateFabStatus() {


        Animation mFabCloseAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        Animation mFabOpenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        Animation mTextOpenAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_open);
        Animation mTextCloseAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_close);
        Animation fabAnimation, textAnimation;

        if (mIsFabOpen) {
            fabAnimation = mFabOpenAnimation;
            textAnimation = mTextOpenAnimation;
        } else {
            fabAnimation = mFabCloseAnimation;
            textAnimation = mTextCloseAnimation;
        }


    }

    private void checkPermissionOnMain() {
        if (notHaveStoragePermission()) {
            mRequestPermissionDialog = DialogFactory.getDialogRequestSomething(this, getString(R.string.title_need_permission), getString(R.string.need_permission_to_get_file));
            mRequestPermissionDialog.setConfirmClickListener(sweetAlertDialog -> {
                requestReadStoragePermissionsSafely(REQUEST_EXTERNAL_PERMISSION_FOR_MAIN);
            });
            mRequestPermissionDialog.setCancelClickListener(sweetAlertDialog -> {
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText(getString(R.string.title_need_permission_fail));
                sweetAlertDialog.setContentText(getString(R.string.reject_read_file));
                sweetAlertDialog.setConfirmClickListener(Dialog::dismiss);
                sweetAlertDialog.showCancelButton(false);
                sweetAlertDialog.setConfirmText(getString(R.string.confirm_text));
            });
            mRequestPermissionDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_PERMISSION_FOR_MAIN:
                if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mRequestPermissionDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    mRequestPermissionDialog.setTitleText(getString(R.string.thankyou_text));
                    mRequestPermissionDialog.setContentText(getString(R.string.thank_you_for_support));
                    mRequestPermissionDialog.showCancelButton(false);
                    mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                    mRequestPermissionDialog.setConfirmClickListener(Dialog::dismiss);
                } else {
                    mRequestPermissionDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    mRequestPermissionDialog.setTitleText(getString(R.string.title_need_permission_fail));
                    mRequestPermissionDialog.setContentText(getString(R.string.reject_read_file));
                    mRequestPermissionDialog.showCancelButton(false);
                    mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                    mRequestPermissionDialog.setConfirmClickListener(Dialog::dismiss);
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsRequestFullPermission) {
            mIsRequestFullPermission = false;

            if (!notHaveStoragePermission()) {
                mRequestPermissionDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                mRequestPermissionDialog.setTitleText(getString(R.string.thankyou_text));
                mRequestPermissionDialog.setContentText(getString(R.string.thank_you_for_support));
                mRequestPermissionDialog.showCancelButton(false);
                mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                mRequestPermissionDialog.setConfirmClickListener(Dialog::dismiss);
            } else {
                mRequestPermissionDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                mRequestPermissionDialog.setTitleText(getString(R.string.title_need_permission_fail));
                mRequestPermissionDialog.setContentText(getString(R.string.reject_read_file));
                mRequestPermissionDialog.showCancelButton(false);
                mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                mRequestPermissionDialog.setConfirmClickListener(Dialog::dismiss);
            }
        }
    }

    @Override
    protected void setClick() {
    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public void setClickSortItem(View.OnClickListener listener) {
//        mActivityMainBinding.sortToolbar.setOnClickListener(listener);
    }



    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    private void setTabView() {
        goToScreen(HOME, false);
        mFragmentViewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mActivityMainBinding.viewpager.setOffscreenPageLimit(5);
        mActivityMainBinding.viewpager.disableScroll(true);

        mActivityMainBinding.viewpager.setAdapter(mFragmentViewPagerAdapter);

        mActivityMainBinding.bottomBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mActivityMainBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            // chon trang
            @Override
            public void onPageSelected(int position) {
                if (mPrevMenuItem != null)
                    mPrevMenuItem.setChecked(false);
                else
                    mActivityMainBinding.bottomBar.getMenu().getItem(0).setChecked(false);

                mCurrentScreen = position;

                mActivityMainBinding.bottomBar.getMenu().getItem(position).setChecked(true);
                mPrevMenuItem = mActivityMainBinding.bottomBar.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setVisibleBtnToolbar(int visibleSortToolbar) {
        mActivityMainBinding.searchToolbar.setVisibility(visibleSortToolbar);
//        mActivityMainBinding.sortToolbar.setVisibility(visibleSortToolbar);
    }

    public void goToScreen(String screenId, boolean isDelay) {
        int indexScreen = mListScreenId.indexOf(screenId);
        if (indexScreen == -1) return;

        if (indexScreen < mListScreenId.size()) {
            mCurrentScreen = indexScreen;

            if (isDelay) {
                mActivityMainBinding.viewpager.postDelayed(() -> mActivityMainBinding.viewpager.setCurrentItem(indexScreen, false), 200);
            } else {
                mActivityMainBinding.viewpager.setCurrentItem(indexScreen, false);
            }
             // toolbar an d
            //setVisibleBtnToolbar(View.GONE);
            if (indexScreen == 0) {
                mActivityMainBinding.titleToolbar.setText(getString(R.string.title_navigation_lib_header));
                setVisibleBtnToolbar(View.VISIBLE);
            } else if (indexScreen == 1) {
                //FirebaseUtils.sendEventFunctionUsed(this, "Go to Bookmark screen", "From home");
                mActivityMainBinding.titleToolbar.setText(getString(R.string.title_navigation_bookmark));
            } else if (indexScreen == 2) {
                //FirebaseUtils.sendEventFunctionUsed(this, "Go to History screen", "From home");
                mActivityMainBinding.titleToolbar.setText(getString(R.string.title_navigation_history));
            } else if (indexScreen == 3) {
               // FirebaseUtils.sendEventFunctionUsed(this, "Go to Browser screen", "From home");
                mActivityMainBinding.titleToolbar.setText(getString(R.string.title_navigation_browser));
            } else if (indexScreen == 4) {
               // FirebaseUtils.sendEventFunctionUsed(this, "Go to News screen", "From home");
                mActivityMainBinding.titleToolbar.setText(getString(R.string.title_navigation_news));
            }

        }
    }
}
