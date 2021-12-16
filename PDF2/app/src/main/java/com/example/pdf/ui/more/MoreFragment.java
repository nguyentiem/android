package com.pdfreader.scanner.pdfviewer.ui.more;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.model.RecentData;
import com.pdfreader.scanner.pdfviewer.databinding.FragmentRecentBinding;
import com.pdfreader.scanner.pdfviewer.databinding.MoreFragmentBinding;
import com.pdfreader.scanner.pdfviewer.listener.OnFileItemWithOptionClickListener;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseFragment;
import com.pdfreader.scanner.pdfviewer.ui.component.ConfirmDialog;
import com.pdfreader.scanner.pdfviewer.ui.component.PdfOptionDialog;
import com.pdfreader.scanner.pdfviewer.ui.component.RenameFileDialog;
import com.pdfreader.scanner.pdfviewer.ui.recent.RecentFragment;
import com.pdfreader.scanner.pdfviewer.ui.recent.RecentNavigator;
import com.pdfreader.scanner.pdfviewer.ui.recent.RecentViewModel;
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
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MoreFragment extends BaseFragment<MoreFragmentBinding, MoreViewModel> implements MoreNavigator, OnFileItemWithOptionClickListener {
     MoreViewModel moreViewModel;
     MoreFragmentBinding moreFragmentBinding;

    @Override
    public void onClickItem(int position) {

    }

    @Override
    public void onMainFunctionItem(int position) {

    }

    @Override
    public void onOptionItem(int position) {

    }

    @Override
    public void onBookMarkItem(int posision,boolean add ) {

    }

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return  R.layout.more_fragment;
    }

    @Override
    public void reloadEasyChangeData() {

    }
    public static MoreFragment newInstance() {
        MoreFragment moreFragment = new MoreFragment();

        Bundle args = new Bundle();
        moreFragment.setArguments(args);
        moreFragment.setRetainInstance(true);

        return moreFragment;
    }
    @Override
    public MoreViewModel getViewModel() {
        moreViewModel = ViewModelProviders.of(this).get(MoreViewModel.class);
        return moreViewModel;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        moreViewModel.setNavigator(this);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        moreFragmentBinding = getViewDataBinding();

//        setForClick();

    }


}
