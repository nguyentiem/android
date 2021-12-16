package com.pdfreader.scanner.pdfviewer.utils.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pdfreader.scanner.pdfviewer.ui.browser.BrowserFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> fragmentTitle = new ArrayList<>();

    public ViewPageAdapter(@NonNull FragmentManager fm)
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
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return fragments.get(0);

            default:
                return null;
        }

    }
public BrowserFragment  getBrowserFragment(){
    return (BrowserFragment)fragments.get(0);
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
