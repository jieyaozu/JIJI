package com.yaozu.object.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import com.yaozu.object.R;
import com.yaozu.object.activity.user.UserInfoActivity;
import com.yaozu.object.activity.user.UserInfoActivity.StickyScrollCallBack;

@SuppressLint({"NewApi", "HandlerLeak"})
public class StickyListView extends ListView {
    private StickyScrollCallBack scrollCallBack;
    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;
    private int mYDown;
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;
    private boolean isCanLoad = true;
    /**
     * 滑动到最下面时的上拉操作
     */
    private int mTouchSlop;
    private final View mListViewFooter;
    private View mListViewFooterRl;

    public StickyListView(Context context) {
        this(context, null);
    }

    public StickyListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null, false);
        mListViewFooterRl = mListViewFooter.findViewById(R.id.listview_footer_rl);
        this.addFooterView(mListViewFooter);
        this.setOverScrollMode(OVER_SCROLL_ALWAYS);
        this.setOnScrollListener(mOnScrollListener);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

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
                if (canLoad() && isOverLayout) {
                    loadData();
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    public void setScrollCallBack(StickyScrollCallBack scrollCallBack) {
        this.scrollCallBack = scrollCallBack;
    }

    /**
     * @param loadListener
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    private final OnScrollListener mOnScrollListener = new OnScrollListener() {

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (null == scrollCallBack) {
                return;
            }
            // 滚动时到了最底部也可以加载更多
            if (visibleItemCount < totalItemCount && canLoad()) {
                isOverLayout = true;
                loadData();
            } else {
                isOverLayout = false;
            }

            if (firstVisibleItem == 0) {
                View firstView = getChildAt(0);
                if (null != firstView) {
                    int firstTop = firstView.getTop();
                    if (firstTop < -UserInfoActivity.STICKY_HEIGHT1) {
                        firstTop = -UserInfoActivity.STICKY_HEIGHT1;
                    }
                    scrollCallBack.onScrollChanged(firstTop);
                }
            } else if (firstVisibleItem < 6) {
                scrollCallBack.onScrollChanged(-UserInfoActivity.STICKY_HEIGHT1);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.i("LeiTest", "onScrollStateChanged=" + scrollState);

            // scrollState=0 ��?������
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                animScrollY();
            }
        }
    };

    private void animScrollY() {
        int offsetDistance = 0, firstTop = 0;
        if (getFirstVisiblePosition() == 0) {
            View firstView = getChildAt(0);
            if (firstView != null) {
                firstTop = firstView.getTop();
                if (firstTop < -UserInfoActivity.STICKY_HEIGHT1 / 2) {
                    offsetDistance = -UserInfoActivity.STICKY_HEIGHT1;
                }
            }

            if (firstTop != offsetDistance) {
                new AnimUiThread(firstTop, offsetDistance).start();
            }
        }
    }

    public void invalidScroll() {

    }

    public int getFirstViewScrollTop() {
        if (getFirstVisiblePosition() == 0) {
            View firstView = getChildAt(0);
            if (null != firstView) {
                return -firstView.getTop();
            }
        }
        return Integer.MAX_VALUE;
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
     * 判断是否到了最底部
     */
    private boolean isBottom() {
        if (this.getAdapter() != null) {
            return this.getLastVisiblePosition() == (this.getAdapter().getCount() - 1);
        }
        return false;
    }

    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return
     */
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp() && isCanLoad;
    }

    private void loadData() {
        if (mOnLoadListener != null) {
            // 设置状态
            setLoading(true);
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
            mListViewFooterRl.setVisibility(GONE);
            mYDown = 0;
            mLastY = 0;
        }
    }

    private boolean isOverLayout = false;

    class AnimUiThread extends Thread {
        private int fromPos, toPos;

        public AnimUiThread(int fromPos, int toPos) {
            this.fromPos = fromPos;
            this.toPos = toPos;
        }

        @Override
        public void run() {
            int num = 10; // ʮ��ѭ����λ
            for (int i = 0; i < num; i++) {
                try {
                    sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int tempPos = fromPos + (toPos - fromPos) * (i + 1) / num;
                Message msg = uiHandler.obtainMessage();
                msg.what = tempPos;
                msg.sendToTarget();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        uiHandler = null;
        super.onDetachedFromWindow();
    }

    private Handler uiHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int pos = msg.what;
            setSelectionFromTop(0, pos);
        }
    };

    /**
     * 加载更多的监听器
     *
     * @author mrsimple
     */
    public static interface OnLoadListener {
        public void onLoad();
    }
}
