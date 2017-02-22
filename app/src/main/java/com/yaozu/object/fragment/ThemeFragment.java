package com.yaozu.object.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.yaozu.object.R;

/**
 * Created by jxj42 on 2017/2/22.
 */

public class ThemeFragment extends BaseFragment {
    public static String TAG = "ThemeFragment";
    public ListView listView;
    public ListThemeAdapter themeAdapter;

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
        listView = (ListView) view.findViewById(R.id.common_refresh_listview);
        return view;
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
