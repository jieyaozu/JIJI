package com.yaozu.object.activity.setting;

import android.support.v7.app.ActionBar;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;

/**
 * Created by jieyaozu on 2017/4/27.
 */

public class SettingActivity extends BaseActivity {
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("设置");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
