package com.pdfreader.scanner.pdfviewer.ui.imageview;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.databinding.ActivityImageViewBinding;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseBindingActivity;
import com.pdfreader.scanner.pdfviewer.utils.ToastUtils;
import com.pdfreader.scanner.pdfviewer.utils.file.FileUtils;

public class ImageViewActivity extends BaseBindingActivity<ActivityImageViewBinding, ImageViewViewModel> implements ImageViewNavigator {
    private ActivityImageViewBinding mActivityImageViewBinding;
    private String mImagePath;
    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_view;
    }

    @Override
    public ImageViewViewModel getViewModel() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityImageViewBinding = getViewDataBinding();

        mImagePath = getIntent().getStringExtra(EXTRA_FILE_PATH);
        if (mImagePath != null && mImagePath.length() > 0 && FileUtils.checkFileExist(mImagePath)) {
            initView();
        } else {
            ToastUtils.showMessageShort(this, getString(R.string.file_not_found));
            finish();
        }
    }

    @Override
    protected void initView() {
        mActivityImageViewBinding.toolbar.toolbarNameTv.setText("");
        mActivityImageViewBinding.toolbar.toolbarBtnBack.setOnClickListener(view -> onBackPressed());

        Glide.with(this)
                .load(mImagePath)
                .into(mActivityImageViewBinding.imageView);
    }

    @Override
    protected void setClick() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }
}
