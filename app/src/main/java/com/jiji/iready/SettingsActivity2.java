package com.jiji.iready;

import android.support.v7.app.ActionBar;

import com.jiji.iready.activity.BaseActivity;

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
}
