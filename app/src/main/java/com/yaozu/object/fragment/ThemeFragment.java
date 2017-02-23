package com.yaozu.object.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.activity.user.UserInfoActivity;
import com.yaozu.object.widget.StickyListView;

/**
 * Created by jxj42 on 2017/2/22.
 */

public class ThemeFragment extends BaseFragment {
    public static String TAG = "ThemeFragment";
    public StickyListView listView;
    public ListThemeAdapter themeAdapter;
    private UserInfoActivity.StickyScrollCallBack scrollListener;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        themeAdapter = new ListThemeAdapter();
        listView.setAdapter(themeAdapter);

        listView.setOnLoadListener(new StickyListView.OnLoadListener() {
            @Override
            public void onLoad() {
                System.out.println("===========onLoad============>");
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme, container, false);
        listView = (StickyListView) view.findViewById(R.id.userinfo_stickylistview);
        listView.setScrollCallBack(scrollListener);
        View viewspace = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UserInfoActivity.STICKY_HEIGHT2);
        viewspace.setLayoutParams(params);
        listView.addHeaderView(viewspace);
        listView.invalidScroll();
        return view;
    }

    public int getStickyHeight() {
        int scrollHeight = listView.getFirstViewScrollTop();
        if (scrollHeight > UserInfoActivity.STICKY_HEIGHT1) {
            return UserInfoActivity.STICKY_HEIGHT1;
        }
        return scrollHeight;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setStickyH(int stickyH) {
        if (Math.abs(stickyH - getStickyHeight()) < 5) {
            return;
        }
        listView.setSelectionFromTop(0, -stickyH);
    }

    public void setScrollCallBack(UserInfoActivity.StickyScrollCallBack scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    protected void onIRefresh() {
        super.onIRefresh();
    }

    @Override
    protected void onILoad() {
        super.onILoad();
    }

    class ListThemeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.item_listview_theme, null);
            } else {
                view = convertView;
            }
            TextView tvTitle = (TextView) view.findViewById(R.id.item_listview_theme_title);
            tvTitle.setText("ddddd" + position);
            return view;
        }
    }
}
