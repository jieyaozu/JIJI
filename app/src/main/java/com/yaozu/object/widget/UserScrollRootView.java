package com.yaozu.object.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.yaozu.object.R;

/**
 * 整个页面除了底部tab的父view
 * 为了实现顶部栏动画而创建的
 *
 * @author jieyz create by 2015.6.10
 */
public class UserScrollRootView extends ViewGroup {
    private String TAG = this.getClass().getSimpleName();
    private final Scroller mScroller;
    private final Scroller mChildScroller;
    private final GestureDetector mGestureDetector;
    private final PanelOnGestureListener mGestureListener;
    private final Context mContext;

    //里面的ListView，如果有的话
    public ListView listView;

    public LinearLayout childView;
    public RelativeLayout surface;

    public UserScrollRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mContext = context;
        registerAnimationReceiver();
        mScroller = new Scroller(context);
        mChildScroller = new Scroller(context);
        mGestureListener = new PanelOnGestureListener();
        mGestureDetector = new GestureDetector(mGestureListener);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollRootView);
        final int content_layout = a.getResourceId(R.styleable.ScrollRootView_content_layout, 0);

        final LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = null;
        if (content_layout != 0) {
            contentView = inflater.inflate(content_layout, null, true);
        }
        addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    float downy = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (childView == null || childView.getScrollY() == 0) {
                super.dispatchTouchEvent(ev);
            }
            int scrolly = UserScrollRootView.this.getScrollY();
            scrollUp(scrolly);
            if (childView != null) {
                scrollChildViewUp(childView.getScrollY());
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float dy = ev.getY() - downy;
            if (canChildScrollUp()) {
                super.dispatchTouchEvent(ev);
            } else if (this.getScrollY() == 0 && dy < 0) {
                super.dispatchTouchEvent(ev);
            }
            downy = ev.getY();
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downy = ev.getY();
            super.dispatchTouchEvent(ev);
        }
        return mGestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isLike = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isLike = true;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    /**
     * 判断listview是否滑动到顶部
     *
     * @return
     */
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            return ViewCompat.canScrollVertically(listView, -1) || listView.getScrollY() > 0;
        } else {
            if (listView != null) {
                return ViewCompat.canScrollVertically(listView, -1);
            } else {
                return false;
            }
        }
    }


    public void setListView(ListView listView) {
        this.listView = listView;
    }

    private void scrollUp(int distance) {
        mScroller.startScroll(0, getScrollY(), 0, distance, 200);
        mLastFlingX = UserScrollRootView.this.getScrollY();
        UserScrollRootView.this.post(mScrollRun);
    }

    private void scrollDown(int distance) {
        mScroller.startScroll(0, distance, 0, distance, 200);
        mLastFlingX = UserScrollRootView.this.getScrollY();
        UserScrollRootView.this.post(mScrollRun);
    }

    private int mLastFlingX = 0;
    private final Runnable mScrollRun = new Runnable() {

        @Override
        public void run() {

            if (mScroller.isFinished()) {
                return;
            }
            final boolean more = mScroller.computeScrollOffset();
            final int y = mScroller.getCurrY();
            final int diff = mLastFlingX - y;
            if (diff != 0) {
                UserScrollRootView.this.scrollBy(0, diff);
                mLastFlingX = y;
                //mHitRect.offset(diff, 0f);
                if (scrollRootViewListener != null) {
                    scrollRootViewListener.onScrollChange(UserScrollRootView.this.getScrollX(), UserScrollRootView.this.getScrollY());
                }
            }
            if (more) {
                UserScrollRootView.this.post(mScrollRun);
            }
        }
    };

    private int mChildLastFlingy = 0;

    private void scrollChildViewUp(int distance) {
        mChildScroller.startScroll(0, childView.getScrollY(), 0, distance, 400);
        mChildLastFlingy = childView.getScrollY();
        childView.post(mScrollChildRun);
    }

    private final Runnable mScrollChildRun = new Runnable() {

        @Override
        public void run() {
            if (mChildScroller.isFinished()) {
                return;
            }
            final boolean more = mChildScroller.computeScrollOffset();
            final int y = mChildScroller.getCurrY();
            final int diff = mChildLastFlingy - y;
            if (diff != 0) {
                childView.scrollBy(0, diff);
                updateSurfaceView(diff);
                mChildLastFlingy = y;
            }
            if (more) {
                childView.post(mScrollChildRun);
            }
        }
    };

    private boolean isFirstPress = true;

    public class PanelOnGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            isFirstPress = true;
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        //消除float转int值时的误差，因为scrollBy方法需要传的是int型的值
        float disdy = 0;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //判断当前是否为横屏
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return false;
            }
            boolean isScrollUp = distanceY > 0;
            int distance = (int) (distanceY + disdy);
            disdy = distanceY - (int) distanceY;

            if (isScrollUp) {//向上滑

            } else {//向下滑
                if ((listView == null) || (listView != null && !canChildScrollUp())) {
                    if (UserScrollRootView.this.getScrollY() + distanceY >= 0) {
                        UserScrollRootView.this.scrollBy(0, distance);
                        setOnScrollListener();
                    } else {
                        distance = -UserScrollRootView.this.getScrollY();
                        UserScrollRootView.this.scrollBy(0, distance);
                        scrollChildView((int) distanceY / 2);
                        if (distance != 0) {
                            setOnScrollListener();
                        }
                    }
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            return true;
        }

        private void setOnScrollListener() {
            if (scrollRootViewListener != null) {
                scrollRootViewListener.onScrollChange(UserScrollRootView.this.getScrollX(), UserScrollRootView.this.getScrollY());
            }
        }
    }

    private void scrollChildView(int distance) {
        if (childView == null) {
            childView = (LinearLayout) UserScrollRootView.this.findViewById(R.id.scroll_child_view);
            surface = (RelativeLayout) UserScrollRootView.this.findViewById(R.id.surface_container);
        }
        updateSurfaceView(distance);
        childView.scrollBy(0, distance);
    }

    private void updateSurfaceView(int distance) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) surface.getLayoutParams();
        params.height = params.height - distance;
        surface.setLayoutParams(params);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childrenCount = getChildCount();
        for (int i = 0; i < childrenCount; ++i) {
            final View v = getChildAt(i);
            if (v != null) {
                v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        final int childrenCount = getChildCount();
        for (int i = 0; i < childrenCount; ++i) {
            final View v = getChildAt(i);
            final int contentHeight = height;
            v.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void registerAnimationReceiver() {
        if (mScrollReceiver == null) {
            mScrollReceiver = new ScrollBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("");
            filter.addAction("");
            LocalBroadcastManager.getInstance(mContext).registerReceiver(mScrollReceiver, filter);
        }
    }

    public void unRegisterAnimationReceiver() {
        if (mScrollReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mScrollReceiver);
            mScrollReceiver = null;
        }
    }

    /**
     * 消息接受类
     */
    private ScrollBroadcastReceiver mScrollReceiver;

    /**
     * 类描述： 用来接收整个布局上下滚动的指令的广播接收者
     * 创建人： 揭耀祖
     * 创建时间： 2015-06-10
     */
    private class ScrollBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
        }
    }

    private ScrollRootViewListener scrollRootViewListener;

    public interface ScrollRootViewListener {
        public void onScrollChange(int scrollx, int scrolly);
    }

    public ScrollRootViewListener getScrollRootViewListener() {
        return scrollRootViewListener;
    }

    public void setScrollRootViewListener(ScrollRootViewListener scrollRootViewListener) {
        this.scrollRootViewListener = scrollRootViewListener;
    }
}
