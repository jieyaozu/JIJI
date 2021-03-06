package com.yaozu.object.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.ObjectApplication;
import com.yaozu.object.R;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.bean.Post;
import com.yaozu.object.bean.constant.SendStatus;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.NetUtil;
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
    private int screenWidth, itemWidth;
    private boolean isShowPermissionTag = true;
    private int clickPosition = 0;

    public ForumListViewAdapter(Context context) {
        mContext = context;
        screenWidth = Utils.getScreenWidth(context);
        itemWidth = (screenWidth - context.getResources().getDimensionPixelSize(R.dimen.forum_item_margin) * 2) / 3;
    }

    public void addData(List<Post> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addDataToFirst(Post post) {
        if (dataList != null) {
            dataList.add(0, post);
            notifyDataSetChanged();
        }
    }

    /**
     * 去掉已经删除掉的贴子
     */
    public void removeDeletePost() {
        if (dataList != null) {
            dataList.remove(clickPosition);
            notifyDataSetChanged();
        }
    }

    public void setShowPermissionTag(boolean show) {
        isShowPermissionTag = show;
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Post post = dataList.get(position);
        if (ObjectApplication.tempPost != null && ObjectApplication.tempPost.getPostid().equals(post.getPostid())) {
            post = ObjectApplication.tempPost;
        }
        Utils.setUserImg(post.getUserIcon(), holder.userIcon);
        holder.userName.setText(post.getUserName());
        holder.createTime.setText(DateUtil.getRelativeTime(post.getCreatetime()));
        holder.title.setText(post.getTitle());
        if (!TextUtils.isEmpty(post.getContent())) {
            holder.content.setText(getNoImageContent(post.getContent()).trim());
            holder.content.setVisibility(View.VISIBLE);
        } else {
            holder.content.setVisibility(View.GONE);
        }
        if (!"0".equals(LoginInfo.getInstance(mContext).getAccountType())) {
            holder.superOperator.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(post.getUploadstatus()) || SendStatus.SUCCESS.equals(post.getUploadstatus())) {
            holder.tvSendStatus.setVisibility(View.GONE);
        } else {
            holder.tvSendStatus.setVisibility(View.VISIBLE);
            if (SendStatus.FAILED.equals(post.getUploadstatus())) {
                holder.tvSendStatus.setText("发送失败点击重试");
            } else {
                holder.tvSendStatus.setText("发送中...");
            }
        }
        setPermissionText(holder.tvPermission, post.getPermission());
        holder.support.setText(post.getSupportNum() + "赞");
        holder.reply.setText(post.getReplyNum() + "回复");
        NoScrollGridViewAdapter adapter = new NoScrollGridViewAdapter();
        adapter.setData(post.getImages());
        holder.noScrollGridView.setAdapter(adapter);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "删除", Toast.LENGTH_SHORT).show();
            }
        });
        holder.xiachen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "下沉", Toast.LENGTH_SHORT).show();
            }
        });
        final Post finalPost = post;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPosition = position;
                IntentUtil.toPostDetailActivity(mContext, finalPost);
            }
        });
        holder.tvSendStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SendStatus.UPLOADING.equals(finalPost.getUploadstatus())) {
                    return;
                }
                List<MyImage> imageList = new ArrayList<>();
                for (MyImage image : ObjectApplication.tempPost.getImages()) {
                    if (image.isSendSuccess != 1) {
                        imageList.add(image);
                    }
                }
                NetUtil.uploadPostImagesToServer(mContext, ObjectApplication.tempPost, imageList, ObjectApplication.tempPost.getPostid());
                notifyDataSetChanged();
            }
        });
    }

    private String getNoImageContent(String contentString) {
        String content = contentString.replaceAll("<img>(.+)</img>", "");
        return content;
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
        TextView delete;
        TextView xiachen;
        TextView tvSendStatus;
        TextView tvPermission;
        LinearLayout superOperator;
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
            delete = (TextView) itemView.findViewById(R.id.forum_delete);
            tvSendStatus = (TextView) itemView.findViewById(R.id.post_sendstatud);
            tvPermission = (TextView) itemView.findViewById(R.id.item_listview_forum_permission);
            xiachen = (TextView) itemView.findViewById(R.id.forum_xiachen);
            superOperator = (LinearLayout) itemView.findViewById(R.id.forum_superadmin_operator);
        }
    }

    private void setPermissionText(TextView textView, String permission) {
        if (!isShowPermissionTag) {
            textView.setVisibility(View.GONE);
            return;
        }
        if ("public".equals(permission)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText("公开");
            textView.setTextColor(mContext.getResources().getColor(R.color._public));
            textView.setBackgroundResource(R.drawable.public_permission_shape);
        } else if ("protected".equals(permission)) {
            textView.setText("保护");
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(mContext.getResources().getColor(R.color._protected));
            textView.setBackgroundResource(R.drawable.protected_permission_shape);
        } else if ("private".equals(permission)) {
            textView.setText("私有");
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(mContext.getResources().getColor(R.color._private));
            textView.setBackgroundResource(R.drawable.private_permission_shape);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private class NoScrollGridViewAdapter extends BaseAdapter {
        private List<MyImage> imagesList = new ArrayList<>();

        public void setData(List<MyImage> images) {
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
            MyImage image = imagesList.get(position);
            ImageLoader.getInstance().displayImage(image.getImageurl_small(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
            return view;
        }
    }
}
