package com.pdfreader.scanner.pdfviewer.ui.viewpdf;

import static com.pdfreader.scanner.pdfviewer.R.id.search_close_btn;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ads.control.Admod;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.pdfreader.scanner.pdfviewer.BuildConfig;
import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.constants.DataConstants;
import com.pdfreader.scanner.pdfviewer.data.DataManager;
import com.pdfreader.scanner.pdfviewer.data.model.DocumentData;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.data.model.ViewPdfOption;
import com.pdfreader.scanner.pdfviewer.databinding.ActivityViewPdfBinding;
import com.pdfreader.scanner.pdfviewer.listener.OnFileItemClickListener;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseBindingActivity;
import com.pdfreader.scanner.pdfviewer.ui.component.ConfirmDialog;
import com.pdfreader.scanner.pdfviewer.ui.component.JumpPageDialog;
import com.pdfreader.scanner.pdfviewer.ui.component.PdfViewOptionDialog;
import com.pdfreader.scanner.pdfviewer.ui.component.RenameFileDialog;
import com.pdfreader.scanner.pdfviewer.ui.component.RenameFileViewDialog;
import com.pdfreader.scanner.pdfviewer.ui.component.ViewSettingDialog;
import com.pdfreader.scanner.pdfviewer.ui.copy.CopyActivity;
import com.pdfreader.scanner.pdfviewer.utils.ColorUtils;
import com.pdfreader.scanner.pdfviewer.utils.CommonUtils;
import com.pdfreader.scanner.pdfviewer.utils.DialogFactory;
import com.pdfreader.scanner.pdfviewer.utils.FirebaseUtils;
import com.pdfreader.scanner.pdfviewer.utils.SnackBarUtils;
import com.pdfreader.scanner.pdfviewer.utils.ToastUtils;
import com.pdfreader.scanner.pdfviewer.utils.adapter.FileListNoAdsAdapter;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.RealPathUtil;
import com.pdfreader.scanner.pdfviewer.utils.pdf.PdfUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.disposables.Disposable;

public class ViewPdfActivity extends BaseBindingActivity<ActivityViewPdfBinding, ViewPdfViewModel> implements ViewPdfNavigator, OnFileItemClickListener {
    private ViewPdfViewModel mViewPdfViewModel;
    private ActivityViewPdfBinding mActivityViewPdfBinding;

