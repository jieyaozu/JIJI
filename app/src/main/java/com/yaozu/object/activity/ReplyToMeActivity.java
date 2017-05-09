package com.yaozu.object.activity;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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
import com.yaozu.object.bean.MessageBean;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.bean.Post;
import com.yaozu.object.db.dao.MessageBeanDao;
import com.yaozu.object.entity.DetailReplyPostListInfo;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.MsgType;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.NoScrollGridView;
import com.yaozu.object.widget.NoScrollListView;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyaozu on 2017/5/8.
 * 回复我的
 */

public class ReplyToMeActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ListReplyAdapter replyAdapter;
    private LinearLayoutManager linearLayoutManager;
    private HeaderViewRecyclerAdapter stringAdapter;

    private List<Post> postList = new ArrayList<>();
    private int itemWidth;
    private String mLastPostid = "";
    private MessageBeanDao messageBeanDao;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_replytome);
    }

    @Override
    protected void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.common_refresh_recyclerview);
    }

    @Override
    protected void initData() {
        messageBeanDao = new MessageBeanDao(this);
        int screenWidth = Utils.getScreenWidth(this);
        itemWidth = (screenWidth - this.getResources().getDimensionPixelSize(R.dimen.forum_item_margin) * 2) / 3;

        replyAdapter = new ListReplyAdapter();
        stringAdapter = new HeaderViewRecyclerAdapter(replyAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter(linearLayoutManager, stringAdapter);

        refreshLayout.doRefreshing();

        clearReplyRemindMessage();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onIRefresh() {
        super.onIRefresh();
        mLastPostid = "";
        refreshLayout.setIsCanLoad(true);
        requestData(mLastPostid);
    }

    @Override
    protected void onILoad() {
        super.onILoad();
        requestData(mLastPostid);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("回复我的");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 清除回复提醒消息
     */
    private void clearReplyRemindMessage() {
        //把群消息提醒数置为0
        MessageBean messageBean = messageBeanDao.findMessageBean(MsgType.TYPE_REPLY);
        messageBean.setNewMsgnumber(0);
        messageBeanDao.updateBean(messageBean);
        //发个广播更新下UI
        Intent playingintent = new Intent(IntentKey.NOTIFY_MESSAGE_REMIND);
        LocalBroadcastManager playinglocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        playinglocalBroadcastManager.sendBroadcast(playingintent);
    }

    private void requestData(final String lastPostid) {
        String url = DataInterface.REPLY_TO_ME_POSTLIST + "userid=" + LoginInfo.getInstance(this).getUserAccount() + "&lastpostid=" + lastPostid;
        RequestManager.getInstance().getRequest(this, url, DetailReplyPostListInfo.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                refreshLayout.completeRefresh();
                if (object != null) {
                    DetailReplyPostListInfo homeForumDataInfo = (DetailReplyPostListInfo) object;
                    List<Post> listData = homeForumDataInfo.getBody().getPostlist();
                    if (listData != null) {
                        if (TextUtils.isEmpty(lastPostid)) {
                            postList.clear();
                        }
                        postList.addAll(listData);
                        replyAdapter.notifyDataSetChanged();
                        if (listData.size() < 15) {
                            refreshLayout.setIsCanLoad(false);
                        }
                        if (listData != null && listData.size() > 0) {
                            mLastPostid = listData.get(listData.size() - 1).getPostid();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {
                refreshLayout.completeRefresh();
            }
        });
    }

    class ListReplyAdapter extends RecyclerView.Adapter<ReplyViewHolder> {
        @Override
        public ReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ReplyViewHolder viewHolder = new ReplyViewHolder(LayoutInflater.from(ReplyToMeActivity.this).inflate(R.layout.item_listview_reply_to_me, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ReplyViewHolder holder, int position) {
            final Post post = postList.get(position);
            holder.tvTime.setText(DateUtil.getRelativeTime(post.getCreatetime()));
            holder.tvparentTitle.setText(post.getParenttitle());
            holder.tvContent.setText(post.getContent());
            holder.tvUserName.setText(post.getUserName());
            Utils.setUserImg(post.getUserIcon(), holder.ivUserIcon);
            NoScrollGridViewAdapter adapter = new NoScrollGridViewAdapter();
            adapter.setData(post.getImages());
            holder.imageListview.setAdapter(adapter);
            if (position == (postList.size() - 1)) {
                holder.divider.setVisibility(View.GONE);
            } else {
                holder.divider.setVisibility(View.VISIBLE);
            }
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostReplyDetailActivity(ReplyToMeActivity.this, post, "", 1);
                }
            });
            holder.titleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostDetailActivity(ReplyToMeActivity.this, post.getParentid());
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return postList.size();
        }
    }

    class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvparentTitle;
        TextView tvContent;
        ImageView ivUserIcon;
        TextView tvUserName;
        NoScrollGridView imageListview;
        NoScrollListView commentListview;
        RelativeLayout titleLayout;
        View divider;
        View view;

        public ReplyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvTime = (TextView) itemView.findViewById(R.id.item_listview_user_replypost_time);
            tvparentTitle = (TextView) itemView.findViewById(R.id.item_listview_user_replypost_parenttitle);
            titleLayout = (RelativeLayout) itemView.findViewById(R.id.item_listview_user_replypost_parenttitle_rl);
            tvContent = (TextView) itemView.findViewById(R.id.item_listview_user_replypost_content);
            imageListview = (NoScrollGridView) itemView.findViewById(R.id.item_listview_user_replypost_container);
            commentListview = (NoScrollListView) itemView.findViewById(R.id.item_listview_user_replypost_comments);
            ivUserIcon = (ImageView) itemView.findViewById(R.id.item_listview_reply_tome_usericon);
            tvUserName = (TextView) itemView.findViewById(R.id.item_listview_reply_tome_username);
            divider = itemView.findViewById(R.id.item_userreply_divider);
        }
    }

    private class NoScrollGridViewAdapter extends BaseAdapter {
        private List<MyImage> imagesList = new ArrayList<MyImage>();

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
                view = View.inflate(ReplyToMeActivity.this, R.layout.item_nogridview, null);
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
