package com.yaozu.object.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.bean.Post;
import com.yaozu.object.entity.DetailReplyPostListInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.NoScrollGridView;
import com.yaozu.object.widget.NoScrollListView;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/4/24.
 */

public class UserReplyPostActivity extends BaseActivity {
    private RecyclerView recyclerView;
    public ListReplyAdapter themeAdapter;
    private HeaderViewRecyclerAdapter stringAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Post> postList = new ArrayList<>();
    private String userid, username;
    private int currentPage = 1;
    private int itemWidth;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_user_theme);
    }

    @Override
    protected void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.common_refresh_recyclerview);
    }

    @Override
    protected void initData() {
        userid = getIntent().getStringExtra(IntentKey.INTENT_USERID);
        username = getIntent().getStringExtra(IntentKey.INTENT_USERNAME);

        int screenWidth = Utils.getScreenWidth(this);
        itemWidth = (screenWidth - this.getResources().getDimensionPixelSize(R.dimen.forum_item_margin) * 2) / 3;

        themeAdapter = new ListReplyAdapter();
        stringAdapter = new HeaderViewRecyclerAdapter(themeAdapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter((LinearLayoutManager) layoutManager, stringAdapter);

        refreshLayout.doRefreshing();
    }

    @Override
    protected void onIRefresh() {
        super.onIRefresh();
        currentPage = 1;
        refreshLayout.setIsCanLoad(true);
        requestData(userid, currentPage);
    }

    @Override
    protected void onILoad() {
        super.onILoad();
        currentPage++;
        requestData(userid, currentPage);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle(username + "回复的帖子");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void requestData(String userid, final int page) {
        String url = DataInterface.FIND_USER_REPLYPOST_LIST + "userid=" + userid + "&pageindex=" + page;
        RequestManager.getInstance().getRequest(this, url, DetailReplyPostListInfo.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                refreshLayout.completeRefresh();
                if (object != null) {
                    DetailReplyPostListInfo homeForumDataInfo = (DetailReplyPostListInfo) object;
                    List<Post> listData = homeForumDataInfo.getBody().getPostlist();
                    if (listData != null) {
                        if (page == 1) {
                            postList.clear();
                        }
                        postList.addAll(listData);
                        themeAdapter.notifyDataSetChanged();
                        if (listData.size() < 15) {
                            refreshLayout.setIsCanLoad(false);
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
            ReplyViewHolder viewHolder = new ReplyViewHolder(LayoutInflater.from(UserReplyPostActivity.this).inflate(R.layout.item_listview_user_replypost, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ReplyViewHolder holder, int position) {
            final Post post = postList.get(position);
            holder.tvTime.setText(DateUtil.getRelativeTime(post.getCreatetime()));
            holder.tvparentTitle.setText(post.getParenttitle());
            holder.tvContent.setText(post.getContent());
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
                    IntentUtil.toPostReplyDetailActivity(UserReplyPostActivity.this, post, "", 1);
                }
            });
            holder.titleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostDetailActivity(UserReplyPostActivity.this, post.getParentid());
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
                view = View.inflate(UserReplyPostActivity.this, R.layout.item_nogridview, null);
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
