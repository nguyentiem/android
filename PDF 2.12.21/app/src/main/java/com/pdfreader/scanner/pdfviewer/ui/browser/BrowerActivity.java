package com.pdfreader.scanner.pdfviewer.ui.browser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.databinding.ActivityBrowerBinding;
import com.pdfreader.scanner.pdfviewer.databinding.ActivityMainBinding;
import com.pdfreader.scanner.pdfviewer.listener.OnMoreListener;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseBindingActivity;
import com.pdfreader.scanner.pdfviewer.ui.base.BaseViewModel;
import com.pdfreader.scanner.pdfviewer.ui.main.MainNavigator;
import com.pdfreader.scanner.pdfviewer.ui.main.MainViewModel;
import com.pdfreader.scanner.pdfviewer.utils.adapter.FragmentViewPagerAdapter;
import com.pdfreader.scanner.pdfviewer.utils.adapter.ViewPageAdapter;

public class BrowerActivity extends BaseBindingActivity  {
     ViewPager viewPager;
     ViewPageAdapter viewPagerAdapter;
    BrowerActivityViewModel browerActivityViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brower);
        viewPager =findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(BrowserFragment.newInstance());
        viewPager.setAdapter(viewPagerAdapter);


    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setClick() {

    }

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public void onBackPressed() {
        BrowserFragment browserFragment = viewPagerAdapter.getBrowserFragment();
        if (browserFragment != null) {
            if (!browserFragment.isCurrentFolderRoot()) {
                browserFragment.onBackPress();
            } else{
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_brower;
    }

    @Override
    public BrowerActivityViewModel getViewModel() {
        browerActivityViewModel = ViewModelProviders.of(this).get(BrowerActivityViewModel.class);
        return browerActivityViewModel;
    }


    @Override
    public void onFragmentDetached(String tag) {

    }
}