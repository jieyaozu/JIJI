package com.yaozu.object;

import android.support.v7.app.ActionBar;

import com.yaozu.object.activity.BaseActivity;

public class SettingsActivity2 extends BaseActivity {

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_settings2);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("设置");
        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);
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
}
