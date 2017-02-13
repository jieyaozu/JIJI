package com.yaozu.object.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.bean.MyImages;
import com.yaozu.object.bean.Post;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/9.
 */

public class PostDetailAdapter extends BaseAdapter {
    private Context mContext;
    protected Typeface typeface;
    private List<Post> mListData = new ArrayList<>();
    private String userid;

    public PostDetailAdapter(Context context, Typeface typeface, String userid) {
        mContext = context;
        this.typeface = typeface;
        this.userid = userid;
    }

    public void setAddData(List<Post> datas) {
        if (datas != null) {
            mListData.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        mListData.clear();
    }

    @Override
    public int getCount() {
        return mListData.size();
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
        View view = View.inflate(mContext, R.layout.item_listview_replypost, null);
        ImageView usericon = (ImageView) view.findViewById(R.id.item_listview_replypost_usericon);
        TextView userName = (TextView) view.findViewById(R.id.item_listview_replypost_username);
        TextView time = (TextView) view.findViewById(R.id.item_listview_replypost_time);
        TextView content = (TextView) view.findViewById(R.id.item_listview_replypost_content);
        TextView isMain = (TextView) view.findViewById(R.id.item_listview_replypost_ismain);
        NoScrollListView noScrollListView = (NoScrollListView) view.findViewById(R.id.item_listview_replypost_container);
        NoScrollListAdapter adapter = new NoScrollListAdapter();
        noScrollListView.setAdapter(adapter);

        Post post = mListData.get(position);
        Utils.setUserImg(post.getUserIcon(), usericon);
        userName.setText(post.getUserName());
        time.setText(DateUtil.getRelativeTime(post.getCreatetime()));
        content.setText(post.getContent());
        adapter.setData(post.getImages());
        if (post.getUserid().equals(userid)) {
            isMain.setVisibility(View.VISIBLE);
        } else {
            isMain.setVisibility(View.GONE);
        }
        userName.setTypeface(typeface);
        content.setTypeface(typeface);
        return view;
    }

    public class NoScrollListAdapter extends BaseAdapter {
        private List<MyImages> imagesList = new ArrayList<>();

        public void setData(List<MyImages> images) {
            if (images != null) {
                imagesList.clear();
                imagesList.addAll(images);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return imagesList.size();
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
            View view = View.inflate(mContext, R.layout.item_noscroll_listview_reply, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.item_noscroll_listview_reply_image);
            MyImages image = imagesList.get(position);
            ImageLoader.getInstance().displayImage(image.getImageurl_big(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
            return view;
        }
    }
}
