package com.yaozu.object.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.bean.Post;
import com.yaozu.object.entity.DetailReplyPostListInfo;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/26.
 */

public class CollectActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private CollectAdapter collectAdapter;
    private HeaderViewRecyclerAdapter stringAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Post> postList = new ArrayList<>();
    private int currentIndex = 1;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_collect);
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.common_refresh_recyclerview);
    }

    @Override
    protected void initData() {
        collectAdapter = new CollectAdapter();
        stringAdapter = new HeaderViewRecyclerAdapter(collectAdapter);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter((LinearLayoutManager) layoutManager, stringAdapter);

        refreshLayout.doRefreshing();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onIRefresh() {
        super.onIRefresh();
        currentIndex = 1;
        refreshLayout.setIsCanLoad(true);
        requestCollects(currentIndex);
    }

    @Override
    protected void onILoad() {
        super.onILoad();
        currentIndex++;
        requestCollects(currentIndex);
    }

    private void requestCollects(final int currentIndex) {
        String url = DataInterface.FIND_COLLECT_POST + "userid=" + LoginInfo.getInstance(this).getUserAccount() + "&pageindex=" + currentIndex;
        RequestManager.getInstance().getRequest(this, url, DetailReplyPostListInfo.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                refreshLayout.completeRefresh();
                if (object != null) {
                    DetailReplyPostListInfo postListInfo = (DetailReplyPostListInfo) object;
                    List<Post> posts = postListInfo.getBody().getPostlist();
                    if (currentIndex == 1) {
                        postList.clear();
                    }
                    if (posts != null) {
                        postList.addAll(posts);
                    }
                    collectAdapter.notifyDataSetChanged();
                    if (posts == null || posts.size() < Constant.PAGE_SIZE) {
                        refreshLayout.setIsCanLoad(false);
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {
                refreshLayout.completeRefresh();
            }
        });
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("我的收藏");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.CollectViewHolder> {

        @Override
        public CollectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CollectViewHolder viewHolder = new CollectViewHolder(LayoutInflater.from(CollectActivity.this).inflate(R.layout.item_collect, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CollectViewHolder holder, int position) {
            final Post post = postList.get(position);
            holder.tvTitle.setText(post.getTitle());
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostDetailActivity(CollectActivity.this, post.getPostid());
                }
            });
        }

        @Override
        public int getItemCount() {
            return postList.size();
        }

        public class CollectViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;

            public CollectViewHolder(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.item_collect_title);
            }
        }
    }
}
