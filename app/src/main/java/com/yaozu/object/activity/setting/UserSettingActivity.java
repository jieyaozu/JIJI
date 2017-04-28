package com.yaozu.object.activity.setting;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.utils.Utils;

/**
 * Created by jieyaozu on 2017/4/28.
 */

public class UserSettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvUserName, tvUserNameOld, tvUserIcon;
    private ImageView ivUserIconOld;
    //修改密码
    private TextView tvPwd;
    //登出
    private Button btLoginOut;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_user_setting);
    }

    @Override
    protected void initView() {
        tvUserName = (TextView) findViewById(R.id.user_setting_username);
        tvUserIcon = (TextView) findViewById(R.id.user_setting_usericon);
        tvPwd = (TextView) findViewById(R.id.user_setting_updatepwd);
        tvUserNameOld = (TextView) findViewById(R.id.user_setting_username_old);

        ivUserIconOld = (ImageView) findViewById(R.id.user_setting_usericon_old);
        btLoginOut = (Button) findViewById(R.id.user_setting_loginout);
    }

    @Override
    protected void initData() {
        Utils.setUserImg(LoginInfo.getInstance(this).getSmallIconPath(), ivUserIconOld);
        tvUserNameOld.setText(LoginInfo.getInstance(this).getUserName());
    }

    @Override
    protected void setListener() {
        tvUserName.setOnClickListener(this);
        tvUserIcon.setOnClickListener(this);
        tvPwd.setOnClickListener(this);
        btLoginOut.setOnClickListener(this);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("个人设置");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_setting_username:
                break;
            case R.id.user_setting_usericon:
                break;
            case R.id.user_setting_updatepwd:
                break;
            case R.id.user_setting_loginout:
                showLoginOutDialog();
                break;
        }
    }

    private void showLoginOutDialog() {
        new MaterialDialog.Builder(this)
                .backgroundColorRes(R.color.colorWhite)
                .contentColorRes(R.color.gray)
                .content("确定登出此账户吗？")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                })
                .negativeText("取消")
                .show();
    }
}
