package com.pdfreader.scanner.pdfviewer.ui.bookmark;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.BookmarkData;
import com.pdfreader.scanner.pdfviewer.databinding.FragmentBookmarkBinding;
import com.pdfreader.scanner.pdfviewer.listener.OnFileItemWithOptionClickListener;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseFragment;
import com.pdfreader.scanner.pdfviewer.ui.component.ConfirmDialog;

import com.pdfreader.scanner.pdfviewer.ui.component.PdfOptionDialog;
import com.pdfreader.scanner.pdfviewer.ui.component.RenameFileDialog;
import com.pdfreader.scanner.pdfviewer.utils.CommonUtils;
import com.pdfreader.scanner.pdfviewer.utils.DialogFactory;
import com.pdfreader.scanner.pdfviewer.utils.FirebaseUtils;
import com.pdfreader.scanner.pdfviewer.utils.SnackBarUtils;
import com.pdfreader.scanner.pdfviewer.utils.ToastUtils;
import com.pdfreader.scanner.pdfviewer.utils.adapter.SaveListNoAdsAdapter;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;
import com.pdfreader.scanner.pdfviewer.utils.pdf.PdfUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BookmarkFragment extends BaseFragment<FragmentBookmarkBinding, BookmarkViewModel> implements BookmarkNavigator, OnFileItemWithOptionClickListener {

    private BookmarkViewModel mBookmarkViewModel;
    private FragmentBookmarkBinding mFragmentBookmarkBinding;
    private boolean mIsLoading;
    private List<BookmarkData> mListFile = new ArrayList<>();
    private SaveListNoAdsAdapter mFileListAdapter;

    private SweetAlertDialog mRequestPermissionDialog;
    private final int REQUEST_EXTERNAL_PERMISSION_FOR_LOAD_FILE_CODE = 1;

    private PdfOptionDialog pdfOptionDialog;

    private boolean mIsCreatedView = false;

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bookmark;
    }

    @Override
    public void reloadEasyChangeData() {

    }

    public void reloadData(boolean isForceReload) {
        if (mActivity != null && mActivity.notHaveStoragePermission()) {
            mFragmentBookmarkBinding.pullToRefresh.setRefreshing(false);

            showPermissionIssueArea();
            mIsLoading = false;
            return;
        }

        if (mIsLoading) return;

        mIsLoading = true;

        if (mListFile == null || mListFile.size() == 0 || isForceReload) {
            if (mFragmentBookmarkBinding != null && mIsCreatedView) {
                showLoadingArea();
            }
        }

        mBookmarkViewModel.getFileList();
    }

    @Override
    public BookmarkViewModel getViewModel() {
        mBookmarkViewModel = ViewModelProviders.of(this).get(BookmarkViewModel.class);
        return mBookmarkViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBookmarkViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentBookmarkBinding = getViewDataBinding();

        setForClick();
        initView();
        setForLiveData();
        mIsCreatedView = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_PERMISSION_FOR_LOAD_FILE_CODE:
                if ((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mRequestPermissionDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    mRequestPermissionDialog.setTitleText(getString(R.string.thankyou_text));
                    mRequestPermissionDialog.setContentText(getString(R.string.get_file_now));
                    mRequestPermissionDialog.showCancelButton(false);
                    mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                    mRequestPermissionDialog.setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismiss();
                        reloadData(true);
                    });
                } else {
                    mRequestPermissionDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    mRequestPermissionDialog.setTitleText(getString(R.string.title_need_permission_fail));
                    mRequestPermissionDialog.setContentText(getString(R.string.couldnt_get_file_now));
                    mRequestPermissionDialog.showCancelButton(false);
                    mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                    mRequestPermissionDialog.setConfirmClickListener(Dialog::dismiss);
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        if (mIsRequestFullPermission) {
            mIsRequestFullPermission = false;

            if (!mActivity.notHaveStoragePermission()) {
                mRequestPermissionDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                mRequestPermissionDialog.setTitleText(getString(R.string.thankyou_text));
                mRequestPermissionDialog.setContentText(getString(R.string.get_file_now));
                mRequestPermissionDialog.showCancelButton(false);
                mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                mRequestPermissionDialog.setConfirmClickListener(Dialog::dismiss);
            } else {
                mRequestPermissionDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                mRequestPermissionDialog.setTitleText(getString(R.string.title_need_permission_fail));
                mRequestPermissionDialog.setContentText(getString(R.string.couldnt_get_file_now));
                mRequestPermissionDialog.showCancelButton(false);
                mRequestPermissionDialog.setConfirmText(getString(R.string.confirm_text));
                mRequestPermissionDialog.setConfirmClickListener(Dialog::dismiss);
            }
        }

        reloadData(false);
        super.onResume();
    }

    public static BookmarkFragment newInstance() {
        BookmarkFragment bookmarkFragment = new BookmarkFragment();

        Bundle args = new Bundle();
        bookmarkFragment.setArguments(args);
        bookmarkFragment.setRetainInstance(true);

        return bookmarkFragment;
    }

    private void initView() {
        mFragmentBookmarkBinding.pullToRefresh.setOnRefreshListener(() -> {
            reloadData(false);
        });

        mFileListAdapter = new SaveListNoAdsAdapter(this);
        mFragmentBookmarkBinding.dataListArea.setLayoutManager(new LinearLayoutManager(mActivity));
        mFragmentBookmarkBinding.dataListArea.setAdapter(mFileListAdapter);
    }

    private void setForClick() {

    }

    private void setForLiveData() {
        mBookmarkViewModel.getListFileLiveData().observe(getViewLifecycleOwner(), this::updateData);
    }

    private void updateData(List<BookmarkData> bookmarkDataList) {
        if (bookmarkDataList.size() > 0) {
            if (bookmarkDataList.equals(mListFile)) {
                mIsLoading = false;
                mFragmentBookmarkBinding.pullToRefresh.setRefreshing(false);

                return;
            }

            mListFile = new ArrayList<>();
            mListFile.addAll(bookmarkDataList);

            Parcelable oldPosition = null;
            if (mFragmentBookmarkBinding.dataListArea.getLayoutManager() != null) {
                oldPosition = mFragmentBookmarkBinding.dataListArea.getLayoutManager().onSaveInstanceState();
            }
            mFileListAdapter.setData(mListFile);
            if (oldPosition != null) {
                mFragmentBookmarkBinding.dataListArea.getLayoutManager().onRestoreInstanceState(oldPosition);
            }

            showDataArea();
        } else {
            showNoDataArea();
        }

        mIsLoading = false;
        mFragmentBookmarkBinding.pullToRefresh.setRefreshing(false);
    }

    private void startRequestPermission() {
        if (mActivity != null && mActivity.notHaveStoragePermission()) {
            mRequestPermissionDialog = DialogFactory.getDialogRequestSomething(mActivity, getString(R.string.title_need_permission), getString(R.string.need_permission_to_get_file));
            mRequestPermissionDialog.setConfirmClickListener(sweetAlertDialog -> {
                requestReadStoragePermissionsSafely(REQUEST_EXTERNAL_PERMISSION_FOR_LOAD_FILE_CODE);
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
            reloadEasyChangeData();
        }
    }

    private void showNoDataArea() {
        if (mFragmentBookmarkBinding != null) {
            mFragmentBookmarkBinding.noDataErrorTv.setText(R.string.no_pdf_found);

            mFragmentBookmarkBinding.dataListArea.setVisibility(View.GONE);
            mFragmentBookmarkBinding.noDataErrorArea.setVisibility(View.VISIBLE);
            mFragmentBookmarkBinding.noPermissionArea.setVisibility(View.GONE);
            mFragmentBookmarkBinding.loadingArea.setVisibility(View.GONE);
        }
    }

    private void showPermissionIssueArea() {
        if (mFragmentBookmarkBinding != null) {
            mFragmentBookmarkBinding.noPermissionArea.setOnClickListener(v -> {
                startRequestPermission();
            });
            mFragmentBookmarkBinding.dataListArea.setVisibility(View.GONE);
            mFragmentBookmarkBinding.noDataErrorArea.setVisibility(View.GONE);
            mFragmentBookmarkBinding.noPermissionArea.setVisibility(View.VISIBLE);
            mFragmentBookmarkBinding.loadingArea.setVisibility(View.GONE);
        }
    }

    private void showDataArea() {
        if (mFragmentBookmarkBinding != null) {
            mFragmentBookmarkBinding.dataListArea.setVisibility(View.VISIBLE);
            mFragmentBookmarkBinding.noDataErrorArea.setVisibility(View.GONE);
            mFragmentBookmarkBinding.noPermissionArea.setVisibility(View.GONE);
            mFragmentBookmarkBinding.loadingArea.setVisibility(View.GONE);
        }
    }

    private void showLoadingArea() {
        if (mFragmentBookmarkBinding != null) {
            mFragmentBookmarkBinding.dataListArea.setVisibility(View.GONE);
            mFragmentBookmarkBinding.noDataErrorArea.setVisibility(View.GONE);
            mFragmentBookmarkBinding.loadingArea.setVisibility(View.VISIBLE);
            mFragmentBookmarkBinding.noPermissionArea.setVisibility(View.GONE);
        }
    }

    public boolean isCreatedView() {
        return mIsCreatedView;
    }

    @Override
    public void onClickItem(int position) {
        String filePath = mListFile.get(position).getFilePath();
        if (!FileUtils.checkFileExist(filePath)) {
            ToastUtils.showMessageShort(mActivity, getString(R.string.file_not_found));
            mBookmarkViewModel.clearSavedData(filePath);
            if (position >= 0 && position < mListFile.size()) {
                mListFile.remove(position);
            }

            mFileListAdapter.clearData(position);
            if (mListFile.size() == 0) {
                updateData(new ArrayList<>());
            }

            return;
        }

        if (mActivity != null) {
            mActivity.showMyPdfAdsBeforeAction(() -> mActivity.gotoPdfFileViewActivity(filePath));
        }
    }

    @Override
    public void onMainFunctionItem(int position) {
        if (position >= 0 && position < mListFile.size()) {
            mBookmarkViewModel.clearBookmarked(mListFile.get(position).getFilePath());
            mListFile.remove(position);

            mFileListAdapter.clearData(position);
            if (mListFile.size() == 0) {
                showNoDataArea();
            }
            SnackBarUtils.getSnackbar(mActivity, mActivity.getString(R.string.bookmark_remove_success)).show();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onOptionItem(int position) {
        BookmarkData bookmarkData = mListFile.get(position);

        mBookmarkViewModel.startCheckIsFileBookmarked(bookmarkData.getFilePath(), position, this::onShowOption);
    }

    @Override
    public void onBookMarkItem(int posision, boolean add) {
//        optionRemoveBookmarkPdf(posision);
    }

    private void onShowOption(int position, boolean isBookmarked) {
        BookmarkData bookmarkData = mListFile.get(position);
        if (PdfUtils.isPDFEncrypted(bookmarkData.getFilePath())) {
            hideOptionDialog();

            CommonUtils.hideKeyboard(mActivity);
        } else {
            hideOptionDialog();
            pdfOptionDialog = new PdfOptionDialog(isBookmarked, bookmarkData.getDisplayName(), bookmarkData.getTimeAdded(), position, new PdfOptionDialog.FileOptionListener() {
                @Override
                public void openFile(int position) {
                    FirebaseUtils.sendEventFunctionUsed(mActivity, "Open file", "From bookmark");
                    openPdfFile(position);
                }

                @Override
                public void shareFile(int position) {
                    onMainFunctionItem(position);
                }

                @Override
                public void copyFile(int position) {

                }

                @Override
                public void mergeFile(int position) {

                }




                @Override
                public void optionBookmark(int position, boolean isAdd) {
//                    optionRemoveBookmarkPdf(position);
                }


                @Override
                public void renameFile(int position) {
                    renamePdfFile(position);
                }

                @Override
                public void saveDrive(int position) {
                    FileUtils.uploadFile(mActivity, new File(mListFile.get(position).getFilePath()));
                }

                @Override
                public void printFile(int position) {
                    printPdfFile(mListFile.get(position).getFilePath());
                }

                @Override
                public void deleteFile(int position) {
                    deletePdfFile(position);
                }
            });
            pdfOptionDialog.show(getChildFragmentManager(), pdfOptionDialog.getTag());
            CommonUtils.hideKeyboard(mActivity);
        }
    }

    protected void optionRemoveBookmarkPdf(int position) {
        FirebaseUtils.sendEventFunctionUsed(mActivity, "Remove bookmark file", "From bookmark");

        if (position >= 0 && position < mListFile.size()) {
            mBookmarkViewModel.clearBookmarked(mListFile.get(position).getFilePath());
            mListFile.remove(position);

            mFileListAdapter.clearData(position);
            if (mListFile.size() <= 1) {
                showNoDataArea();
            }
            hideOptionDialog();
            SnackBarUtils.getSnackbar(mActivity, mActivity.getString(R.string.bookmark_remove_success)).show();
        }
    }

    private void hideOptionDialog() {

        if (pdfOptionDialog != null && pdfOptionDialog.isVisible()) {
            pdfOptionDialog.dismiss();
        }
    }

    private void openPdfFile(int position) {
        onClickItem(position);
    }

    private void renamePdfFile(int position) {
        FirebaseUtils.sendEventFunctionUsed(mActivity, "Rename file", "From bookmark");

        BookmarkData bookmarkData = mListFile.get(position);
        String displayName;
        try {
            displayName = bookmarkData.getDisplayName().substring(0, bookmarkData.getDisplayName().lastIndexOf("."));
        } catch (Exception e) {
            return;
        }

        RenameFileDialog renameFileDialog = new RenameFileDialog(mActivity, displayName, new RenameFileDialog.RenameFileListener() {
            @Override
            public void onSubmitName(String name) {
                String newName = name + ".pdf";
                int result = FileUtils.renameFile(bookmarkData, newName);

                if (result == -2 || result == 0) {
                    ToastUtils.showMessageShort(mActivity, getString(R.string.can_not_edit_video_name));
                } else if (result == -1) {
                    SnackBarUtils.getSnackbar(mActivity, getString(R.string.duplicate_video_name) + ": " + name).show();
                } else {
                    SnackBarUtils.getSnackbar(mActivity, getString(R.string.rename_file_success)).show();
                    String oldFilePath = bookmarkData.getFilePath();

                    bookmarkData.setFilePath(bookmarkData.getFilePath().replace(bookmarkData.getDisplayName(), newName));
                    bookmarkData.setDisplayName(newName);
                    mListFile.set(position, bookmarkData);
                    mFileListAdapter.updateData(position, bookmarkData);

                    mBookmarkViewModel.updateSavedData(oldFilePath, bookmarkData.getFilePath());
                }
            }

            @Override
            public void onCancel() {

            }
        });

        renameFileDialog.show();
    }

    private void deletePdfFile(int position) {
        FirebaseUtils.sendEventFunctionUsed(mActivity, "Delete file", "From bookmark");
        BookmarkData bookmarkData = mListFile.get(position);

        ConfirmDialog confirmDialog = new ConfirmDialog(mActivity, mActivity.getString(R.string.confirm_delete_file_title), mActivity.getString(R.string.confirm_delete_file_message), new ConfirmDialog.ConfirmListener() {
            @Override
            public void onSubmit() {
                if (!mActivity.notHaveStoragePermission()) {
                    FileUtils.deleteFileOnExist(bookmarkData.getFilePath());
                    mBookmarkViewModel.clearSavedData(bookmarkData.getFilePath());

                    if (position >= 0 && position < mListFile.size()) {
                        mListFile.remove(position);
                    }

                    mFileListAdapter.clearData(position);
                    if (mListFile.size() == 0) {
                        showNoDataArea();
                    }
                    SnackBarUtils.getSnackbar(mActivity, mActivity.getString(R.string.delete_success_text)).show();
                    hideOptionDialog();
                }
            }

            @Override
            public void onCancel() {

            }
        });
        confirmDialog.show();
    }

}
