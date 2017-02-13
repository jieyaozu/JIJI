package com.yaozu.object.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.bean.MyImages;
import com.yaozu.object.bean.Post;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.NoScrollGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/6.
 */

public class ForumListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<Post> dataList = new ArrayList<Post>();
    private Typeface typeface;
    private int screenWidth, itemWidth;

    public ForumListViewAdapter(Context context) {
        mContext = context;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/zhunyuan.ttf");
        screenWidth = Utils.getScreenWidth(context);
        itemWidth = (screenWidth - context.getResources().getDimensionPixelSize(R.dimen.forum_item_margin) * 2) / 3;
    }

    public void addData(List<Post> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        dataList.clear();
    }

    @Override
    public int getCount() {
        return dataList.size();
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
        ViewHolder viewHolder;
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            viewHolder = new ViewHolder();
            view = View.inflate(mContext, R.layout.item_listview_forum, null);
            viewHolder.userIcon = (ImageView) view.findViewById(R.id.item_listview_forum_usericon);
            viewHolder.userName = (TextView) view.findViewById(R.id.item_listview_forum_username);
            viewHolder.createTime = (TextView) view.findViewById(R.id.item_listview_forum_time);
            viewHolder.title = (TextView) view.findViewById(R.id.item_listview_forum_title);
            viewHolder.support = (TextView) view.findViewById(R.id.item_listview_forum_support);
            viewHolder.reply = (TextView) view.findViewById(R.id.item_listview_forum_reply);
            viewHolder.title.setTypeface(typeface);
            viewHolder.userName.setTypeface(typeface);
            viewHolder.support.setTypeface(typeface);
            viewHolder.reply.setTypeface(typeface);
            viewHolder.noScrollGridView = (NoScrollGridView) view.findViewById(R.id.item_listview_forum_container);
            viewHolder.adapter = new NoScrollGridViewAdapter();
            view.setTag(viewHolder);
        }
        final Post post = dataList.get(position);
        Utils.setUserImg(post.getUserIcon(), viewHolder.userIcon);
        viewHolder.userName.setText(post.getUserName());
        viewHolder.createTime.setText(post.getCreatetime());
        viewHolder.title.setText(post.getTitle());

        viewHolder.adapter.setData(post.getImages());
        viewHolder.noScrollGridView.setAdapter(viewHolder.adapter);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toPostDetailActivity(mContext, post);
            }
        });
        return view;
    }

    private class ViewHolder {
        ImageView userIcon;
        TextView userName;
        TextView createTime;
        TextView support;
        TextView reply;
        TextView title;
        NoScrollGridView noScrollGridView;
        NoScrollGridViewAdapter adapter;
    }

    private class NoScrollGridViewAdapter extends BaseAdapter {
        private List<MyImages> imagesList = new ArrayList<MyImages>();

        public void setData(List<MyImages> images) {
            if (images == null) {
                this.imagesList.clear();
                notifyDataSetChanged();
                return;
            }
            if (this.imagesList != null) {
                this.imagesList.clear();
                this.imagesList.addAll(images);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            if (imagesList.size() > 3) {
                return 3;
            }
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
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(mContext, R.layout.item_nogridview, null);
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.item_nogridview_image);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemWidth;
            imageView.setLayoutParams(params);
            MyImages image = imagesList.get(position);
            ImageLoader.getInstance().displayImage(image.getImageurl_small(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
            return view;
        }
    }
}
