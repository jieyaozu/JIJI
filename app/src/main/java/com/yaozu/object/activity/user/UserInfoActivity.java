package com.yaozu.object.activity.user;

import android.support.v7.app.ActionBar;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.Utils;

/**
 * Created by jieyaozu on 2017/2/22.
 */

public class UserInfoActivity extends BaseActivity {
    private String userid;
    private ImageView ivUserIcon;
    private TextView tvUserName;

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
    protected void initView() {
        userid = getIntent().getStringExtra(IntentKey.INTENT_USERID);
        ivUserIcon = (ImageView) findViewById(R.id.activity_userinfo_usericon);
        tvUserName = (TextView) findViewById(R.id.activity_userinfo_username);

        Utils.setUserImg(LoginInfo.getInstance(this).getIconPath(), ivUserIcon);
        tvUserName.setText(LoginInfo.getInstance(this).getUserName());
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }
}