    private final int REQUEST_EXTERNAL_PERMISSION_FOR_GET_LOCAL_FILE = 1;
    private final int REQUEST_EXTERNAL_PERMISSION_FOR_OPEN_LOCAL_FILE = 2;
    private final int REQUEST_EXTERNAL_PERMISSION_FOR_FILE_SELECTOR = 3;
    private SweetAlertDialog mRequestPermissionDialog;
    private String mFilePath = null;
    private String mPassword = null;
    private DocumentData mSelectedFile = null;
    private boolean mIsNeedToReview = false;
    private boolean mIsFromOtherScreen = false;
    private boolean mIsFromSplash = false;
    private boolean mIsViewFull = false;
    private SweetAlertDialog mOpeningDialog;
    private List<FileData> mListFileSelector = new ArrayList<>();
    private FileListNoAdsAdapter mFileListSelectorAdapter;
    private boolean mIsBookmarked;
    private String mFileSelectorSearchKey = "";
    private ViewPdfOption mViewOption;
    private String extraFilePath;
    private FileData mFileData;
    private int curentPage = 0, pageNumber = 0;
    private Disposable disposable;
    private PdfViewOptionDialog menuOptioDialog;
    private ViewSettingDialog viewSettingDialog;

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_view_pdf;
    }

    @Override
    public ViewPdfViewModel getViewModel() {
        mViewPdfViewModel = ViewModelProviders.of(this).get(ViewPdfViewModel.class);
        return mViewPdfViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityViewPdfBinding = getViewDataBinding();
        mViewPdfViewModel.setNavigator(this);
//        fragmentManager = getSupportFragmentManager();
        mIsFromOtherScreen = false;

        extraFilePath = getIntent().getStringExtra(EXTRA_FILE_PATH);
        if (extraFilePath != null && extraFilePath.length() > 0 && FileUtils.checkFileExistAndType(extraFilePath, FileUtils.FileType.type_PDF)) {
            mFileData = FileUtils.getFileDataFromFilePath(extraFilePath);
            mIsFromOtherScreen = true;
            mIsFromSplash = getIntent().getBooleanExtra(EXTRA_FROM_FIRST_OPEN, false);

            if (PdfUtils.isPDFEncrypted(extraFilePath)) {
                handleEncryptedFile(extraFilePath, null);
            } else {
                String name = FileUtils.getFileName(extraFilePath);
                mFilePath = extraFilePath;
                mSelectedFile = new DocumentData(name, null, mFilePath);

                mOpeningDialog = DialogFactory.getDialogProgress(this, getString(R.string.loading_text));
                mOpeningDialog.show();
            }

            mIsNeedToReview = getIntent().getBooleanExtra(EXTRA_IS_PREVIEW, false);
        }

        initView();
    }

    @Override
    protected void initView() {
        // mActivityViewPdfBinding.defaultLayout.toolbar.toolbarNameTv.setText(getString(R.string.view_pdf));
//        mActivityViewPdfBinding.defaultLayout.toolbar.toolbarBtnBack.setOnClickListener(view -> onBackPressed());

        mActivityViewPdfBinding.contentLayout.toolbar.toolbarBtnBack.setOnClickListener(view -> onBackPressed());
        mActivityViewPdfBinding.contentLayout.toolbar.renameToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renamePdfFileView();
            }
        });

        mActivityViewPdfBinding.contentLayout.toolbar.toolbarActionFullScreen.setOnClickListener(view -> {
            mIsViewFull = true;
            setForViewFullScreen();

            ToastUtils.showMessageShort(getApplicationContext(), getString(R.string.change_to_full_screen_mode));
        });
        ImageView closeBtn = mActivityViewPdfBinding.defaultLayout.searchEdt.findViewById(search_close_btn);
        if (closeBtn != null) {
            closeBtn.setEnabled(false);
            closeBtn.setImageDrawable(null);
        }

        Admod.getInstance().loadBanner(this, BuildConfig.banner_id);
        preloadDoneAdsIfInit();

        mIsViewFull = false;
        setForViewFullScreen();

        mViewOption = DataManager.getInstance(this).getViewPDFOptions();
        if (mViewOption == null) {
            mViewOption = new ViewPdfOption(DataConstants.VIEW_MODE_DAY, DataConstants.VIEW_ORIENTATION_VERTICAL, DataConstants.VIEW_MODE_SINGLE);
        }


        mActivityViewPdfBinding.contentLayout.optionViewBookmark.setOnClickListener(v -> {
            if (mIsBookmarked) {
                ToastUtils.showMessageShort(getApplicationContext(), getString(R.string.change_to_unbookmarked));
            } else {
                ToastUtils.showMessageShort(getApplicationContext(), getString(R.string.change_to_bookmarked));
            }
            mViewPdfViewModel.revertBookmarked(mFilePath);
        });

        mActivityViewPdfBinding.contentLayout.optionViewJump.setOnClickListener(v -> {
            JumpPageDialog jumpPageDialog = new JumpPageDialog(this, mActivityViewPdfBinding.contentLayout.pdfView.getPageCount(), new JumpPageDialog.SplitRangeListener() {
                @Override
                public void onSubmitRange(int page) {
                    mActivityViewPdfBinding.contentLayout.pdfView.jumpTo(page - 1);
                }

                @Override
                public void onCancel() {

                }
            });
            jumpPageDialog.show();
        });

        mActivityViewPdfBinding.defaultLayout.btnLayoutSelectFile.setOnClickListener((v) -> this.checkPermissionBeforeGetFile());
        setForLayoutView();

        if (!mIsFromOtherScreen) {
            requestForFileSelector();
        }

        setForBookmark();

        mActivityViewPdfBinding.defaultLayout.searchEdt.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null) newText = "";
                mFileSelectorSearchKey = newText.trim();
                updateForSearchFileSelector();

                return false;
            }
        });
        mActivityViewPdfBinding.contentLayout.optionViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowOption(mIsBookmarked);
