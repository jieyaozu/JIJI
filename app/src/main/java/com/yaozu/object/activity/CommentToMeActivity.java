package com.yaozu.object.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.bean.Comment;
import com.yaozu.object.entity.CommentToMeReqData;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyaozu on 2017/5/8.
 * 评论我的
 */

public class CommentToMeActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private CommentListAdapter commentListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private HeaderViewRecyclerAdapter stringAdapter;

    private List<Comment> commentList = new ArrayList<>();
    private String mLastCommentId = "";

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_comment_to_me);
    }

    @Override
    protected void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.common_refresh_recyclerview);
    }

    @Override
    protected void initData() {
        commentListAdapter = new CommentListAdapter();
        stringAdapter = new HeaderViewRecyclerAdapter(commentListAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter(linearLayoutManager, stringAdapter);

        refreshLayout.doRefreshing();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("评论我的");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onIRefresh() {
        super.onIRefresh();
        mLastCommentId = "";
        refreshLayout.setIsCanLoad(true);
        requestCommentData(mLastCommentId);
    }

    @Override
    protected void onILoad() {
        super.onILoad();
        requestCommentData(mLastCommentId);
    }

    //网络请求获取数据
    private void requestCommentData(final String lastCommentid) {
        String url = DataInterface.COMMENT_TO_ME_LIST + "userid=" + LoginInfo.getInstance(this).getUserAccount() + "&lastcommentid=" + lastCommentid;
        RequestManager.getInstance().getRequest(this, url, CommentToMeReqData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                refreshLayout.completeRefresh();
                if (object != null) {
                    CommentToMeReqData commentToMeReqData = (CommentToMeReqData) object;
                    List<Comment> comments = commentToMeReqData.getBody().getCommentList();
                    if (comments != null) {
                        if (TextUtils.isEmpty(lastCommentid)) {
                            commentList.clear();
                        }
                        commentList.addAll(comments);
                        commentListAdapter.notifyDataSetChanged();
                        if (comments.size() < 15) {
                            refreshLayout.setIsCanLoad(false);
                        }
                        if (comments.size() > 0) {
                            mLastCommentId = comments.get(comments.size() - 1).getCommentid();
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

    class CommentListAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CommentViewHolder viewHolder = new CommentViewHolder(LayoutInflater.from(CommentToMeActivity.this).inflate(R.layout.item_listview_comment_to_me, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            final Comment comment = commentList.get(position);
            Utils.setUserImg(comment.getSiconpath(), holder.ivUserIcon);
            holder.tvUserName.setText(comment.getUserName());
            holder.tvPublictime.setText(DateUtil.getRelativeTime(comment.getPublictime()));
            holder.tvContent.setText(comment.getContent());
            Spannable spannable = new SpannableString(comment.getPostUserName() + " : " + comment.getPostContent());
            ForegroundColorSpan nameColor = new ForegroundColorSpan(getResources().getColor(R.color.top_blue));
            spannable.setSpan(nameColor, 0, comment.getPostUserName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvPostContent.setText(spannable);
            holder.tvPostContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostReplyDetailActivity(CommentToMeActivity.this, comment.getPostid(), true);
                }
            });
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivUserIcon;
        private TextView tvUserName;
        private TextView tvPublictime;
        private TextView tvContent;
        private TextView tvPostContent;
        private View view;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ivUserIcon = (ImageView) itemView.findViewById(R.id.comment_to_me_usericon);
            tvUserName = (TextView) itemView.findViewById(R.id.comment_to_me_username);
            tvPublictime = (TextView) itemView.findViewById(R.id.comment_to_me_pubilctime);
            tvContent = (TextView) itemView.findViewById(R.id.comment_to_me_content);
            tvPostContent = (TextView) itemView.findViewById(R.id.comment_to_me_postcontent);
            view = itemView;
        }
    }
}
