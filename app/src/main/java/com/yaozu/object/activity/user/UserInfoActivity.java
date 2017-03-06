package com.yaozu.object.activity.user;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.adapter.UserinfoPagerAdapter;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.entity.UserInfoData;
import com.yaozu.object.fragment.ReplyFragment;
import com.yaozu.object.fragment.ThemeFragment;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by jieyaozu on 2017/2/22.
 */

public class UserInfoActivity extends BaseActivity {
    private String userid;
    private ImageView ivUserIcon;
    private TextView tvUserName;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ViewPager viewPager;
    private UserinfoPagerAdapter pagerAdapter;
    private RelativeLayout stickyView;
    public static int STICKY_HEIGHT1; // height1是代表从顶部到tab的距离
    public static int STICKY_HEIGHT2; // height2是代表从顶部到viewpager的距离
    private StickyScrollCallBack scrollListener;
    private ThemeFragment detailFragment1;
    private ReplyFragment detailFragment2;
    private UserInfoData userInfoData;
    private int accountType = -1;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_userinfo);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("返回");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userinfo_actions, menu);
        if (!userid.equals(LoginInfo.getInstance(this).getUserAccount()) && "2".equals(LoginInfo.getInstance(this).getAccountType())) {
            menu.findItem(R.id.action_change_accounttype).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_accounttype:
                showChangeTypeDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initView() {
        userid = getIntent().getStringExtra(IntentKey.INTENT_USERID);
        ivUserIcon = (ImageView) findViewById(R.id.activity_userinfo_usericon);
        tvUserName = (TextView) findViewById(R.id.activity_userinfo_username);
        viewPager = (ViewPager) findViewById(R.id.userinfo_viewpager);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.userinfo_psts);
        pagerSlidingTabStrip.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        stickyView = (RelativeLayout) findViewById(R.id.sticky_view);
        stickyView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        STICKY_HEIGHT2 = stickyView.getMeasuredHeight();
        STICKY_HEIGHT1 = STICKY_HEIGHT2 - pagerSlidingTabStrip.getMeasuredHeight();

        System.out.println("LeiTest" + "height1=" + STICKY_HEIGHT1 + " height2=" + STICKY_HEIGHT2);

        // 这是一个回调 滚动条滑动的时候，会调用onScrollChanged函数
        scrollListener = new StickyScrollCallBack() {

            @Override
            public void onScrollChanged(int scrollY) {
                processStickyTranslateY(scrollY);
            }

            @Override
            public int getCurrentViewpagerItem() {
                return viewPager.getCurrentItem();
            }
        };

        requestFindUserinfo(userid);
    }

    @Override
    protected void initData() {
        List<String> titles = new ArrayList<String>();
        titles.add("主题");
        titles.add("跟贴");
        List<Fragment> fragments = new ArrayList<>();
        detailFragment1 = ThemeFragment.newInstance(userid);
        detailFragment2 = ReplyFragment.newInstance(userid);
        fragments.add(detailFragment1);
        fragments.add(detailFragment2);
        detailFragment1.setScrollCallBack(scrollListener);
        detailFragment2.setScrollCallBack(scrollListener);
        pagerAdapter = new UserinfoPagerAdapter(getSupportFragmentManager(), titles, fragments);

        viewPager.setAdapter(pagerAdapter);
        pagerSlidingTabStrip.setViewPager(viewPager);

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    if (scrollListener.getCurrentViewpagerItem() == 0) {
                        int tempH1 = detailFragment1.getStickyHeight();
                        int stickyH2 = 0;
                        if (tempH1 > STICKY_HEIGHT1 / 2) {
                            stickyH2 = STICKY_HEIGHT1;
                        }
                        detailFragment2.setStickyH(stickyH2);
                    } else if (scrollListener.getCurrentViewpagerItem() == 1) {
                        int tempH2 = detailFragment2.getStickyHeight();
                        int stickyH1 = 0;
                        if (tempH2 > STICKY_HEIGHT1 / 2) {
                            stickyH1 = STICKY_HEIGHT1;
                        }
                        detailFragment1.setStickyH(stickyH1);
                    }
                }
            }
        };
        viewPager.addOnPageChangeListener(pageChangeListener);
        pagerSlidingTabStrip.setViewPagerStateListener(pageChangeListener);
    }

    @Override
    protected void setListener() {

    }

    private void requestFindUserinfo(String userid) {
        String url = DataInterface.FIND_USERINFO + "userid=" + userid;
        RequestManager.getInstance().getRequest(this, url, UserInfoData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    userInfoData = (UserInfoData) object;
                    accountType = userInfoData.getBody().getUserinfo().getType();
                    tvUserName.setText(userInfoData.getBody().getUserinfo().getUsername());
                    Utils.setUserImg(userInfoData.getBody().getUserinfo().getIconpath(), ivUserIcon);
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    MaterialDialog materialDialog;
    String userType = "";

    private void showChangeTypeDialog() {
        View contentView = View.inflate(this, R.layout.dialog_change_accouttype_contentview, null);
        materialDialog = new MaterialDialog(this)
                .setTitle("更改账户类型")
                .setContentView(contentView)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        requestChangeAccountType(userid, userType);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
        RadioGroup radioGroup = (RadioGroup) contentView.findViewById(R.id.accounttype_rg);
        RadioButton normal = (RadioButton) contentView.findViewById(R.id.accounttype_normal);
        RadioButton postmain = (RadioButton) contentView.findViewById(R.id.accounttype_postmain);
        if (0 == accountType) {
            userType = "0";
            normal.setChecked(true);
        } else if (1 == accountType) {
            userType = "1";
            postmain.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.accounttype_normal:
                        userType = "0";
                        break;
                    case R.id.accounttype_postmain:
                        userType = "1";
                        break;
                }
            }
        });
        materialDialog.show();
    }

    /**
     * 更改账户类型
     *
     * @param userid
     * @param type
     */
    private void requestChangeAccountType(String userid, final String type) {
        String url = DataInterface.CHANGE_ACCOUNT_TYPE + "userid=" + userid + "&type=" + type;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if ("1".equals(requestData.getBody().getCode())) {
                        accountType = Integer.parseInt(type);
                    }
                    showToast(requestData.getBody().getMessage());
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    private int lastProcessStickyTranslateY = 0;

    @SuppressLint("NewApi")
    private void processStickyTranslateY(int translateY) {
        if (translateY == Integer.MIN_VALUE || translateY == lastProcessStickyTranslateY) {
            return;
        }
        lastProcessStickyTranslateY = translateY;
        stickyView.setTranslationY(translateY);

/*        if (navBottomPos == 0 || locTvTopPosY == 0) {
            locTvTopPosX = titleLocTv.getLeft();
            locTvTopPosY = titleLocTv.getTop();
            navBottomPos = navLayout.getBottom();
            titleHeight = titleLocTv.getMeasuredHeight();
        }

        int locationX = location[0];

        int locationY = location[1] - PixValue.dip.valueOf(50)
                + notifyBarHeight;
        if (locationY < locTvTopPosY) {
            locationY = locTvTopPosY;
        }

        if (locationY < navBottomPos - titleHeight) {
            locationX = locationX
                    - ((navBottomPos - titleHeight - locationY) * 2);
            if (locationX < locTvTopPosX) {
                locationX = locTvTopPosX;
            }
        }

        ViewHelper.setX(maskTv, locationX);
        ViewHelper.setY(maskTv, locationY);*/
    }

    public interface StickyScrollCallBack {
        public void onScrollChanged(int scrollY);

        public int getCurrentViewpagerItem();
    }

}
