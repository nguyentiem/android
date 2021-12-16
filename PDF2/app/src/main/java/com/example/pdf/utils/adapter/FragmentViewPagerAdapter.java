package com.example.pdf.utils.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pdfreader.scanner.pdfviewer.ui.bookmark.BookmarkFragment;
import com.pdfreader.scanner.pdfviewer.ui.lib.LibFragment;
import com.pdfreader.scanner.pdfviewer.ui.more.MoreFragment;
import com.pdfreader.scanner.pdfviewer.ui.recent.RecentFragment;

import org.jetbrains.annotations.NotNull;

public class FragmentViewPagerAdapter extends FragmentPagerAdapter {
//    private BrowserFragment mBrowserFragment;
    private FragmentManager mFragmentManager;

    public FragmentViewPagerAdapter(FragmentManager manager, int behavior) {
        super(manager, behavior);

        mFragmentManager = manager;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LibFragment.newInstance();
            case 1:
                return BookmarkFragment.newInstance();
            case 2:
                return RecentFragment.newInstance();
//            case 3:
//                mBrowserFragment = BrowserFragment.newInstance();
//                return mBrowserFragment;
            case 4:
                return MoreFragment.newInstance();

        }

        return LibFragment.newInstance();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
//        if (object instanceof BrowserFragment) {
//            mFragmentManager.beginTransaction().remove((Fragment) object).commit();
//            return POSITION_NONE;
//        }

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