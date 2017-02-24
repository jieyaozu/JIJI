package com.yaozu.object.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.NoScrollGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/6.
 */

public class ForumListViewAdapter extends RecyclerView.Adapter<ForumListViewAdapter.MyViewHolder> {
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_listview_forum, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setTypeface(typeface);
        holder.userName.setTypeface(typeface);
        holder.support.setTypeface(typeface);
        holder.reply.setTypeface(typeface);
        holder.content.setTypeface(typeface);

        final Post post = dataList.get(position);
        Utils.setUserImg(post.getUserIcon(), holder.userIcon);
        holder.userName.setText(post.getUserName());
        holder.createTime.setText(DateUtil.getRelativeTime(post.getCreatetime()));
        holder.title.setText(post.getTitle());
        if (!TextUtils.isEmpty(post.getContent())) {
            holder.content.setText(post.getContent());
            holder.content.setVisibility(View.VISIBLE);
        } else {
            holder.content.setVisibility(View.GONE);
        }
        holder.reply.setText(post.getReplyNum() + "回复");
        NoScrollGridViewAdapter adapter = new NoScrollGridViewAdapter();
        adapter.setData(post.getImages());
        holder.noScrollGridView.setAdapter(adapter);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toPostDetailActivity(mContext, post);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userIcon;
        TextView userName;
        TextView createTime;
        TextView support;
        TextView reply;
        TextView title;
        TextView content;
        NoScrollGridView noScrollGridView;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            noScrollGridView = (NoScrollGridView) itemView.findViewById(R.id.item_listview_forum_container);
            userIcon = (ImageView) itemView.findViewById(R.id.item_listview_forum_usericon);
            userName = (TextView) itemView.findViewById(R.id.item_listview_forum_username);
            createTime = (TextView) itemView.findViewById(R.id.item_listview_forum_time);
            title = (TextView) itemView.findViewById(R.id.item_listview_forum_title);
            content = (TextView) itemView.findViewById(R.id.item_listview_forum_content);
            support = (TextView) itemView.findViewById(R.id.item_listview_forum_support);
            reply = (TextView) itemView.findViewById(R.id.item_listview_forum_reply);
        }
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
