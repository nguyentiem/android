package com.pdfreader.scanner.pdfviewer.ui.lib;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.databinding.FragmentLibBinding;
import com.pdfreader.scanner.pdfviewer.listener.OnFileItemWithOptionClickListener;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseFragment;
import com.pdfreader.scanner.pdfviewer.ui.component.ConfirmDialog;

import com.pdfreader.scanner.pdfviewer.ui.component.PdfOptionDialog;
import com.pdfreader.scanner.pdfviewer.ui.component.RenameFileDialog;
import com.pdfreader.scanner.pdfviewer.ui.component.SettingSortDialog;
import com.pdfreader.scanner.pdfviewer.ui.main.MainActivity;
import com.pdfreader.scanner.pdfviewer.utils.CommonUtils;
import com.pdfreader.scanner.pdfviewer.utils.DialogFactory;
import com.pdfreader.scanner.pdfviewer.utils.FirebaseUtils;
import com.pdfreader.scanner.pdfviewer.utils.SnackBarUtils;
import com.pdfreader.scanner.pdfviewer.utils.ToastUtils;
import com.pdfreader.scanner.pdfviewer.utils.adapter.FileListNoAdsWithOptionAdapter;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.RealPathUtil;
import com.pdfreader.scanner.pdfviewer.utils.pdf.PdfUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LibFragment extends BaseFragment<FragmentLibBinding, LibViewModel> implements LibNavigator, OnFileItemWithOptionClickListener, SettingSortDialog.SettingSortSubmit {

    private static final String TAG = "libFragment";
    private LibViewModel mLibViewModel;
    private FragmentLibBinding mFragmentLibBinding;
    private int mCurrentSortBy = FileUtils.SORT_BY_DATE_DESC;
    private boolean mIsLoading;
    private List<FileData> mListFile = new ArrayList<>();
    private FileListNoAdsWithOptionAdapter mFileListAdapter;

    private SweetAlertDialog mRequestPermissionDialog;
    private final int REQUEST_EXTERNAL_PERMISSION_FOR_LOAD_FILE_CODE = 1;

    private PdfOptionDialog pdfOptionDialog;


    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_lib;
    }

    @Override
    public void reloadEasyChangeData() {

    }

    public void reloadData(boolean isForceReload) {
        if (mActivity != null && mActivity.notHaveStoragePermission()) {
            mFragmentLibBinding.pullToRefresh.setRefreshing(false);

            showPermissionIssueArea();
            mIsLoading = false;
            return;
        }

        if (mIsLoading) return;

        mIsLoading = true;

        if (mListFile == null || mListFile.size() == 0 || isForceReload) {

            showLoadingArea();
        }

        mLibViewModel.getFileList(mCurrentSortBy);
    }

    @Override
    public LibViewModel getViewModel() {
        mLibViewModel = ViewModelProviders.of(this).get(LibViewModel.class);
        return mLibViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mLibViewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentLibBinding = getViewDataBinding();

        setForClick();
        initView();
        setForLiveData();

        reloadData(true);
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


    public static LibFragment newInstance() {
        LibFragment libFragment = new LibFragment();

        Bundle args = new Bundle();
        libFragment.setArguments(args);
        libFragment.setRetainInstance(true);

        return libFragment;
    }

    private void initView() {
        mFragmentLibBinding.pullToRefresh.setOnRefreshListener(() -> {
            reloadData(false);
        });

        mFileListAdapter = new FileListNoAdsWithOptionAdapter(this);
        mFragmentLibBinding.dataListArea.setLayoutManager(new LinearLayoutManager(mActivity));
        Log.d(TAG, "initView: ");
        mFragmentLibBinding.dataListArea.setAdapter(mFileListAdapter);
        Log.d(TAG, "initView: 2");
    }

    private void setForClick() {
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).setClickSortItem(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSortPopup();
                }
            });
        }
    }

    private void showSortPopup() {
        if (mIsLoading) {
            ToastUtils.showMessageShort(mActivity, getString(R.string.sort_not_available));
            return;
        }

        SettingSortDialog dialog = new SettingSortDialog(mActivity, this, mCurrentSortBy);
        dialog.show();
    }

    @Override
    public void updateNewSort(int newSort) {
        mCurrentSortBy = newSort;
        reloadData(true);
    }

    private void setForLiveData() {
        mLibViewModel.getListFileLiveData().observe(getViewLifecycleOwner(), this::updateData);
    }

    private void updateData(List<FileData> fileDataList) {
        if (fileDataList.size() > 0) {
            if (fileDataList.equals(mListFile)) {
                mIsLoading = false;
                mFragmentLibBinding.pullToRefresh.setRefreshing(false);

                return;
            }

            mListFile = new ArrayList<>();
            mListFile.addAll(fileDataList);
            ////////////////

            Parcelable oldPosition = null;
            if (mFragmentLibBinding.dataListArea.getLayoutManager() != null) {
                oldPosition = mFragmentLibBinding.dataListArea.getLayoutManager().onSaveInstanceState();
            }
            mFileListAdapter.setData(mListFile);
            if (oldPosition != null) {
                mFragmentLibBinding.dataListArea.getLayoutManager().onRestoreInstanceState(oldPosition);
            }
            showDataArea();
        } else {
            showNoDataArea();
        }

        mIsLoading = false;
        mFragmentLibBinding.pullToRefresh.setRefreshing(false);
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
        mFragmentLibBinding.noDataErrorTv.setText(R.string.no_pdf_found);
        mFragmentLibBinding.dataListArea.setVisibility(View.GONE);
        mFragmentLibBinding.noDataErrorArea.setVisibility(View.VISIBLE);
        mFragmentLibBinding.noPermissionArea.setVisibility(View.GONE);
        mFragmentLibBinding.loadingArea.setVisibility(View.GONE);
    }

    private void showPermissionIssueArea() {
        mFragmentLibBinding.noPermissionArea.setOnClickListener(v -> {
            startRequestPermission();
        });
        mFragmentLibBinding.dataListArea.setVisibility(View.GONE);
        mFragmentLibBinding.noDataErrorArea.setVisibility(View.GONE);
        mFragmentLibBinding.noPermissionArea.setVisibility(View.VISIBLE);
        mFragmentLibBinding.loadingArea.setVisibility(View.GONE);
    }

    private void showDataArea() {
        mFragmentLibBinding.dataListArea.setVisibility(View.VISIBLE);
        mFragmentLibBinding.noDataErrorArea.setVisibility(View.GONE);
        mFragmentLibBinding.noPermissionArea.setVisibility(View.GONE);
        mFragmentLibBinding.loadingArea.setVisibility(View.GONE);
    }

    private void showLoadingArea() {
        mFragmentLibBinding.dataListArea.setVisibility(View.GONE);
        mFragmentLibBinding.noDataErrorArea.setVisibility(View.GONE);
        mFragmentLibBinding.loadingArea.setVisibility(View.VISIBLE);
        mFragmentLibBinding.noPermissionArea.setVisibility(View.GONE);
    }

    @Override
    public void onClickItem(int position) {
        String filePath = mListFile.get(position).getFilePath();
        if (filePath == null) {
            filePath = RealPathUtil.getInstance().getRealPath(mActivity, mListFile.get(position).getFileUri(), FileUtils.FileType.type_PDF);
        }

        String finalFilePath = filePath;
        if (mActivity != null) {
            mActivity.showMyPdfAdsBeforeAction(() -> {
                mActivity.gotoPdfFileViewActivity(finalFilePath);
                mFileListAdapter.setCurrentItem(position);
            });
        }
    }

    @Override
    public void onMainFunctionItem(int position) {
        FileUtils.shareFile(mActivity, new File(mListFile.get(position).getFilePath()));
    }

    @Override
    public void onOptionItem(int position) {
        FileData fileData = mListFile.get(position);
        mLibViewModel.startCheckIsFileBookmarked(fileData.getFilePath(), position, this::onShowOption);
    }

    @Override
    public void onBookMarkItem(int posision,boolean add) {
        optionBookmarkPdf(mListFile.get(posision).getFilePath(), add);
    }

    private void onShowOption(int position, boolean isBookmarked) {
        FileData fileData = mListFile.get(position);

        if (PdfUtils.isPDFEncrypted(fileData.getFilePath())) {
            hideOptionDialog();

            CommonUtils.hideKeyboard(mActivity);
        } else {
            hideOptionDialog();
            pdfOptionDialog = new PdfOptionDialog(isBookmarked, fileData.getDisplayName(), fileData.getTimeAdded(), position, new PdfOptionDialog.FileOptionListener() {
                @Override
                public void openFile(int position) {
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
                    optionBookmarkPdf(mListFile.get(position).getFilePath(), isAdd);
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
    private void hideOptionDialog() {

        if (pdfOptionDialog != null && pdfOptionDialog.isVisible()) {
            pdfOptionDialog.dismiss();
        }
    }

    private void openPdfFile(int position) {
        FirebaseUtils.sendEventFunctionUsed(mActivity, "Open file", "From library");
        onClickItem(position);
    }

    private void renamePdfFile(int position) {
        FirebaseUtils.sendEventFunctionUsed(mActivity, "Rename file", "From library");

        FileData fileData = mListFile.get(position);
        String displayName;
        try {
            displayName = fileData.getDisplayName().substring(0, fileData.getDisplayName().lastIndexOf("."));
        } catch (Exception e) {
            return;
        }

        RenameFileDialog renameFileDialog = new RenameFileDialog(mActivity, displayName, new RenameFileDialog.RenameFileListener() {
            @Override
            public void onSubmitName(String name) {
                String newName = name + ".pdf";
                int result = FileUtils.renameFile(fileData, newName);

                if (result == -2 || result == 0) {
                    ToastUtils.showMessageShort(mActivity, getString(R.string.can_not_edit_video_name));
                } else if (result == -1) {
                    SnackBarUtils.getSnackbar(mActivity, getString(R.string.duplicate_video_name) + ": " + name).show();
                } else {
                    SnackBarUtils.getSnackbar(mActivity, getString(R.string.rename_file_success)).show();
                    String oldFilePath = fileData.getFilePath();

                    fileData.setFilePath(fileData.getFilePath().replace(fileData.getDisplayName(), newName));
                    fileData.setDisplayName(newName);
                    mListFile.set(position, fileData);
                    mFileListAdapter.updateData(position, fileData);

                    mLibViewModel.updateSavedData(oldFilePath, fileData.getFilePath());
                }
            }

            @Override
            public void onCancel() {

            }
        });

        renameFileDialog.show();
    }

    private void deletePdfFile(int position) {
        FirebaseUtils.sendEventFunctionUsed(mActivity, "Delete file", "From library");

        FileData fileData = mListFile.get(position);

        ConfirmDialog confirmDialog = new ConfirmDialog(mActivity, mActivity.getString(R.string.confirm_delete_file_title), mActivity.getString(R.string.confirm_delete_file_message), new ConfirmDialog.ConfirmListener() {
            @Override
            public void onSubmit() {
                if (mActivity != null && !mActivity.notHaveStoragePermission()) {
                    FileUtils.deleteFileOnExist(fileData.getFilePath());
                    mLibViewModel.clearSavedData(fileData.getFilePath());

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
