package com.example.miniproject1;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragAdapter extends FragmentPagerAdapter {
    public int num = 3;
    public FragAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return new BanDoFrag();
            case 2:
                return new ThongTinFrag();
             case 1:
                return new KhaiBaoFrag();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return num;
    }
}