//                showMoreMenu();
            }
        });

        mActivityViewPdfBinding.contentLayout.optionViewMode.setOnClickListener(v -> {
            // open dialogsetting
            onShowSetting();
        });

        setForViewOrientation(true);
        mActivityViewPdfBinding.contentLayout.optionViewOrientation.setOnClickListener(v -> {
            // do nothing
        });
    }

    public void changeViewMode() {
        if (mViewOption.getViewMode() == DataConstants.VIEW_MODE_DAY) {
            mViewOption.setViewMode(DataConstants.VIEW_MODE_NIGHT);
            ToastUtils.showMessageShort(getApplicationContext(), getString(R.string.change_to_night_mode));
        } else {
            mViewOption.setViewMode(DataConstants.VIEW_MODE_DAY);
            ToastUtils.showMessageShort(getApplicationContext(), getString(R.string.change_to_day_mode));
        }
        setForViewMode(false);
        DataManager.getInstance(this).saveViewPDFOptions(mViewOption);
    }

    public void changeSingleMode() {

    }

    // khoi dong ,
    public void changeOrentation() {
        if (mViewOption.getOrientation() == DataConstants.VIEW_ORIENTATION_VERTICAL) {
            mViewOption.setOrientation(DataConstants.VIEW_ORIENTATION_HORIZONTAL);
            ToastUtils.showMessageShort(getApplicationContext(), getString(R.string.change_to_horizontal_mode));
        } else {
            mViewOption.setOrientation(DataConstants.VIEW_ORIENTATION_VERTICAL);
            ToastUtils.showMessageShort(getApplicationContext(), getString(R.string.change_to_vertical_mode));
        }
        setForViewOrientation(false);
        DataManager.getInstance(this).saveViewPDFOptions(mViewOption);
    }


    @Override
    protected void setClick() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_PERMISSION_FOR_GET_LOCAL_FILE:
            case REQUEST_EXTERNAL_PERMISSION_FOR_OPEN_LOCAL_FILE:
                if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mRequestPermissionDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    mRequestPermissionDialog.setTitleText(getString(R.string.thankyou_text));
                    mRequestPermissionDialog.setContentText(getString(R.string.get_file_now));
                    mRequestPermissionDialog.showCancelButton(false);
                    mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                    mRequestPermissionDialog.setConfirmClickListener(sweetAlertDialog -> {
                        if (requestCode == REQUEST_EXTERNAL_PERMISSION_FOR_GET_LOCAL_FILE) {
                            startChooseFileActivity();
                        } else {
                            openPdfFile();
                        }
                        sweetAlertDialog.dismiss();
                    });
                } else {
                    mRequestPermissionDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    mRequestPermissionDialog.setTitleText(getString(R.string.title_need_permission_fail));
                    mRequestPermissionDialog.setContentText(getString(R.string.couldnt_get_file_now));
                    mRequestPermissionDialog.showCancelButton(false);
                    mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                    mRequestPermissionDialog.setConfirmClickListener(Dialog::dismiss);
                    mRequestPermissionDialog.setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismiss();
                        if (mIsFromOtherScreen) {
                            finish();
                        }
                    });
                }
                break;

            case REQUEST_EXTERNAL_PERMISSION_FOR_FILE_SELECTOR:
                if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestForFileSelector();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsRequestFullPermission) {
            mIsRequestFullPermission = false;

            if (mRequestFullPermissionCode == REQUEST_EXTERNAL_PERMISSION_FOR_GET_LOCAL_FILE || mRequestFullPermissionCode == REQUEST_EXTERNAL_PERMISSION_FOR_OPEN_LOCAL_FILE) {
                if (!notHaveStoragePermission()) {
                    mRequestPermissionDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    mRequestPermissionDialog.setTitleText(getString(R.string.thankyou_text));
                    mRequestPermissionDialog.setContentText(getString(R.string.get_file_now));
                    mRequestPermissionDialog.showCancelButton(false);
                    mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                    mRequestPermissionDialog.setConfirmClickListener(sweetAlertDialog -> {
                        if (mRequestFullPermissionCode == REQUEST_EXTERNAL_PERMISSION_FOR_GET_LOCAL_FILE) {
                            startChooseFileActivity();
                        } else {
                            openPdfFile();

                        }
                        sweetAlertDialog.dismiss();
                    });
                } else {
                    mRequestPermissionDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    mRequestPermissionDialog.setTitleText(getString(R.string.title_need_permission_fail));
                    mRequestPermissionDialog.setContentText(getString(R.string.couldnt_get_file_now));
                    mRequestPermissionDialog.showCancelButton(false);
                    mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                    mRequestPermissionDialog.setConfirmClickListener(Dialog::dismiss);
                }
            } else {
                if (!notHaveStoragePermission()) {
                    requestForFileSelector();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  if(disposable!=null)

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == TAKE_FILE_REQUEST && data != null) {
            if (data == null) return;
            Uri uri = data.getData();
            if (uri == null) return;

            if (RealPathUtil.getInstance().isDriveFile(uri)) {
                startDownloadFromGoogleDrive(uri);
                return;
            }

            //Getting Absolute Path
            String filePath = RealPathUtil.getInstance().getRealPath(this, uri, FileUtils.FileType.type_PDF);
            checkFilePathGet(uri, filePath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void updateFilePathFromGGDrive(Uri uri, String filePath) {
        super.updateFilePathFromGGDrive(uri, filePath);
        checkFilePathGet(uri, filePath);
    }

    private void checkFilePathGet(Uri uri, String filePath) {
        if (filePath != null && filePath.length() > 0 && FileUtils.checkFileExistAndType(filePath, FileUtils.FileType.type_PDF)) {
            if (PdfUtils.isPDFEncrypted(filePath)) {
                handleEncryptedFile(filePath, uri);
                return;
            } else {
                int numberPages = FileUtils.getNumberPages(filePath);
                if (numberPages == 0) {
                    ToastUtils.showMessageLong(this, getString(R.string.view_pdf_file_empty));
                    return;
                }
            }

            mFilePath = filePath;
            String name = FileUtils.getFileName(getApplicationContext(), uri);
            mSelectedFile = new DocumentData(name, uri, mFilePath);

            mIsViewFull = false;
            setForViewFullScreen();

            setForLayoutView();
        } else {
            ToastUtils.showMessageLong(this, getString(R.string.can_not_select_file));
        }
    }

    private void handleEncryptedFile(String filePath, Uri uri) {

    }

    @Override
    public void onBackPressed() {
        if (mIsViewFull) {
            mIsViewFull = false;
            setForViewFullScreen();
        } else {
            if (!mIsFromOtherScreen && mSelectedFile != null) {
                mSelectedFile = null;
                mFilePath = null;
                setForLayoutView();
                return;
            } else if (mIsFromSplash) {
                restartApp(false);
                return;
            }

            super.onBackPressed();
        }
    }

    private void startChooseFileActivity() {
        Uri uri = Uri.parse(Environment.getRootDirectory() + "/");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(uri, getString(R.string.pdf_type));
        try {
            startActivityForResult(Intent.createChooser(intent, String.valueOf(R.string.view_pdf_select_file_title)), TAKE_FILE_REQUEST);
        } catch (android.content.ActivityNotFoundException ex) {
            // TODO show error
        }
    }

    @SuppressLint("SetTextI18n")
    private void setForLayoutView() {
        if (mSelectedFile != null && mFilePath != null) {
            String fileName = mSelectedFile.getDisplayName();
            if (mIsFromOtherScreen && mSelectedFile.getDisplayName().contains(DataConstants.TEMP_FILE_NAME)) {
                fileName = "PDF file";
            }
            mActivityViewPdfBinding.contentLayout.toolbar.toolbarNameTv.setText(fileName);

            mActivityViewPdfBinding.defaultLayout.contentView.setVisibility(View.GONE);
            mActivityViewPdfBinding.contentLayout.contentView.setVisibility(View.VISIBLE);

            checkPermissionBeforeOpenFile();
        } else {
            mActivityViewPdfBinding.contentLayout.toolbar.toolbarNameTv.setText(getString(R.string.view_pdf_nothing_to_clear));

            if (!mIsFromOtherScreen) {
                mActivityViewPdfBinding.defaultLayout.contentView.setVisibility(View.VISIBLE);
            } else {
                mActivityViewPdfBinding.defaultLayout.contentView.setVisibility(View.GONE);
            }
            mActivityViewPdfBinding.contentLayout.contentView.setVisibility(View.GONE);
        }
    }

    private void setForBookmark() {
        mActivityViewPdfBinding.contentLayout.optionViewBookmarkImg.setColorFilter(getIconColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
        mViewPdfViewModel.getIsBookmarkedLiveData().observe(this, this::updateBookmarkState);
        mViewPdfViewModel.startCheckIsBookmarked(mFilePath);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    private void updateBookmarkState(boolean isBookmarked) {
        mIsBookmarked = isBookmarked;

        if (mIsBookmarked) {
            mActivityViewPdfBinding.contentLayout.optionViewBookmarkImg.setColorFilter(getBookmarkedColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            mActivityViewPdfBinding.contentLayout.optionViewBookmarkImg.setColorFilter(getIconColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    private void requestForFileSelector() {
        if (notHaveStoragePermission()) {
            showPermissionIssueArea();
        } else {
            mFileListSelectorAdapter = new FileListNoAdsAdapter(this);
            mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.dataListArea.setLayoutManager(new LinearLayoutManager(this));
            mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.dataListArea.setAdapter(mFileListSelectorAdapter);

            startRequestForFileList(true);
        }
    }

    private int getBookmarkedColor() {
        return ColorUtils.getColorFromResource(this, R.color.red_theme_color);
    }

    private int getIconColor() {
        if (mViewOption.getViewMode() == DataConstants.VIEW_MODE_DAY) {
            return ColorUtils.getColorFromResource(this, R.color.icon_type_day_mode);
        } else {
            return ColorUtils.getColorFromResource(this, R.color.icon_type_night_mode);
        }
    }

    private int getViewPdfContainerColor() {
        if (mViewOption.getViewMode() == DataConstants.VIEW_MODE_DAY) {
            return ColorUtils.getColorFromResource(this, R.color.background_type_day_mode);
        } else {
            return ColorUtils.getColorFromResource(this, R.color.background_type_night_mode);
        }
    }

    private int getViewOptionColor() {
        if (mViewOption.getViewMode() == DataConstants.VIEW_MODE_DAY) {
            return ColorUtils.getColorFromResource(this, R.color.option_view_type_day_mode);
        } else {
            return ColorUtils.getColorFromResource(this, R.color.option_view_type_night_mode);
        }
    }

    private int getViewTextColor() {
        if (mViewOption.getViewMode() == DataConstants.VIEW_MODE_DAY) {
            return ColorUtils.getColorFromResource(this, R.color.text_type_day_mode);
        } else {
            return ColorUtils.getColorFromResource(this, R.color.text_type_night_mode);
        }
    }

    @Override
    public void onClickItem(int position) {
        if (position < mFileListSelectorAdapter.getListData().size() && position >= 0) {
            CommonUtils.hideKeyboard(this);
            mActivityViewPdfBinding.defaultLayout.searchEdt.clearFocus();

            FileData selectedFile = mFileListSelectorAdapter.getListData().get(position);
            checkFilePathGet(selectedFile.getFileUri(), selectedFile.getFilePath());
        }
    }

    @Override
    protected void onDestroy() {
        if (mIsFromOtherScreen && mSelectedFile != null && mSelectedFile.getDisplayName() != null && mSelectedFile.getDisplayName().contains(DataConstants.TEMP_FILE_NAME)) {
            if (!mIsBookmarked) {
                FileUtils.deleteFileOnExist(mSelectedFile.getFilePath());
                mViewPdfViewModel.clearSavedData(mSelectedFile.getFilePath());
            }
        }
        super.onDestroy();
    }

    private void startRequestForFileList(boolean needShowLoading) {
        if (needShowLoading) {
            showLoadingArea();
        }
        mViewPdfViewModel.getListFileSelectorLiveData().observe(this, this::updateListFileSelector);
        mViewPdfViewModel.getFileList(DataConstants.FILE_TYPE_PDF, FileUtils.SORT_BY_DATE_DESC);
    }

    private void updateListFileSelector(List<FileData> fileDataList) {
        if (mListFileSelector == fileDataList) {
            return;
        }

        mListFileSelector = new ArrayList<>();
        mListFileSelector.addAll(fileDataList);

        if (mListFileSelector.size() > 0) {
            if (mFileSelectorSearchKey.length() > 0) {
                updateForSearchFileSelector();
            } else {
                Parcelable oldPosition = null;
                if (mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.dataListArea.getLayoutManager() != null) {
                    oldPosition = mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.dataListArea.getLayoutManager().onSaveInstanceState();
                }
                mFileListSelectorAdapter.setData(mListFileSelector);
                if (oldPosition != null) {
                    mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.dataListArea.getLayoutManager().onRestoreInstanceState(oldPosition);
                }
            }

            showDataArea();
        } else {
            showNoDataArea();
        }
    }

    private void showNoDataArea() {
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.noDataErrorTv.setText(R.string.no_pdf_found);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.dataListArea.setVisibility(View.GONE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.noDataErrorArea.setVisibility(View.VISIBLE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.noPermissionArea.setVisibility(View.GONE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.loadingArea.setVisibility(View.GONE);
    }

    private void showPermissionIssueArea() {
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.noPermissionArea.setOnClickListener(v -> {
            startRequestPermissionForFileSelector();
        });
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.dataListArea.setVisibility(View.GONE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.noDataErrorArea.setVisibility(View.GONE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.noPermissionArea.setVisibility(View.VISIBLE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.loadingArea.setVisibility(View.GONE);
    }

    private void showDataArea() {

        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.dataListArea.setVisibility(View.VISIBLE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.noDataErrorArea.setVisibility(View.GONE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.noPermissionArea.setVisibility(View.GONE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.loadingArea.setVisibility(View.GONE);
    }

    private void showLoadingArea() {
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.dataListArea.setVisibility(View.GONE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.noDataErrorArea.setVisibility(View.GONE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.loadingArea.setVisibility(View.VISIBLE);
        mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.noPermissionArea.setVisibility(View.GONE);
    }

    private void updateForSearchFileSelector() {
        if (mListFileSelector.size() > 0) {
            if (mFileSelectorSearchKey != null && mFileSelectorSearchKey.length() > 0) {
                List<FileData> searchList = new ArrayList<>();
                for (FileData fileData : mListFileSelector) {
                    String fileName = FileUtils.getFileName(fileData.getFilePath());
                    if (fileName.toLowerCase().contains(mFileSelectorSearchKey.toLowerCase())) {
                        searchList.add(fileData);
                    }
                }
                mFileListSelectorAdapter.setData(searchList);
                mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.dataListArea.scrollToPosition(0);

            } else {
                mFileListSelectorAdapter.setData(mListFileSelector);
                mActivityViewPdfBinding.defaultLayout.fileSelectorLayout.dataListArea.scrollToPosition(0);

            }
        }

    }

    private void startRequestPermissionForFileSelector() {
        if (notHaveStoragePermission()) {
            requestReadStoragePermissionsSafely(REQUEST_EXTERNAL_PERMISSION_FOR_FILE_SELECTOR);
        } else {
            requestForFileSelector();
        }
    }

    private void setForViewFullScreen() {
        if (mIsViewFull) {
            mActivityViewPdfBinding.bannerAds.setVisibility(View.GONE);
            mActivityViewPdfBinding.contentLayout.toolbar.layoutToolbar.setVisibility(View.GONE);
            mActivityViewPdfBinding.contentLayout.optionView.setVisibility(View.GONE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            mActivityViewPdfBinding.bannerAds.setVisibility(View.VISIBLE);
            mActivityViewPdfBinding.contentLayout.toolbar.layoutToolbar.setVisibility(View.VISIBLE);
            mActivityViewPdfBinding.contentLayout.optionView.setVisibility(View.VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void checkPermissionBeforeGetFile() {
        if (notHaveStoragePermission()) {
            mRequestPermissionDialog = DialogFactory.getDialogRequestSomething(this, getString(R.string.title_need_permission), getString(R.string.need_permission_to_get_file));
            mRequestPermissionDialog.setConfirmClickListener(sweetAlertDialog -> {
                requestReadStoragePermissionsSafely(REQUEST_EXTERNAL_PERMISSION_FOR_GET_LOCAL_FILE);
            });
            mRequestPermissionDialog.setCancelClickListener(sweetAlertDialog -> {
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText(getString(R.string.title_need_permission_fail));
                sweetAlertDialog.setContentText(getString(R.string.couldnt_get_file_now));
                sweetAlertDialog.setConfirmClickListener(Dialog::dismiss);
                sweetAlertDialog.showCancelButton(false);
                sweetAlertDialog.setConfirmText(getString(R.string.confirm_text));
            });
            mRequestPermissionDialog.show();
        } else {
            startChooseFileActivity();
        }
    }

    private void checkPermissionBeforeOpenFile() {
        if (notHaveStoragePermission()) {
            mRequestPermissionDialog = DialogFactory.getDialogRequestSomething(this, getString(R.string.title_need_permission), getString(R.string.need_permission_to_get_file));
            mRequestPermissionDialog.setConfirmClickListener(sweetAlertDialog -> {
                requestReadStoragePermissionsSafely(REQUEST_EXTERNAL_PERMISSION_FOR_OPEN_LOCAL_FILE);
            });
            mRequestPermissionDialog.setCancelClickListener(sweetAlertDialog -> {
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setTitleText(getString(R.string.title_need_permission_fail));
                sweetAlertDialog.setContentText(getString(R.string.couldnt_get_file_now));
                sweetAlertDialog.setConfirmClickListener(Dialog::dismiss);
                sweetAlertDialog.showCancelButton(false);
                sweetAlertDialog.setConfirmText(getString(R.string.confirm_text));
                sweetAlertDialog.setConfirmClickListener(sweetAlertDialog1 -> {
                    sweetAlertDialog.dismiss();
                    if (mIsFromOtherScreen) {
                        finish();
                    }
                });
            });
            mRequestPermissionDialog.show();
        } else {
            openPdfFile();
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void setForViewMode(boolean isFirstTime) {
//        mActivityViewPdfBinding.contentLayout.toolbar.layoutToolbar.setBackgroundColor(getViewOptionColor());
//        mActivityViewPdfBinding.contentLayout.toolbar.toolbarBtnBack.setColorFilter(getIconColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
//        mActivityViewPdfBinding.contentLayout.toolbar.toolbarActionFullScreen.setColorFilter(getIconColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
//        mActivityViewPdfBinding.contentLayout.toolbar.toolbarActionShare.setColorFilter(getIconColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
//        mActivityViewPdfBinding.contentLayout.toolbar.toolbarActionMore.setColorFilter(getIconColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
//        mActivityViewPdfBinding.contentLayout.toolbar.toolbarNameTv.setTextColor(getViewTextColor());
//        mActivityViewPdfBinding.contentLayout.separator.setBackgroundColor(getViewOptionColor());
//
//        mActivityViewPdfBinding.contentLayout.pdfViewContainer.setBackgroundColor(getViewPdfContainerColor());
//        mActivityViewPdfBinding.contentLayout.pdfView.setBackgroundColor(getViewPdfContainerColor());
//        mActivityViewPdfBinding.contentLayout.optionView.setBackgroundColor(getViewOptionColor());
//
//        mActivityViewPdfBinding.contentLayout.optionViewOrientationImg.setColorFilter(getIconColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
//        mActivityViewPdfBinding.contentLayout.optionViewJumpImg.setColorFilter(getIconColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
//
//        if (mViewOption.getViewMode() == DataConstants.VIEW_MODE_DAY) {
//            mActivityViewPdfBinding.contentLayout.optionViewModeImg.setImageDrawable(getDrawable(R.drawable.ic_view_night_mode));
//            mActivityViewPdfBinding.contentLayout.pdfView.setNightMode(false);
//        } else {
//            mActivityViewPdfBinding.contentLayout.optionViewModeImg.setImageDrawable(getDrawable(R.drawable.ic_view_day_mode));
//            mActivityViewPdfBinding.contentLayout.pdfView.setNightMode(true);
//        }
//
//        if (!mIsBookmarked) {
//            mActivityViewPdfBinding.contentLayout.optionViewBookmarkImg.setColorFilter(getIconColor(), android.graphics.PorterDuff.Mode.MULTIPLY);
//        }
//        if (mViewOption.getOrientation() == DataConstants.VIEW_ORIENTATION_HORIZONTAL) {
//            mActivityViewPdfBinding.contentLayout.optionViewOrientationImg.setImageDrawable(getDrawable(R.drawable.ic_view_horizontal));
//        } else {
//            mActivityViewPdfBinding.contentLayout.optionViewOrientationImg.setImageDrawable(getDrawable(R.drawable.ic_view_vertical));
//        }

        if (!isFirstTime) {
            curentPage = mActivityViewPdfBinding.contentLayout.pdfView.getCurrentPage();
            pageNumber = mActivityViewPdfBinding.contentLayout.pdfView.getPageCount();
            openPdfFileForChangeOrientation(curentPage);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setForViewOrientation(boolean isFirstTime) {

        if (mViewOption.getOrientation() == DataConstants.VIEW_ORIENTATION_HORIZONTAL) {
            mActivityViewPdfBinding.contentLayout.optionViewOrientationImg.setImageDrawable(getDrawable(R.drawable.ic_view_horizontal));
        } else {
            mActivityViewPdfBinding.contentLayout.optionViewOrientationImg.setImageDrawable(getDrawable(R.drawable.ic_view_vertical));
        }

        if (!isFirstTime) {
            curentPage = mActivityViewPdfBinding.contentLayout.pdfView.getCurrentPage();
            pageNumber = mActivityViewPdfBinding.contentLayout.pdfView.getPageCount();
            openPdfFileForChangeOrientation(curentPage);
        }
    }


    private void openPdfFile() {
        setForBookmark();
        mActivityViewPdfBinding.contentLayout.pdfView.fromFile(new File(mFilePath))
                .enableSwipe(true)
                .swipeHorizontal(mViewOption.getOrientation() == DataConstants.VIEW_ORIENTATION_HORIZONTAL)
                .onError(t -> errorOpenPdfFile())
                .onLoad(nbPages -> {
                    pageNumber = nbPages;
                    mActivityViewPdfBinding.contentLayout.numpage.setText("" + nbPages);
//                    pageNumber =  mActivityViewPdfBinding.contentLayout.pdfView.getPageCount();

                    try {
                        if (mOpeningDialog != null) mOpeningDialog.dismiss();
                        mViewPdfViewModel.saveRecent(mFilePath, getString(R.string.view_pdf));
                    } catch (Exception ignored) {
                    }
                })
                .onPageScroll(new OnPageScrollListener() {
                    @Override
                    public void onPageScrolled(int page, float positionOffset) {
                        curentPage = page;
                        mActivityViewPdfBinding.contentLayout.curpage.setText("" + (page + 1));
                    }
                })
                .enableDoubletap(true)
                .spacing(10)
                .defaultPage(0)
                .enableAnnotationRendering(false)
                .password(PdfUtils.isPDFEncrypted(mFilePath) ? mPassword : null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .fitEachPage(true)
                .pageFitPolicy(FitPolicy.WIDTH)
                .nightMode(mViewOption.getViewMode() == DataConstants.VIEW_MODE_NIGHT)
                .load();


    }

    private void openPdfFileForChangeOrientation(int currentPage) {
        mActivityViewPdfBinding.contentLayout.pdfView.fromFile(new File(mFilePath))
                .enableSwipe(true)
                .swipeHorizontal(mViewOption.getOrientation() == DataConstants.VIEW_ORIENTATION_HORIZONTAL)
                .onError(t -> errorOpenPdfFile())
                .enableDoubletap(true)
                .onRender(nbPages -> {

                    mActivityViewPdfBinding.contentLayout.numpage.setText("" + nbPages);
                    mActivityViewPdfBinding.contentLayout.pdfView.jumpTo(currentPage);
                    mActivityViewPdfBinding.contentLayout.curpage.setText(""+currentPage);
                })
                .onPageScroll(new OnPageScrollListener() {
                    @Override
                    public void onPageScrolled(int page, float positionOffset) {
                        mActivityViewPdfBinding.contentLayout.curpage.setText("" + (page+1));
                    }
                })
                .spacing(10)
                .defaultPage(0)
                .enableAnnotationRendering(false)
                .password(PdfUtils.isPDFEncrypted(mFilePath) ? mPassword : null)
                .scrollHandle(null)
                .enableAntialiasing(true)
                .fitEachPage(true)
                .pageFitPolicy(FitPolicy.WIDTH)
                .nightMode(mViewOption.getViewMode() == DataConstants.VIEW_MODE_NIGHT)
                .load();

    }

    private void errorOpenPdfFile() {

        if (mOpeningDialog != null) mOpeningDialog.dismiss();

        if (mIsFromOtherScreen) {
            SweetAlertDialog errorNoticeDialog = DialogFactory.getDialogError(this, getString(R.string.view_pdf_can_not_open_file_notice));
            errorNoticeDialog.setConfirmText(getString(R.string.close_text));
            errorNoticeDialog.setConfirmClickListener(sweetAlertDialog -> {
                sweetAlertDialog.dismiss();
                finish();
            });
            errorNoticeDialog.setCanceledOnTouchOutside(false);
            errorNoticeDialog.setOnDismissListener(dialogInterface -> finish());
            errorNoticeDialog.show();
        } else {
            ToastUtils.showMessageLong(this, getString(R.string.view_pdf_can_not_open_file));
            mFilePath = null;
            mSelectedFile = null;
            setForLayoutView();
        }
    }

    private void printPdfFile() {
        if (PdfUtils.isPDFEncrypted(mFilePath)) {
            SnackBarUtils.getSnackbar(this, getString(R.string.view_pdf_can_not_print_protected_file)).show();
            return;
        }
        FileUtils.printFile(this, new File(mFilePath));
    }

    private void sharePdfFile() {
        FileUtils.shareFile(this, new File(mFilePath));
    }

    private void uploadPdfFile() {
        FileUtils.uploadFile(this, new File(mFilePath));
    }

    private void deleteFileAndExit() {
        ConfirmDialog confirmDialog = new ConfirmDialog(this, getString(R.string.confirm_delete_file_title), getString(R.string.confirm_delete_file_message), new ConfirmDialog.ConfirmListener() {
            @Override
            public void onSubmit() {
                if (!notHaveStoragePermission()) {
                    FileUtils.deleteFileOnExist(mFilePath);
                    mViewPdfViewModel.clearSavedData(mFilePath);

                    SnackBarUtils.getSnackbar(ViewPdfActivity.this, getString(R.string.delete_success_text)).show();
                    hideMenuOptionDialog();

                    setResult(RESULT_FILE_DELETED);
                    finish();
                }
            }

            @Override
            public void onCancel() {

            }
        });
        confirmDialog.show();
    }

    private void onShowOption(boolean isBookmarked) {

        if (PdfUtils.isPDFEncrypted(extraFilePath)) {
            hideMenuOptionDialog();

            CommonUtils.hideKeyboard(this);
        } else {
            hideMenuOptionDialog();
            menuOptioDialog = new PdfViewOptionDialog(isBookmarked, mFileData.getDisplayName(), mFileData.getTimeAdded(), new PdfViewOptionDialog.FileViewOptionListener() {

                @Override
                public void shareFile() {
                    FileUtils.shareFile(ViewPdfActivity.this, new File(extraFilePath));
                }

                @Override
                public void copyFile() {
                    // gui file path den activity moi va lua chon
                    Intent intent = new Intent(ViewPdfActivity.this, CopyActivity.class);
                    intent.putExtra("filePath", extraFilePath);
                    startActivity(intent);

                }

                @Override
                public void mergeFile() {

                }


                @Override
                public void optionBookmark() {
                    mViewPdfViewModel.revertBookmarked(mFilePath);
                }


                @Override
                public void renameFile() {
                    renamePdfFile();
                }

                @Override
                public void saveDrive() {
                    // FileUtils.uploadFile(mActivity, new File(mListFile.get(position).getFilePath()));
                    uploadPdfFile();
                }

                @Override
                public void printFile() {
                    printPdfFile();
                }

                @Override
                public void deleteFile() {
                    deleteFileAndExit();
                }
            });
            menuOptioDialog.show(getSupportFragmentManager(), menuOptioDialog.getTag());
            CommonUtils.hideKeyboard(this);
        }
    }

    private void hideMenuOptionDialog() {
        if (menuOptioDialog != null && menuOptioDialog.isVisible()) {
            menuOptioDialog.dismiss();
        }
    }

    private void renamePdfFile() {
        FirebaseUtils.sendEventFunctionUsed(this, "Rename file", "From library");

        FileData fileData = mFileData;
        String displayName;
        try {
            displayName = fileData.getDisplayName().substring(0, fileData.getDisplayName().lastIndexOf("."));
        } catch (Exception e) {
            return;
        }

        RenameFileDialog renameFileDialog = new RenameFileDialog(this, displayName, new RenameFileDialog.RenameFileListener() {
            @Override
            public void onSubmitName(String name) {
                String newName = name + ".pdf";
                int result = FileUtils.renameFile(fileData, newName);

                if (result == -2 || result == 0) {

                    ToastUtils.showMessageShort(ViewPdfActivity.this, getString(R.string.can_not_edit_video_name));
                } else if (result == -1) {
                    SnackBarUtils.getSnackbar(ViewPdfActivity.this, getString(R.string.duplicate_video_name) + ": " + name).show();
                } else {
                    SnackBarUtils.getSnackbar(ViewPdfActivity.this, getString(R.string.rename_file_success)).show();
                    String oldFilePath = fileData.getFilePath();

                    fileData.setFilePath(fileData.getFilePath().replace(fileData.getDisplayName(), newName));
                    fileData.setDisplayName(newName);
                    extraFilePath = fileData.getFilePath();
                    mActivityViewPdfBinding.contentLayout.toolbar.toolbarNameTv.setText(fileData.getDisplayName());
                    mViewPdfViewModel.updateSavedData(oldFilePath, fileData.getFilePath());
                    //updateSavedData(oldFilePath, fileData.getFilePath());
                }
            }

            @Override
            public void onCancel() {

            }
        });

        renameFileDialog.show();
    }

    private void renamePdfFileView() {
        FirebaseUtils.sendEventFunctionUsed(this, "Rename file", "From library");

        FileData fileData = mFileData;
//        String displayName;
//        try {
//            displayName = fileData.getDisplayName().substring(0, fileData.getDisplayName().lastIndexOf("."));
//        } catch (Exception e) {
//            return;
//        }

        RenameFileViewDialog renameFileViewDialog = new RenameFileViewDialog(this, fileData, new RenameFileViewDialog.RenameFileViewListener() {
            @Override
            public void onSubmitName(String name) {
                String newName = name + ".pdf";
                int result = FileUtils.renameFile(fileData, newName);

                if (result == -2 || result == 0) {

                    ToastUtils.showMessageShort(ViewPdfActivity.this, getString(R.string.can_not_edit_video_name));
                } else if (result == -1) {
                    SnackBarUtils.getSnackbar(ViewPdfActivity.this, getString(R.string.duplicate_video_name) + ": " + name).show();
                } else {
                    SnackBarUtils.getSnackbar(ViewPdfActivity.this, getString(R.string.rename_file_success)).show();
                    String oldFilePath = fileData.getFilePath();

                    fileData.setFilePath(fileData.getFilePath().replace(fileData.getDisplayName(), newName));
                    fileData.setDisplayName(newName);
                    extraFilePath = fileData.getFilePath();
                    mActivityViewPdfBinding.contentLayout.toolbar.toolbarNameTv.setText(fileData.getDisplayName());
                    mViewPdfViewModel.updateSavedData(oldFilePath, fileData.getFilePath());

                }
            }


        });

        renameFileViewDialog.show();
    }

    private void onShowSetting() {

        if (PdfUtils.isPDFEncrypted(extraFilePath)) {
            hideSettingDialog();

            CommonUtils.hideKeyboard(this);
        } else {
            hideSettingDialog();
            viewSettingDialog = new ViewSettingDialog(mViewOption, new ViewSettingDialog.ViewSettingListener() {

                @Override
                public void clickViewMode() {
                    Toast.makeText(getApplicationContext(), mViewOption.getmSingle() == 0 ? "single" : "facing", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void clickScolling() {
                    setForViewOrientation(false);
                }

                @Override
                public void clickDrak() {
                    setForViewMode(false);
                }

            });
            viewSettingDialog.show(getSupportFragmentManager(), viewSettingDialog.getTag());
            CommonUtils.hideKeyboard(this);
        }
    }

    private void hideSettingDialog() {
        if (viewSettingDialog != null && viewSettingDialog.isVisible()) {
            viewSettingDialog.dismiss();
        }
    }
}
