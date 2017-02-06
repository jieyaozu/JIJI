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
import android.widget.Toast;

import com.yaozu.object.R;
import com.yaozu.object.adapter.ForumListViewAdapter;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.NoScrollListView;

/**
 * Created by jxj42 on 2017/2/5.
 */

public class ForumFragment extends Fragment implements View.OnClickListener {
    public static String TAG = "ForumFragment";
    private ListView mListView;
    private ForumListViewAdapter listViewAdapter;
    private View mViewHeader;
    private NoScrollListView mHeaderListView;
    private HeaderListViewAdapter mHeaderAdapter;
    private ImageView ivButton;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAdapter = new ForumListViewAdapter(this.getActivity());
        mListView.setAdapter(listViewAdapter);

        mHeaderAdapter = new HeaderListViewAdapter();
        mHeaderListView.setAdapter(mHeaderAdapter);

        ivButton.setOnClickListener(this);
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
        mViewHeader = inflater.inflate(R.layout.header_list_forum, null);
        ivButton = (ImageView) view.findViewById(R.id.fragment_forum_imageview);

        mHeaderListView = (NoScrollListView) mViewHeader.findViewById(R.id.header_list_forum_listview);
        mListView.addHeaderView(mViewHeader);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_forum_imageview:
                IntentUtil.toSendPostActivity(this.getActivity());
                break;
        }
    }

    public class HeaderListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 5;
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
                view = View.inflate(ForumFragment.this.getActivity(), R.layout.item_listview_header_forum, null);
            }
            return view;
        }
    }
}
