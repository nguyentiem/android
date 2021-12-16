package com.pdfreader.scanner.pdfviewer.ui.merge;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.databinding.FragmentMergeBinding;
import com.pdfreader.scanner.pdfviewer.listener.OnFileItemClickListener;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseFragment;
import com.pdfreader.scanner.pdfviewer.ui.component.ConfirmDialog;
import com.pdfreader.scanner.pdfviewer.ui.lib.LibFragment;
import com.pdfreader.scanner.pdfviewer.utils.DialogFactory;
import com.pdfreader.scanner.pdfviewer.utils.FirebaseUtils;
import com.pdfreader.scanner.pdfviewer.utils.SnackBarUtils;
import com.pdfreader.scanner.pdfviewer.utils.adapter.FileListSelectAdapter;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MergeFragment extends BaseFragment<FragmentMergeBinding, MergeViewModel> implements MergeNavigator, OnFileItemClickListener {//

    private MergeViewModel viewModel;
    private FragmentMergeBinding binding;
    public int mCurrentSortBy = 2;
    private boolean mIsLoading;
    private List<FileData> mListFile = new ArrayList<>();
    private FileListSelectAdapter mFileListAdapter;
    private SweetAlertDialog mRequestPermissionDialog;
    private final int REQUEST_EXTERNAL_PERMISSION_FOR_LOAD_FILE_CODE = 1;

    public FileListSelectAdapter getmFileListAdapter() {
        return mFileListAdapter;
    }
    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_merge;
    }

    @Override
    public void reloadEasyChangeData() {

    }

    public void reloadData(boolean isForceReload) {
        if (mActivity != null && mActivity.notHaveStoragePermission()) {
            binding.pullToRefreshMerge.setRefreshing(false);
            showPermissionIssueArea();
            mIsLoading = false;
            return;
        }
        if (mIsLoading) {
            return;
        }
        if (mListFile == null || isForceReload || mIsLoading) {
            showLoadingArea();
        }
        mIsLoading = true;
        viewModel.getAllList(mCurrentSortBy);
    }

    @Override
    public MergeViewModel getViewModel() {
        viewModel = ViewModelProviders.of(this).get(MergeViewModel.class);
        return viewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

       // viewModel.setNavigator(this);
//        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();
        setForLiveData();
        reloadData(true);
        initView();

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

      //  reloadData(true);
        super.onResume();
    }


    public static MergeFragment newInstance() {
        //if(instance==null) {
        MergeFragment libFragment = new MergeFragment();

        return libFragment;
    }

    private void initView() {
        binding.pullToRefreshMerge.setOnRefreshListener(() -> {
            reloadData(true);
        });

        mFileListAdapter = new FileListSelectAdapter(this);
        binding.dataListAreaMerge.setLayoutManager(new LinearLayoutManager(mActivity));
        binding.dataListAreaMerge.setAdapter(mFileListAdapter);

    }


    private void setForLiveData() {
        viewModel.getListFileLiveData().observe(getViewLifecycleOwner(), new Observer<List<FileData>>() {
            @Override
            public void onChanged(List<FileData> fileData) {
//                Log.d(TAG, "onChanged: "+mCurrentSortBy);

                updateData(fileData);
            }
        });
        viewModel.getLoadListLiveData().observe(getViewLifecycleOwner(), this::updateLoadListDone);
    }
    private void updateLoadListDone(boolean loadListDone) {
    }
    private void updateData(List<FileData> fileDataList) {

        if (fileDataList.size() > 0) {
            if (fileDataList.equals(mListFile)) {
                mIsLoading = false;
                binding.pullToRefreshMerge.setRefreshing(false);

                return;
            }

            mListFile = fileDataList;

            Parcelable oldPosition = null;
            if (binding.dataListAreaMerge.getLayoutManager() != null) {
                oldPosition = binding.dataListAreaMerge.getLayoutManager().onSaveInstanceState();
            }
            mFileListAdapter.setData(mListFile);
            if (oldPosition != null) {
                binding.dataListAreaMerge.getLayoutManager().onRestoreInstanceState(oldPosition);
            }
            showDataArea();
        } else {
            showNoDataArea();
        }

        mIsLoading = false;
        binding.pullToRefreshMerge.setRefreshing(false);
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
        binding.noDataErrorTvMerge.setText(R.string.no_pdf_found);
        binding.dataListAreaMerge.setVisibility(View.GONE);
        binding.noDataErrorAreaMerge.setVisibility(View.VISIBLE);
        binding.noPermissionAreaMerge.setVisibility(View.GONE);
        binding.loadingAreaMerge.setVisibility(View.GONE);
    }

    private void showPermissionIssueArea() {
        binding.noPermissionAreaMerge.setOnClickListener(v -> {
            startRequestPermission();
        });
        binding.dataListAreaMerge.setVisibility(View.GONE);
        binding.noDataErrorAreaMerge.setVisibility(View.GONE);
        binding.noPermissionAreaMerge.setVisibility(View.VISIBLE);
        binding.loadingAreaMerge.setVisibility(View.GONE);
    }

    private void showDataArea() {
        binding.dataListAreaMerge.setVisibility(View.VISIBLE);
        binding.noDataErrorAreaMerge.setVisibility(View.GONE);
        binding.noPermissionAreaMerge.setVisibility(View.GONE);
        binding.loadingAreaMerge.setVisibility(View.GONE);
    }

    private void showLoadingArea() {
        binding.dataListAreaMerge.setVisibility(View.GONE);
        binding.noDataErrorAreaMerge.setVisibility(View.GONE);
        binding.loadingAreaMerge.setVisibility(View.VISIBLE);
        binding.noPermissionAreaMerge.setVisibility(View.GONE);
    }

    @Override
    public void onClickItem(int position) {
        // them vao danh sasch
        mFileListAdapter.revertData(position);
        LogFile();

    }
public void LogFile(){
    List<FileData> fileData = mFileListAdapter.getSelectedList();
    for (FileData file : fileData) {
        Log.d("TAG", "LogFile: "+file.getDisplayName());
    }
}

//    private void openPdfFile(int position) {
//        FirebaseUtils.sendEventFunctionUsed(mActivity, "Open file", "From library");
//        onClickItem(position);
//    }

    private void deletePdfFile() {
        FirebaseUtils.sendEventFunctionUsed(mActivity, "Delete file", "From library");
        List<FileData> fileData = mFileListAdapter.getSelectedList();
        ConfirmDialog confirmDialog = new ConfirmDialog(mActivity, mActivity.getString(R.string.confirm_delete_file_title), mActivity.getString(R.string.confirm_delete_file_message), new ConfirmDialog.ConfirmListener() {
            @Override
            public void onSubmit() {
                if (mActivity != null && !mActivity.notHaveStoragePermission()) {
                   //  for here
                    for (FileData file : fileData) {
                        FileUtils.deleteFileOnExist(file.getFilePath());
                        viewModel.clearSavedData(file.getFilePath());
                        mListFile.remove(file);
                    }

                    mFileListAdapter.removeSelectedList();

                    if (mListFile.size() == 0) {
                        showNoDataArea();
                    }
                    SnackBarUtils.getSnackbar(mActivity, mActivity.getString(R.string.delete_success_text)).show();

                }
            }

            @Override
            public void onCancel() {

            }
        });
        confirmDialog.show();
    }

}
