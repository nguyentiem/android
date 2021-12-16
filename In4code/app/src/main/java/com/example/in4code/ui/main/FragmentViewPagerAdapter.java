package com.example.in4code.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.ListFragment;

import com.example.in4code.ui.favorite.FavoriteFragment;
import com.example.in4code.ui.home.HomeFragment;
import com.example.in4code.ui.recent.RecentFragment;

import org.jetbrains.annotations.NotNull;

public class FragmentViewPagerAdapter   extends FragmentPagerAdapter {

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
                return new HomeFragment();
            case 1:
                return new RecentFragment();
            case 2:
                return null;
            case 3:

                return new FavoriteFragment();
            case 4:
                return new ListFragment();

        }

        return new HomeFragment();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {


        return super.getItemPosition(object);
    }


    @Override
    public int getCount() {
        return 5;
    }
}