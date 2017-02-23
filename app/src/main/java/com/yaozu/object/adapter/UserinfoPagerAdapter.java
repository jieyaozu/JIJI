package com.yaozu.object.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/22.
 */

public class UserinfoPagerAdapter extends FragmentPagerAdapter {
    private List<String> titles;
    private List<Fragment> fragmentList = new ArrayList<>();

    public UserinfoPagerAdapter(FragmentManager fm, List<String> titles, List<Fragment> fragmentList) {
        super(fm);
        this.titles = titles;
        this.fragmentList.addAll(fragmentList);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragmentList.get(position);
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
