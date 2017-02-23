package com.yaozu.object.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

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
        return view;
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
            return view;
        }
    }
}
