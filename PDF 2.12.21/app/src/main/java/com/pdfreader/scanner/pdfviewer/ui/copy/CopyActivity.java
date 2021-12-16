package com.pdfreader.scanner.pdfviewer.ui.copy;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.constants.DataConstants;
import com.pdfreader.scanner.pdfviewer.data.model.FileData;
import com.pdfreader.scanner.pdfviewer.databinding.ActivityCopyBinding;
import com.pdfreader.scanner.pdfviewer.listener.OnFolderItemWithOptionClickListener;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseBindingActivity;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseViewModel;
import com.pdfreader.scanner.pdfviewer.ui.component.PdfOptionDialog;
import com.pdfreader.scanner.pdfviewer.utils.DialogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CopyActivity extends BaseBindingActivity implements OnFolderItemWithOptionClickListener {
    CopyViewModel copyViewModel;
    private String filesrc;
    private ActivityCopyBinding activityCopyBinding;
    private boolean mIsLoading;
    private List<FileData> mListFile = new ArrayList<>();
    private CopyAdapter copyAdapter;
    private SweetAlertDialog mRequestPermissionDialog;
    private final int REQUEST_EXTERNAL_PERMISSION_FOR_LOAD_FILE_CODE = 1;
    private PdfOptionDialog pdfOptionDialog;
    private FileData mCurrentFolder;

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_copy;
    }

    @Override
    public BaseViewModel getViewModel() {
        copyViewModel = ViewModelProviders.of(this).get(CopyViewModel.class);
        return copyViewModel;
    }

    @Override
    protected void initView() {
        filesrc = getIntent().getExtras().getString("filePath");
        activityCopyBinding.pullToRefreshFolder.setOnRefreshListener(() -> {
            reloadData(false);
        });

        copyAdapter = new CopyAdapter(this);
        activityCopyBinding.dataListFolderArea.setLayoutManager(new LinearLayoutManager(this));
        activityCopyBinding.dataListFolderArea.setAdapter(copyAdapter);

        File rootFile = Environment.getExternalStorageDirectory();
        String rootDir = rootFile.getAbsolutePath();
        mCurrentFolder = new FileData();
        mCurrentFolder.setFilePath(rootDir);
    }

    @Override
    protected void setClick() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    @Override
    public void onBackPressed() {
        try {
            if (mCurrentFolder.getParentFile() != null && mCurrentFolder.getParentFile().getFilePath() != null && mCurrentFolder.getParentFile().getFilePath().length() > 0) {
                if (mIsLoading) return;

                File dir = new File(mCurrentFolder.getParentFile().getFilePath());
                if (dir.isDirectory()) {
                    FileData temp = mCurrentFolder.getParentFile();
                    mCurrentFolder = new FileData(temp);

                    reloadData(false);
                }
            } else {
                super.onBackPressed();
            }

        } catch (Exception e) {
            super.onBackPressed();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_copy);
        activityCopyBinding = ActivityCopyBinding.inflate(getLayoutInflater()); // tọa thể hiện
        setContentView(activityCopyBinding.getRoot());
        // setForClick();
        initView();
        setForLiveData();
        activityCopyBinding.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyClick();
            }
        });
        activityCopyBinding.backButtonCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        reloadData(true);
    }

    @SuppressLint("SetTextI18n")
    public void reloadData(boolean isForceReload) {
        if (this.notHaveStoragePermission()) {
            activityCopyBinding.pullToRefreshFolder.setRefreshing(false);

            showPermissionIssueArea();
            mIsLoading = false;
            return;
        }

        if (mIsLoading) return;

        mIsLoading = true;

        if (mListFile == null || mListFile.size() == 0 || isForceReload) {
            showLoadingArea();
        }

        activityCopyBinding.folderDir.setText("Dir: " + mCurrentFolder.getFilePath());
        copyViewModel.setCurrentPath(mCurrentFolder);
        copyViewModel.getFolderList();
    }

    private void setForLiveData() {
        copyViewModel.getmListFolderLiveData().observe(this, this::updateData);
    }

    private void updateData(List<FileData> fileDataList) {
        if (fileDataList.size() > 0) {
            if (fileDataList.equals(mListFile)) {
                mIsLoading = false;
                activityCopyBinding.pullToRefreshFolder.setRefreshing(false);
                return;
            }

            mListFile = new ArrayList<>();
            mListFile.addAll(fileDataList);

            Parcelable oldPosition = null;
            if (activityCopyBinding.dataListFolderArea.getLayoutManager() != null) {
                oldPosition = activityCopyBinding.dataListFolderArea.getLayoutManager().onSaveInstanceState();
            }
            copyAdapter.setData(mListFile);
            if (oldPosition != null) {
                activityCopyBinding.dataListFolderArea.getLayoutManager().onRestoreInstanceState(oldPosition);
            }
            showDataArea();
        } else {
            showNoDataArea();
        }
        mIsLoading = false;
        activityCopyBinding.pullToRefreshFolder.setRefreshing(false);
    }

    private void startRequestPermission() {
        if (this != null && this.notHaveStoragePermission()) {
            mRequestPermissionDialog = DialogFactory.getDialogRequestSomething(this, getString(R.string.title_need_permission), getString(R.string.need_permission_to_get_file));
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

        }
    }

    @Override
    public void onResume() {
        if (mIsRequestFullPermission) {
            mIsRequestFullPermission = false;

            if (!this.notHaveStoragePermission()) {
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

    public boolean isCurrentFolderRoot() {
        File rootFile = Environment.getExternalStorageDirectory();
        String rootDir = rootFile.getAbsolutePath();
        return mCurrentFolder == null || mCurrentFolder.getFilePath().equals(rootDir);
    }

    private void showNoDataArea() {
        activityCopyBinding.noDataFolderErrorTv.setText(R.string.no_pdf_found);
        activityCopyBinding.dataListFolderArea.setVisibility(View.GONE);
        activityCopyBinding.noDataFolderErrorArea.setVisibility(View.VISIBLE);
        activityCopyBinding.noPermissionAreaFolder.setVisibility(View.GONE);
        activityCopyBinding.loadingAreaFolder.setVisibility(View.GONE);
    }

    private void showPermissionIssueArea() {
        activityCopyBinding.noPermissionAreaFolder.setOnClickListener(v -> {
            startRequestPermission();
        });
        activityCopyBinding.dataListFolderArea.setVisibility(View.GONE);
        activityCopyBinding.noDataFolderErrorArea.setVisibility(View.GONE);
        activityCopyBinding.noPermissionAreaFolder.setVisibility(View.VISIBLE);
        activityCopyBinding.loadingAreaFolder.setVisibility(View.GONE);
    }

    private void showDataArea() {
        activityCopyBinding.dataListFolderArea.setVisibility(View.VISIBLE);
        activityCopyBinding.noDataFolderErrorArea.setVisibility(View.GONE);
        activityCopyBinding.noPermissionAreaFolder.setVisibility(View.GONE);
        activityCopyBinding.loadingAreaFolder.setVisibility(View.GONE);
    }

    private void showLoadingArea() {
        activityCopyBinding.dataListFolderArea.setVisibility(View.GONE);
        activityCopyBinding.noDataFolderErrorArea.setVisibility(View.GONE);
        activityCopyBinding.loadingAreaFolder.setVisibility(View.VISIBLE);
        activityCopyBinding.noPermissionAreaFolder.setVisibility(View.GONE);
    }

    @Override
    public void onClickItem(int position) {
        if (position >= 0 && position < mListFile.size()) {
            FileData fileData = mListFile.get(position);
            String filePath = fileData.getFilePath();
            if (fileData.getFileType().equals(DataConstants.FILE_TYPE_DIRECTORY)) {
                mCurrentFolder = fileData;
                reloadData(false);
                return;
            }
        }
    }

    public void copyClick() {
        showLoadingArea();
//      filesrc
        Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String message = new String("");
                String newDir = convertPath(mCurrentFolder);
                File newFile = new File(newDir);

                if (newFile.exists()) {
                    message = new String("chose other dirtory");
                    /// file daton tai
                } else {
                    try {
                        copy(new File(filesrc), newFile);
                        message = new String("copy success");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                emitter.onSuccess(message);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                finish();
            }


            @Override
            public void onError(@NonNull Throwable e) {

            }
        });

    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public String convertPath(FileData fileData) {

        String filePath1 = fileData.getFilePath();
        String result = new String();
        if (filePath1 != null && filesrc != null) {
            int dem = 0;
            int len = filesrc.length();
            int i = 0;
            for (i = len - 1; i >= 0; i--) {
                if (filesrc.charAt(i) == '/') {

                    break;

                }
            }
            result = filesrc.substring(i, len);
            Log.d("TAG", "coppy String : " + result);
            result = filePath1 + result;
        }
        Log.d("TAG", "coppy String : " + result);
        return result;
    }
}