package com.yaozu.object.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yaozu.object.R;
import com.yaozu.object.utils.Utils;

/**
 * Created by jxj42 on 2017/2/6.
 */

public class ForumListViewAdapter extends BaseAdapter {
    private Context mContext;

    public ForumListViewAdapter(Context context) {
        mContext = context;
    }

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
            view = View.inflate(mContext, R.layout.item_listview_forum, null);
        }
        ImageView userIcon = (ImageView) view.findViewById(R.id.item_listview_forum_usericon);
        Utils.setUserImg("", userIcon);
        return view;
    }
}
