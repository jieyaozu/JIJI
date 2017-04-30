package com.yaozu.object.activity.user;

import android.support.v7.app.ActionBar;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.widget.polites.GestureImageView;

/**
 * Created by 耀祖 on 2016/1/28.
 */
public class UserIconDetail extends BaseActivity {
    private String iconPath;
    private GestureImageView gestureImageView;
    private String userid;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_usericon_detail);
        setSwipeBackEnable(false);
    }

    @Override
    protected void initView() {
        gestureImageView = (GestureImageView) findViewById(R.id.activity_usericon_detail);
        iconPath = getIntent().getStringExtra(IntentKey.USER_ICON_PATH);
        userid = getIntent().getStringExtra(IntentKey.INTENT_USERID);
        ImageLoader.getInstance().displayImage(iconPath, gestureImageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
        gestureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.usericon_quit_enter_page, R.anim.usericon_scale_quit);
    }
}
