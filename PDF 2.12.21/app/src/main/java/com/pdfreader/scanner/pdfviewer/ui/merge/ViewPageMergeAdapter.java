package com.pdfreader.scanner.pdfviewer.ui.merge;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pdfreader.scanner.pdfviewer.ui.browser.BrowserFragment;
import com.pdfreader.scanner.pdfviewer.ui.lib.LibFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPageMergeAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> fragmentTitle = new ArrayList<>();

    public ViewPageMergeAdapter(@NonNull FragmentManager fm)
    {
        super(fm);
    }

    public void add(Fragment fragment)
    {
        fragments.add(fragment);
        // fragmentTitle.add(title);
    }

    @NonNull @Override public Fragment getItem(int position)
    {
        //Log.d("TAG", ""+ (fragments.get(0) instanceof LibFragment));
        switch (position) {

            case 0: // Fragment # 0 - This will show FirstFragment
                return fragments.get(0);

            default:
                return fragments.get(0);
        }

    }
    public MergeFragment getMerge(){
        return (MergeFragment) fragments.get(0);
    }
    @Override public int getCount()
    {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return fragmentTitle.get(position);
    }
}
