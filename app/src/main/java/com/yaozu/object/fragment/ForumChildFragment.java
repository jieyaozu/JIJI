package com.yaozu.object.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.adapter.ForumListViewAdapter;
import com.yaozu.object.bean.Post;
import com.yaozu.object.entity.HomeForumDataInfo;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.widget.FloatingActionButton;
import com.yaozu.object.widget.NoScrollListView;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/5.
 */

public class ForumChildFragment extends BaseFragment implements View.OnClickListener {
    public static String TAG = "ForumChildFragment";
    private RecyclerView mRecyclerView;
    private ForumListViewAdapter listViewAdapter;
    private View mViewHeader;
    private NoScrollListView mHeaderListView;
    private HeaderListViewAdapter mHeaderAdapter;
    private FloatingActionButton ivButton;
    private int currentPage = 1;
    private HeaderViewRecyclerAdapter stringAdapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView progressTextView;

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAdapter = new ForumListViewAdapter(this.getActivity());
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        stringAdapter = new HeaderViewRecyclerAdapter(listViewAdapter);
        stringAdapter.addHeaderView(mViewHeader);
        mRecyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter(linearLayoutManager, stringAdapter);

        mHeaderAdapter = new HeaderListViewAdapter();
        mHeaderListView.setAdapter(mHeaderAdapter);

        ivButton.setOnClickListener(this);
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.doRefreshing();
            }
        }, 500);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.common_refresh_recyclerview);
        mViewHeader = inflater.inflate(R.layout.header_list_forum, null);
        ivButton = (FloatingActionButton) view.findViewById(R.id.fragment_forum_imageview);
        progressTextView = (TextView) view.findViewById(R.id.progressbar);

        mHeaderListView = (NoScrollListView) mViewHeader.findViewById(R.id.header_list_forum_listview);
        return view;
    }

    @Override
    protected void onIRefresh() {
        currentPage = 1;
        refreshLayout.setIsCanLoad(true);
        requestPostList(currentPage);
    }

    @Override
    protected void onILoad() {
        currentPage++;
        requestPostList(currentPage);
    }

    private void requestPostList(final int pageIndex) {
        String url = DataInterface.FIND_HOME_POST_LIST + "pageindex=" + pageIndex;
        RequestManager.getInstance().getRequest(this.getActivity(), url, HomeForumDataInfo.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                progressTextView.setVisibility(View.GONE);
                refreshLayout.completeRefresh();
                if (object != null) {
                    HomeForumDataInfo postDataInfo = (HomeForumDataInfo) object;
                    if (pageIndex == 1) {
                        listViewAdapter.clearData();
                        mHeaderAdapter.setData(postDataInfo.getBody().getToplist());
                    }
                    List<Post> postList = postDataInfo.getBody().getPostlist();
                    listViewAdapter.addData(postList);
                    if (postList == null || postList.size() == 0) {
                        refreshLayout.setIsCanLoad(false);
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {
                progressTextView.setVisibility(View.GONE);
                refreshLayout.completeRefresh();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_forum_imageview:
                if (LoginInfo.getInstance(this.getActivity()).isLogining()) {
                    IntentUtil.toSendPostActivity(this.getActivity());
                } else {
                    IntentUtil.toLoginActivity(this.getActivity());
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
                view = View.inflate(ForumChildFragment.this.getActivity(), R.layout.item_listview_header_forum, null);
            }
            TextView tvTitle = (TextView) view.findViewById(R.id.home_toppost_title);
            final Post post = mDataList.get(position);
            tvTitle.setText(post.getTitle());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostDetailActivity(ForumChildFragment.this.getActivity(), post.getPostid());
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
}
