package com.yaozu.object.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yaozu.object.fragment.FindFragment;
import com.yaozu.object.fragment.ThemeFragment;

import java.util.List;

/**
 * Created by jxj42 on 2017/2/22.
 */

public class UserinfoPagerAdapter extends FragmentPagerAdapter {
    private List<String> titles;

    public UserinfoPagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new ThemeFragment();
        } else {
            fragment = new ThemeFragment();
        }
        Bundle jumpBundle = new Bundle();
        fragment.setArguments(jumpBundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
