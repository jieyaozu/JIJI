package com.yaozu.object.activity.group;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaozu.object.ObjectApplication;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.adapter.ForumListViewAdapter;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.bean.Post;
import com.yaozu.object.entity.GroupForumDataInfo;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.pushreceiver.remind.NewPostRemind;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.widget.FloatingActionButton;
import com.yaozu.object.widget.NoScrollListView;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyaozu on 2017/4/5.
 * 展示群内所有帖子的页面
 */

public class GroupOfPostActivity extends BaseActivity implements View.OnClickListener {
    private ActionBar mActionBar;
    private GroupBean groupBean;
    public String TAG = this.getClass().getSimpleName();
    private RecyclerView mRecyclerView;
    private ForumListViewAdapter listViewAdapter;
    private View mViewHeader;
    private NoScrollListView mHeaderListView;
    private HeaderListViewAdapter mHeaderAdapter;
    private FloatingActionButton ivButton;
    private String mLastPostId = "";
    private HeaderViewRecyclerAdapter stringAdapter;
    private LinearLayoutManager linearLayoutManager;

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;

    private String isMember;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_group_of_post);
        Constant.SENDING_POST = false;
        registerUpdateReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NewPostRemind.getInstance(this).putRemind(groupBean.getGroupid(), 0);
        unRegisterUpdateRecevier();
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.common_refresh_recyclerview);
        LayoutInflater inflater = this.getLayoutInflater();
        mViewHeader = inflater.inflate(R.layout.header_list_forum, null);
        ivButton = (FloatingActionButton) findViewById(R.id.activity_forum_imageview);

        mHeaderListView = (NoScrollListView) mViewHeader.findViewById(R.id.header_list_forum_listview);
    }

    @Override
    protected void initData() {
        groupBean = (GroupBean) getIntent().getSerializableExtra(IntentKey.INTENT_GROUP);

        listViewAdapter = new ForumListViewAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        stringAdapter = new HeaderViewRecyclerAdapter(listViewAdapter);
        stringAdapter.addHeaderView(mViewHeader);
        mRecyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter(linearLayoutManager, stringAdapter);

        mHeaderAdapter = new HeaderListViewAdapter();
        mHeaderListView.setAdapter(mHeaderAdapter);

        ivButton.setOnClickListener(this);
        refreshLayout.doRefreshing();

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if ("0".equals(isMember)) {
                    return;
                }

                if (dy > 0 && !mIsAnimatingOut && ivButton.getVisibility() == View.VISIBLE) {
                    // User scrolled down and the FAB is currently visible -> hide the FAB
                    animateOut(ivButton);
                } else if (dy < 0 && ivButton.getVisibility() != View.VISIBLE) {
                    // User scrolled up and the FAB is currently not visible -> show the FAB
                    animateIn(ivButton);
                }

            }
        });
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onIRefresh() {
        mLastPostId = "";
        refreshLayout.setIsCanLoad(true);
        requestGroupPostList(groupBean.getGroupid(), mLastPostId);
    }

    @Override
    protected void onILoad() {
        requestGroupPostList(groupBean.getGroupid(), mLastPostId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_of_post_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_groupdetail:
                IntentUtil.toGroupDetailActivity(this, groupBean);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        mActionBar = actionBar;
        actionBar.setTitle(groupBean.getGroupname());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NewPostRemind.getInstance(this).putRemind(groupBean.getGroupid(), 0);
        if (Constant.SENDING_POST) {
            Constant.SENDING_POST = false;
            if (ObjectApplication.tempPost != null) {
                listViewAdapter.addDataToFirst(ObjectApplication.tempPost);
            } else {
                refreshLayout.doRefreshing();
            }
        }
        if (Constant.IS_DELETE_POST) {
            Constant.IS_DELETE_POST = false;
            listViewAdapter.removeDeletePost();
        }
    }

    /**
     * 获取群内的贴子
     *
     * @param lastpostid
     */
    private void requestGroupPostList(String groupid, final String lastpostid) {
        String userid = LoginInfo.getInstance(this).getUserAccount();
        String url = DataInterface.FIND_GROUP_POST_LIST + "groupid=" + groupid + "&userid=" + userid + "&lastpostid=" + lastpostid;
        RequestManager.getInstance().getRequest(this, url, GroupForumDataInfo.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                refreshLayout.completeRefresh();
                if (object != null) {
                    GroupForumDataInfo postDataInfo = (GroupForumDataInfo) object;
                    if (TextUtils.isEmpty(lastpostid)) {
                        listViewAdapter.clearData();
                        mHeaderAdapter.setData(postDataInfo.getBody().getToplist());
                        isMember = postDataInfo.getBody().getIsGroupMember();
                        if ("0".equals(isMember)) {
                            ivButton.setVisibility(View.GONE);
                        } else {
                            ivButton.setVisibility(View.VISIBLE);
                        }
                    }

                    List<Post> postList = postDataInfo.getBody().getPostlist();
                    listViewAdapter.addData(postList);
                    if (postList == null || postList.size() == 0) {
                        refreshLayout.setIsCanLoad(false);
                    } else {
                        mLastPostId = postList.get(postList.size() - 1).getPostid();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_forum_imageview:
                if (LoginInfo.getInstance(this).isLogining()) {
                    IntentUtil.toSendPostActivity(this, groupBean.getGroupid());
                } else {
                    IntentUtil.toLoginActivity(this);
                }
                break;
        }
    }

    public class HeaderListViewAdapter extends BaseAdapter {
        private List<Post> mDataList = new ArrayList<Post>();

        @Override
        public int getCount() {
            return mDataList.size();
        }

        public void setData(List<Post> datas) {
            if (datas != null) {
                mDataList.clear();
                mDataList.addAll(datas);
                notifyDataSetChanged();
            }
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
                view = View.inflate(GroupOfPostActivity.this, R.layout.item_listview_header_forum, null);
            }
            TextView tvTitle = (TextView) view.findViewById(R.id.home_toppost_title);
            final Post post = mDataList.get(position);
            tvTitle.setText(post.getTitle());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostDetailActivity(GroupOfPostActivity.this, post.getPostid());
                }
            });
            return view;
        }
    }

    private void animateOut(final FloatingActionButton button) {
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(INTERPOLATOR).withLayer()
                    .setListener(new ViewPropertyAnimatorListener() {
                        public void onAnimationStart(View view) {
                            mIsAnimatingOut = true;
                        }

                        public void onAnimationCancel(View view) {
                            mIsAnimatingOut = false;
                        }

                        public void onAnimationEnd(View view) {
                            mIsAnimatingOut = false;
                            view.setVisibility(View.GONE);
                        }
                    }).start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_out);
            anim.setInterpolator(INTERPOLATOR);
            anim.setDuration(200L);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    mIsAnimatingOut = true;
                }

                public void onAnimationEnd(Animation animation) {
                    mIsAnimatingOut = false;
                    button.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {
                }
            });
            button.startAnimation(anim);
        }
    }

    // Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                    .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_in);
            anim.setDuration(200L);
            anim.setInterpolator(INTERPOLATOR);
            button.startAnimation(anim);
        }
    }

    /**
     * @Description:
     * @author
     * @date 2013-10-28 jieyaozu 10:30:27
     */
    protected void registerUpdateReceiver() {
        if (updataroadcastReceiver == null) {
            updataroadcastReceiver = new UpdataBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(IntentKey.NOTIFY_UPLOAD_IMAGE_SUCCESS);
            filter.addAction(IntentKey.NOTIFY_UPLOAD_IMAGE_FAILED);
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.registerReceiver(updataroadcastReceiver, filter);
        }
    }

    protected void unRegisterUpdateRecevier() {
        if (updataroadcastReceiver != null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.unregisterReceiver(updataroadcastReceiver);
            updataroadcastReceiver = null;
        }
    }

    private UpdataBroadcastReceiver updataroadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;

    /**
     * 2015-11-5
     */
    private class UpdataBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IntentKey.NOTIFY_UPLOAD_IMAGE_SUCCESS.equals(intent.getAction())) {
                listViewAdapter.notifyDataSetChanged();
                refreshLayout.doRefreshing();
            } else if (IntentKey.NOTIFY_UPLOAD_IMAGE_FAILED.equals(intent.getAction())) {
                listViewAdapter.notifyDataSetChanged();
            }
        }
    }
}
