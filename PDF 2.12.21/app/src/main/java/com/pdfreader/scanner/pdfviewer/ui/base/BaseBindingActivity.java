package com.pdfreader.scanner.pdfviewer.ui.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.IntegerRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.ads.control.Admod;
import com.ads.control.funtion.AdCallback;
import com.google.android.gms.ads.InterstitialAd;
import com.pdfreader.scanner.pdfviewer.BuildConfig;
import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.constants.AppConstants;
import com.pdfreader.scanner.pdfviewer.data.DataManager;


import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.ui.main.MainActivity;
import com.pdfreader.scanner.pdfviewer.ui.search.SearchActivity;
import com.pdfreader.scanner.pdfviewer.ui.splash.SplashActivity;
import com.pdfreader.scanner.pdfviewer.ui.viewpdf.ViewPdfActivity;
import com.pdfreader.scanner.pdfviewer.utils.DateTimeUtils;
import com.pdfreader.scanner.pdfviewer.utils.DialogFactory;
import com.pdfreader.scanner.pdfviewer.utils.FirebaseUtils;
import com.pdfreader.scanner.pdfviewer.utils.NetworkUtils;
import com.pdfreader.scanner.pdfviewer.utils.ToastUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.DirectoryUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;
import com.pdfreader.scanner.pdfviewer.utils.pdf.PdfUtils;
import com.rate.control.OnCallback;
import com.rate.control.funtion.RateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public abstract class BaseBindingActivity<T extends ViewDataBinding, V extends BaseViewModel>
        extends AppCompatActivity implements BaseFragment.Callback {

    protected static final int PREVIEW_FILE_REQUEST = 2369;
    protected static final int PERMISSION_WRITE = 2368;
    protected static final int PICK_IMAGE_REQUEST = 2367;
    protected static final int CAMERA_REQUEST = 2366;
    protected static final int TAKE_FILE_REQUEST = 2365;
    protected static final int ADD_FILE_REQUEST = 2364;
    protected static final int SCAN_REQUEST = 2363;
    protected static final int CREATE_PDF_FROM_SELECT_FILE = 2362;

    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";
    public static final String EXTRA_FILE_EXTENSION = "EXTRA_FILE_EXTENSION";
    public static final String EXTRA_FILE_TYPE = "EXTRA_FILE_TYPE";
    protected static final String EXTRA_PASSWORD = "EXTRA_PASSWORD";
    protected static final String EXTRA_IS_PREVIEW = "EXTRA_IS_PREVIEW";
    protected static final String EXTRA_NEED_SCAN = "EXTRA_NEED_SCAN";
    protected static final String EXTRA_DATA_CREATE_PDF = "EXTRA_DATA_CREATE_PDF";
    protected static final String EXTRA_FROM_FIRST_OPEN = "EXTRA_FROM_FIRST_OPEN";

    protected static final int RESULT_FILE_DELETED = -1111;
    public static final int RESULT_NEED_FINISH = -1112;

    private static final String TAG = "BaseBindingActivity";
    private V mViewModel;
    private T mViewDataBinding;
    protected String mCurrentPhotoPath;

    private SweetAlertDialog mDownloadFromGgDriveDialog;
    protected SweetAlertDialog mLoadFromLocalDialog;

    private InterstitialAd mDoneInterstitialAd;
    private InterstitialAd mMyPdfInterstitialAd;

    protected boolean mIsRequestFullPermission = false;
    protected int mRequestFullPermissionCode = -1000;

    protected boolean mIsNeedSetTheme = true;

    private int orderAll =1, orderBook =1;

    public int getOrderAll() {
        return orderAll;
    }

    public void setOrderAll(int orderAll) {
        this.orderAll = orderAll;
    }

    public int getOrderBook() {
        return orderBook;
    }

    public void setOrderBook(int orderBook) {
        this.orderBook = orderBook;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("duynm", "onNewIntent");
        super.onNewIntent(intent);
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    @Override
    public void onBackPressed() {
        if (isTaskRoot() && !(this instanceof MainActivity)) {
            restartApp(false);
            return;
        }
        super.onBackPressed();
    }

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract V getViewModel();

    @Override
    public void onFragmentAttached() {
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mIsNeedSetTheme) {
            int chosenTheme = DataManager.getInstance(this).getTheme();
            int[] THEME_LIST = {R.style.RedAppTheme, R.style.BlueAppTheme, R.style.JadeAppTheme, R.style.VioletAppTheme, R.style.OrangeAppTheme, R.style.GreenAppTheme, R.style.YellowThemeColor};

            int selectedTheme = THEME_LIST[chosenTheme];

            setTheme(selectedTheme);
        }

        setNoActionBar();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        performDataBinding();

        String appLocaleString = DataManager.getInstance(this).getAppLocale();
        Locale appLocale;

        if (appLocaleString == null) {
            appLocale = getResources().getConfiguration().locale;
        } else {
            appLocale = new Locale(appLocaleString);
        }

        Locale.setDefault(appLocale);
        Configuration config = new Configuration();
        config.locale = appLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void preloadTapFunctionAdsIfInit() {
    }

    protected void preloadDoneAdsIfInit() {
        mDoneInterstitialAd = Admod.getInstance().getInterstitalAds(this, BuildConfig.full_done_id);
    }

    public void preloadMyPdfAdsIfInit() {
        mMyPdfInterstitialAd = Admod.getInstance().getInterstitalAds(this, BuildConfig.full_my_pdf_id);
    }

    public void preloadViewPdfAdsIfInit() {
    }

    public void showTapFunctionAdsBeforeAction(Runnable callback) {

    }

    public void showOnePerTwoTapFunctionAdsBeforeAction(Runnable callback) {
        callback.run();
    }

    public void showDoneAdsBeforeAction(Runnable callback) {
        Admod.getInstance().forceShowInterstitial(this, mDoneInterstitialAd, new AdCallback() {
            @Override
            public void onAdClosed() {
                callback.run();
            }
        });
    }

    public void showMyPdfAdsBeforeAction(Runnable callback) {
        Admod.getInstance().forceShowInterstitial(this, mMyPdfInterstitialAd, new AdCallback() {
            @Override
            public void onAdClosed() {
                callback.run();
            }
        });
    }

    public void showViewPdfAdsBeforeAction(Runnable callback) {
        callback.run();
    }

    public void showBackHomeAdsBeforeAction(Runnable callback) {
        callback.run();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * create view component
     */
    protected abstract void initView();

    /**
     * set on-click listener for view component
     */
    protected abstract void setClick();

    public T getViewDataBinding() {
        return mViewDataBinding;
    }

    private void performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        this.mViewModel = mViewModel == null ? getViewModel() : mViewModel;
        mViewDataBinding.setLifecycleOwner(this);
        mViewDataBinding.setVariable(getBindingVariable(), mViewModel);
        mViewDataBinding.executePendingBindings();
    }

    public boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public void requestReadStoragePermissionsSafely(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            mIsRequestFullPermission = true;
            mRequestFullPermissionCode = requestCode;

            startActivity(intent);
        }
    }

    public void requestFullStoragePermission() {

    }

    public boolean notHaveStoragePermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            return (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && !hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE));
        } else {
            return (!Environment.isExternalStorageManager());
        }
    }

    @SuppressLint("RestrictedApi")
    public void setNoActionBar() {
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setShowHideAnimationEnabled(false);

            actionBar.hide();
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @SuppressLint("RestrictedApi")
    public void setActionBar(String title, boolean isShowBackButton) {
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setShowHideAnimationEnabled(false);

            actionBar.show();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(title);

            actionBar.setHomeButtonEnabled(isShowBackButton);
            actionBar.setDisplayHomeAsUpEnabled(isShowBackButton);
        }
    }

    public void restartApp(boolean isFromSplash) {
        Intent intent;
        if (isFromSplash) {
            intent = new Intent(BaseBindingActivity.this, SplashActivity.class);
        } else {
            intent = new Intent(BaseBindingActivity.this, MainActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        finish();
    }

    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    public int getIntegerByResource(@IntegerRes int integer) {
        return getResources().getInteger(integer);
    }

    /**
     * for set full screen without action bar and navigation bar()
     */
    protected void setActivityFullScreen() {

    }

    protected void setActivityWithActionBar() {

    }

    /**
     * Show popup download from Google drive
     */

    protected void updateFilePathFromGGDrive(Uri uri, String filePath) {
        try {
            if (mDownloadFromGgDriveDialog != null && mDownloadFromGgDriveDialog.isShowing()) {
                mDownloadFromGgDriveDialog.dismiss();
            }
        } catch (Exception e) {
            // donothing
        }
    }

    protected void startDownloadFromGoogleDrive(Uri uri) {
        mDownloadFromGgDriveDialog = DialogFactory.getDialogProgress(this, getString(R.string.downloading_from_gg_drive_text));
        mDownloadFromGgDriveDialog.show();

        AsyncTask.execute(() -> {
            try {
                @SuppressLint("Recycle")
                Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);

                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                String originalName = (returnCursor.getString(nameIndex));
                String size = (Long.toString(returnCursor.getLong(sizeIndex)));

                if (originalName == null) {
                    originalName = getString(R.string.prefix_for_google_drive) + DateTimeUtils.currentTimeToNaming();
                }

                File file = new File(DirectoryUtils.getDefaultStorageLocation(), originalName);
                InputStream inputStream = getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(file);
                int read = 0;
                int maxBufferSize = 1024 * 1024;
                int bytesAvailable = inputStream.available();

                int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                final byte[] buffers = new byte[bufferSize];
                while ((read = inputStream.read(buffers)) != -1) {
                    outputStream.write(buffers, 0, read);
                }
                inputStream.close();
                outputStream.close();

                runOnUiThread(() -> {
                    updateFilePathFromGGDrive(uri, file.getPath());
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    updateFilePathFromGGDrive(uri, null);
                });
            }
        });
    }

    protected void updateFilePathFromGGDriveList(int index, ArrayList<Uri> uriList, String filePath) {
        if (mDownloadFromGgDriveDialog != null) {
            if (index == uriList.size() - 1)
                mDownloadFromGgDriveDialog.dismiss();
        }
    }

    protected void startDownloadFromGoogleDriveList(ArrayList<Uri> uriList) {
        runOnUiThread(() -> {
            mDownloadFromGgDriveDialog = DialogFactory.getDialogProgress(this, getString(R.string.downloading_from_gg_drive_text));
            mDownloadFromGgDriveDialog.show();
        });

        for (int i = 0; i < uriList.size(); i++) {
            int finalIndex = i;
            Uri uri = uriList.get(i);
            if (uri == null) {
                runOnUiThread(() -> {
                    updateFilePathFromGGDriveList(finalIndex, uriList, null);
                });
                return;
            }

            try {
                @SuppressLint("Recycle")
                Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);

                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                String originalName = (returnCursor.getString(nameIndex));
                String size = (Long.toString(returnCursor.getLong(sizeIndex)));

                if (originalName == null) {
                    originalName = getString(R.string.prefix_for_google_drive) + DateTimeUtils.currentTimeToNaming();
                }

                File file = new File(DirectoryUtils.getDefaultStorageLocation(), originalName);
                InputStream inputStream = getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(file);
                int read = 0;
                int maxBufferSize = 1024 * 1024;
                int bytesAvailable = inputStream.available();

                int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                final byte[] buffers = new byte[bufferSize];
                while ((read = inputStream.read(buffers)) != -1) {
                    outputStream.write(buffers, 0, read);
                }
                inputStream.close();
                outputStream.close();

                runOnUiThread(() -> {
                    updateFilePathFromGGDriveList(finalIndex, uriList, file.getPath());
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    updateFilePathFromGGDriveList(finalIndex, uriList, null);
                });
            }
        }
    }

    /**
     * for activity move - all activity moving should put here - ads coverage
     */

    public void gotoActivityWithFlag(int flag) {
        Intent intent;

        switch (flag) {

            case AppConstants.FLAG_SEARCH_PDF:
                intent = new Intent(BaseBindingActivity.this, SearchActivity.class);
                startActivity(intent);
                FirebaseUtils.sendEventFunctionUsed(this, flag, "Search PDF");

                break;

            case AppConstants.FLAG_PROTECT_PDF:
                showOnePerTwoTapFunctionAdsBeforeAction(() -> {

                });

                break;
            case AppConstants.FLAG_UNLOCK_PDF:
                showOnePerTwoTapFunctionAdsBeforeAction(() -> {

                });

                break;
            case AppConstants.FLAG_VIEW_PDF:
                showOnePerTwoTapFunctionAdsBeforeAction(() -> {
                    Intent viewPdfIntent = new Intent(BaseBindingActivity.this, ViewPdfActivity.class);
                    startActivity(viewPdfIntent);
                });

                break;
            case AppConstants.FLAG_IMAGE_TO_PDF_FROM_HOME:

                break;
            case AppConstants.FLAG_IMAGE_TO_PDF:
            default:
                showOnePerTwoTapFunctionAdsBeforeAction(() -> {

                });
        }
    }

    public void gotoProtectPasswordActivity(String filePath) {
        if (PdfUtils.isPDFEncrypted(filePath)) {
            ToastUtils.showMessageShort(this, getString(R.string.protect_pdf_file_is_encrypted_before));
            return;
        }




        FirebaseUtils.sendEventFunctionUsed(this, AppConstants.FLAG_PROTECT_PDF, "Protect Pdf");
    }

    public void gotoUnlockPasswordActivity(String filePath, String password) {



        FirebaseUtils.sendEventFunctionUsed(this, AppConstants.FLAG_UNLOCK_PDF, "Unlock Pdf");
    }

    public void gotoViewByAndroidViewActivity(String filePath, FileUtils.FileType fileType) {
        if (filePath != null) {
            FileUtils.openFile(this, filePath, fileType);
        } else {
            ToastUtils.showMessageShort(this, getString(R.string.can_not_open_file));
        }
    }

    public void gotoPdfFileByAndroidViewActivity(String filePath) {
        gotoViewByAndroidViewActivity(filePath, FileUtils.FileType.type_PDF);
    }

    public void gotoPdfFileViewActivity(String filePath) {
        Intent intent = new Intent(BaseBindingActivity.this, ViewPdfActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        startActivity(intent);

        FirebaseUtils.sendEventFunctionUsed(this, AppConstants.FLAG_VIEW_PDF, "View Pdf");
    }

    public void gotoPdfFilePreviewActivity(String filePath) {
        Intent intent = new Intent(BaseBindingActivity.this, ViewPdfActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        intent.putExtra(EXTRA_IS_PREVIEW, true);
        startActivityForResult(intent, PREVIEW_FILE_REQUEST);

        FirebaseUtils.sendEventFunctionUsed(this, AppConstants.FLAG_VIEW_PDF, "View Pdf");
    }

    /**
     * Request rating
     */

    @SuppressLint("ResourceAsColor")
    public void showRatingUsPopup(final  Runnable acceptRunnable, final Runnable rejectRunnable) {
        RateUtils.showRateDialog(this, new OnCallback() {
            @Override
            public void onMaybeLater() {
                rejectRunnable.run();
            }

            @Override
            public void onSubmit(String review) {
                FirebaseUtils.sendEventFunctionUsed(BaseBindingActivity.this, "Popup rating", review);
                ToastUtils.showMessageShort(BaseBindingActivity.this, getString(R.string.thank_you_for_rate_us));
                DataManager dataManager = DataManager.getInstance(getApplicationContext());
                dataManager.setRatingUsDone();
                rejectRunnable.run();
            }

            @Override
            public void onRate() {
                FirebaseUtils.sendEventFunctionUsed(BaseBindingActivity.this, "Rate 5 stars", "From home");
                gotoRateUsActivity();
                acceptRunnable.run();
            }
        });
    }

    public void gotoRateUsActivity() {
        DataManager dataManager = DataManager.getInstance(this);
        dataManager.setRatingUsDone();

        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
}
