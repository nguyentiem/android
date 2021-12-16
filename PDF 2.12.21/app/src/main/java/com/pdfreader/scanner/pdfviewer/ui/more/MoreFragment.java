package com.pdfreader.scanner.pdfviewer.ui.more;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.databinding.MoreFragmentBinding;
import com.pdfreader.scanner.pdfviewer.listener.OnMoreListener;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseFragment;

public class MoreFragment extends BaseFragment<MoreFragmentBinding, MoreViewModel> implements MoreNavigator {
    MoreViewModel moreViewModel;
    MoreFragmentBinding moreFragmentBinding;
    OnMoreListener listener;

    public MoreFragment(OnMoreListener listener) {
        this.listener = listener;
    }

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.more_fragment;
    }

    @Override
    public void reloadEasyChangeData() {

    }

    public static MoreFragment newInstance(OnMoreListener listener) {

        MoreFragment moreFragment = new MoreFragment(listener);

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
        moreFragmentBinding.feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.feedBackClick();
            }
        });

        moreFragmentBinding.filemanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.fileManageClick();
            }
        });

        moreFragmentBinding.removeads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.remoteAddClick();
            }
        });
        moreFragmentBinding.shareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.shareAppClick();
            }
        });

        moreFragmentBinding.rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.rateUsClick();
            }
        });
    }


}
