package com.yaozu.object.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yaozu.object.R;
import com.yaozu.object.widget.PagerSlidingTabStrip;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jxj42 on 2017/4/3.
 */

public class ForumFragment extends BaseFragment {
    public static String TAG = "ForumFragment";
    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ForumPagerAdapter forumPagerAdapter;
    private String[] titles = {"热帖", "娱乐", "财经", "政治", "科技", "IT", "技术宅"};

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.forum_viewpager);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.forum_sliding_tab);
        forumPagerAdapter = new ForumPagerAdapter(getFragmentManager());
        viewPager.setAdapter(forumPagerAdapter);
        pagerSlidingTabStrip.setViewPager(viewPager);

        //viewPager.setOffscreenPageLimit(titles.length);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_home, container, false);
        return view;
    }

    private class ForumPagerAdapter extends FragmentPagerAdapter {
        public Map<Integer, Fragment> fragments = new HashMap<Integer, Fragment>();

        public ForumPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment == null) {
                fragment = new ForumChildFragment();
                fragments.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
