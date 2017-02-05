package com.yaozu.object.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.yaozu.object.R;
import com.yaozu.object.utils.Utils;

/**
 * Created by jxj42 on 2017/2/5.
 */

public class ForumFragment extends Fragment {
    public static String TAG = "ForumFragment";
    private ListView mListView;
    private ListViewAdapter listViewAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAdapter = new ListViewAdapter();
        mListView.setAdapter(listViewAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        mListView = (ListView) view.findViewById(R.id.fragment_forum_listview);
        return view;
    }

    public class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 20;
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
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(ForumFragment.this.getActivity(), R.layout.item_listview_forum, null);
            }
            ImageView userIcon = (ImageView) view.findViewById(R.id.item_listview_forum_usericon);
            Utils.setUserImg("", userIcon);
            return view;
        }
    }
}
