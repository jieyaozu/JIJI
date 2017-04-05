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
import com.yaozu.object.entity.HomeForumDataInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.NoScrollGridView;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyaozu on 2017/2/28.
 */

public class ThemePostActivity extends BaseActivity {
    private List<Post> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListThemeAdapter themeAdapter;
    private HeaderViewRecyclerAdapter stringAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String userid;
    private int currentPage = 1;
    private int itemWidth;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_theme_post);
    }

    @Override
    protected void initView() {
        userid = getIntent().getStringExtra(IntentKey.INTENT_USERID);
        recyclerView = (RecyclerView) findViewById(R.id.common_refresh_recyclerview);

        themeAdapter = new ListThemeAdapter();
        stringAdapter = new HeaderViewRecyclerAdapter(themeAdapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter((LinearLayoutManager) layoutManager, stringAdapter);

        refreshLayout.doRefreshing();
    }

    @Override
    protected void initData() {
        int screenWidth = Utils.getScreenWidth(this);
        itemWidth = (screenWidth - this.getResources().getDimensionPixelSize(R.dimen.forum_item_margin) * 2) / 3;
    }

    @Override
    protected void setListener() {

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
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("我的主题");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void requestData(String userid, final int page) {
        String url = DataInterface.FIND_USER_POST_LIST + "userid=" + userid + "&pageindex=" + page;
        RequestManager.getInstance().getRequest(this, url, HomeForumDataInfo.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                refreshLayout.completeRefresh();
                if (object != null) {
                    HomeForumDataInfo homeForumDataInfo = (HomeForumDataInfo) object;
                    List<Post> listData = homeForumDataInfo.getBody().getPostlist();
                    if (listData != null) {
                        if (page == 1) {
                            postList.clear();
                        }
                        postList.addAll(listData);
                        themeAdapter.notifyDataSetChanged();
                        if (listData.size() < Constant.PAGE_SIZE) {
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

    class ListThemeAdapter extends RecyclerView.Adapter<ThemeViewHolder> {

        @Override
        public ThemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ThemeViewHolder viewHolder = new ThemeViewHolder(LayoutInflater.from(ThemePostActivity.this).inflate(R.layout.item_listview_theme, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ThemeViewHolder holder, int position) {
            final Post post = postList.get(position);
            holder.tvTime.setText(DateUtil.getRelativeTime(post.getCreatetime()));
            holder.tvSupporu.setText(post.getSupportNum() + "赞");
            holder.tvReply.setText(post.getReplyNum() + "回复");
            holder.tvTitle.setText(post.getTitle());
            holder.tvContent.setText(post.getContent());
            NoScrollGridViewAdapter adapter = new NoScrollGridViewAdapter();
            adapter.setData(post.getImages());
            holder.noScrollGridView.setAdapter(adapter);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostDetailActivity(ThemePostActivity.this, post);
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

    class ThemeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        TextView tvSupporu;
        TextView tvReply;
        TextView tvTitle;
        TextView tvContent;
        NoScrollGridView noScrollGridView;
        View itemView;

        public ThemeViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTime = (TextView) itemView.findViewById(R.id.item_listview_theme_time);
            tvSupporu = (TextView) itemView.findViewById(R.id.item_listview_theme_support);
            tvReply = (TextView) itemView.findViewById(R.id.item_listview_theme_reply);
            tvTitle = (TextView) itemView.findViewById(R.id.item_listview_theme_title);
            tvContent = (TextView) itemView.findViewById(R.id.item_listview_theme_content);
            noScrollGridView = (NoScrollGridView) itemView.findViewById(R.id.item_listview_theme_container);
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
                view = View.inflate(ThemePostActivity.this, R.layout.item_nogridview, null);
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
