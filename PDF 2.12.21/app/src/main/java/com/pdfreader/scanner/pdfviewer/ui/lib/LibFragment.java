package com.pdfreader.scanner.pdfviewer.ui.lib;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
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
import com.pdfreader.scanner.pdfviewer.ui.copy.CopyActivity;
import com.pdfreader.scanner.pdfviewer.ui.main.MainActivity;
import com.pdfreader.scanner.pdfviewer.utils.CommonUtils;
import com.pdfreader.scanner.pdfviewer.utils.DialogFactory;
import com.pdfreader.scanner.pdfviewer.utils.FirebaseUtils;
import com.pdfreader.scanner.pdfviewer.utils.SnackBarUtils;
import com.pdfreader.scanner.pdfviewer.utils.ToastUtils;
import com.pdfreader.scanner.pdfviewer.utils.adapter.FileListNoAdsWithOptionAdapter;
import com.pdfreader.scanner.pdfviewer.utils.file.FileSortUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.RealPathUtil;
import com.pdfreader.scanner.pdfviewer.utils.glide.GlideApp;
import com.pdfreader.scanner.pdfviewer.utils.pdf.PdfUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LibFragment extends BaseFragment<FragmentLibBinding, LibViewModel> implements LibNavigator, OnFileItemWithOptionClickListener,SettingSortDialog.SettingSortSubmit {//
    private static final String TAG = "libFragment";
    private LibViewModel mLibViewModel;
    private FragmentLibBinding mFragmentLibBinding;
    public  int mCurrentSortBy =1;
    private boolean mIsLoading;
    private List<FileData> mListFile = new ArrayList<>();
    private FileListNoAdsWithOptionAdapter mFileListAdapter;
    private SweetAlertDialog mRequestPermissionDialog;
    private final int REQUEST_EXTERNAL_PERMISSION_FOR_LOAD_FILE_CODE = 1;
    private PdfOptionDialog pdfOptionDialog;
    SettingSortDialog dialog;



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

        if (mIsLoading) {

                return;

        }
        if (mListFile == null || isForceReload||mIsLoading) {
            showLoadingArea();
        }

        mIsLoading = true;

        mLibViewModel.getFileList(mCurrentSortBy);

//        mFileListAdapter.setData(mLibViewModel.getListFileLiveData().getValue());
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
//        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentLibBinding = getViewDataBinding();
//        Log.d(TAG, "onViewCreated: ");
        setForLiveData();
        setForClick();
        initView();
       // reloadData(true);
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
    public void onPause() {
//        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onResume() {
//        Log.d(TAG, "onResume: ");

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

        reloadData(true);
        super.onResume();
    }


    public static LibFragment newInstance() {
        //if(instance==null) {
            LibFragment libFragment = new LibFragment();

            Bundle args = new Bundle();
            libFragment.setArguments(args);
            libFragment.setRetainInstance(true);
      //  }
        return libFragment;
    }

    private void initView() {
        mFragmentLibBinding.pullToRefresh.setOnRefreshListener(() -> {
            reloadData(true);
        });

        mFileListAdapter = new FileListNoAdsWithOptionAdapter(this);
        mFragmentLibBinding.dataListArea.setLayoutManager(new LinearLayoutManager(mActivity));
        mFragmentLibBinding.dataListArea.setAdapter(mFileListAdapter);

    }

    private void setForClick() {//
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

         dialog = new SettingSortDialog(mActivity,this, mCurrentSortBy);
        dialog.show();

    }
    @Override
    public void updateNewSort(int newSort) {
//        Log.d(TAG, "updateNewSort: ");
//        Toast.makeText(getContext(),"sort done "+mListFile.size(),Toast.LENGTH_SHORT).show();
        mCurrentSortBy = newSort;
        FileSortUtils.performSortOperation(mCurrentSortBy,mListFile);
//        Log.d(TAG, "updateNewSort: "+mCurrentSortBy);
        mFileListAdapter.setData(mListFile);
    }



    private void setForLiveData() {
        mLibViewModel.getListFileLiveData().observe(getViewLifecycleOwner(), new Observer<List<FileData>>() {
            @Override
            public void onChanged(List<FileData> fileData) {
//                Log.d(TAG, "onChanged: "+mCurrentSortBy);

                updateData(fileData);
            }
        });
    }

    private void updateData(List<FileData> fileDataList) {
//        Log.d(TAG, "updateData: "+fileDataList.size());
         if (fileDataList.size() > 0) {
             if (fileDataList.equals(mListFile)) {
                 mIsLoading = false;
                 mFragmentLibBinding.pullToRefresh.setRefreshing(false);

                 return;
             }

             mListFile = fileDataList;

//             mListFile.addAll(fileDataList);
             ////////////////
//             Log.d(TAG, "updateData: "+mListFile.size());
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
        mListFile.get(posision).setBookMark(add);
        mFileListAdapter.updateData(posision, mListFile.get(posision));
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
                    // gui file path den activity moi va lua chon
                    Intent intent = new Intent(getContext(), CopyActivity.class);
                    intent.putExtra("filePath",mListFile.get(position).getFilePath());
                    startActivity(intent);
                    ///////////////////// cu
//

                    // intent here

                }

                @Override
                public void mergeFile(int position) {
                    Toast.makeText(getContext(),"merge",Toast.LENGTH_SHORT).show();

                }


                @Override
                public void optionBookmark(int position, boolean isAdd) {
                    onBookMarkItem(position,isAdd);
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
