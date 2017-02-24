package com.yaozu.object.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.yaozu.object.R;

/**
 * 继承自SwipeRefreshLayout,从而实现滑动到底部时上拉加载更多的功能.
 *
 * @author mrsimple
 */
public class RefreshLayout extends SwipeRefreshLayout {

    /**
     * 滑动到最下面时的上拉操作
     */

    private int mTouchSlop;
    /**
     * listview实例
     */
    private RecyclerView mRecyclerView;

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;

    /**
     * ListView的加载中footer
     */
    private View mListViewFooter;

    private View mListViewFooterRl;

    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;
    private boolean isCanLoad = true;
    private LinearLayoutManager layoutManager;

    /**
     * @param context
     */
    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null,
                false);
        mListViewFooterRl = mListViewFooter.findViewById(R.id.listview_footer_rl);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 初始化ListView对象
        if (mRecyclerView == null) {
            getListView();
        }
    }

    int lastVisibleItem;

    /**
     * 获取ListView对象
     */
    private void getListView() {
        int childs = getChildCount();
        if (childs > 0) {
            for (int i = 0; i < childs; i++) {
                View childView = getChildAt(i);
                if (childView instanceof RecyclerView) {
                    mRecyclerView = (RecyclerView) childView;
                    // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                    mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == recyclerView.getAdapter().getItemCount()) {
                                if (canLoad()) {
                                    loadData();
                                }
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                            if (layoutManager != null) {
                                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                                if (lastVisibleItem + 1 == recyclerView.getAdapter().getItemCount()) {
                                    if (canLoad()) {
                                        loadData();
                                    }
                                }
                            }
                        }
                    });
                    Log.d(VIEW_LOG_TAG, "### 找到RecyclerView");
                }
            }
        }
    }

    public void setIsCanLoad(boolean isCanLoad) {
        this.isCanLoad = isCanLoad;
    }

    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                // 抬起
//                if (canLoad() && isOverLayout) {
//                    loadData();
//                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return
     */
    private boolean canLoad() {
        return !isRefreshing() && !isLoading && isPullUp() && isCanLoad;
    }

    public void completeRefresh() {
        setRefreshing(false);
        setLoading(false);
    }

    OnRefreshListener mListener;

    @Override
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
        super.setOnRefreshListener(listener);
    }

    public void doRefreshing() {
        setRefreshing(true);
        if (mListener != null) {
            mListener.onRefresh();
        }
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {

        return false;
    }

    /**
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        System.out.println("==========onload=============>");
        if (mOnLoadListener != null) {
            // 设置状态
            setLoading(true);
            //
            mOnLoadListener.onLoad();
        }
    }

    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            if (mListViewFooterRl != null) {
                mListViewFooterRl.setVisibility(VISIBLE);
            }
        } else {
            mYDown = 0;
            mLastY = 0;
        }
    }

    /**
     * @param loadListener
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//    }
//
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
//                         int totalItemCount) {
//        // 滚动时到了最底部也可以加载更多
//        if (visibleItemCount < totalItemCount && canLoad()) {
//            isOverLayout = true;
//            //loadData();
//        } else {
//            isOverLayout = false;
//        }
//    }

    private boolean isOverLayout = false;

    /**
     * 加载更多的监听器
     *
     * @author mrsimple
     */
    public static interface OnLoadListener {
        public void onLoad();
    }
}

