package com.pdfreader.scanner.pdfviewer.utils.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pdfreader.scanner.pdfviewer.listener.OnMoreListener;
import com.pdfreader.scanner.pdfviewer.ui.bookmark.BookmarkFragment;

import com.pdfreader.scanner.pdfviewer.ui.browser.BrowserFragment;
import com.pdfreader.scanner.pdfviewer.ui.lib.LibFragment;

import com.pdfreader.scanner.pdfviewer.ui.more.MoreFragment;
import com.pdfreader.scanner.pdfviewer.ui.recent.RecentFragment;

import org.jetbrains.annotations.NotNull;

public class FragmentViewPagerAdapter extends FragmentPagerAdapter {
//    private BrowserFragment mBrowserFragment;
    private FragmentManager mFragmentManager;
    OnMoreListener listener;
    public FragmentViewPagerAdapter(FragmentManager manager, int behavior,OnMoreListener listener) {
        super(manager, behavior);
        this.listener = listener;
        mFragmentManager = manager;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LibFragment.newInstance();
            case 3:
                return BookmarkFragment.newInstance();
            case 1:
                return RecentFragment.newInstance();
//            case 2:
//                mBrowserFragment = BrowserFragment.newInstance();
//                return mBrowserFragment;
            case 4:
                return MoreFragment.newInstance(this.listener);

//            default: return LibFragment.newInstance();
        }

            return  RecentFragment.newInstance();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof BrowserFragment) {
            mFragmentManager.beginTransaction().remove((Fragment) object).commit();
            return POSITION_NONE;
        }

        return super.getItemPosition(object);
    }

//    public BrowserFragment getBrowserFragment() {
//        return mBrowserFragment;
//    }

    @Override
    public int getCount() {
        return 5;
    }
}