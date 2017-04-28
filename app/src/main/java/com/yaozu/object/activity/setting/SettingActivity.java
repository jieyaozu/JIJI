package com.yaozu.object.activity.setting;

import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;

/**
 * Created by jieyaozu on 2017/4/27.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvAccoutnSet, tvClearCache, tvCheckUpdate, tvAbout;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void initView() {
        tvAccoutnSet = (TextView) findViewById(R.id.setting_account_set);
        tvClearCache = (TextView) findViewById(R.id.setting_system_clearcache);
        tvCheckUpdate = (TextView) findViewById(R.id.setting_checkupdate);
        tvAbout = (TextView) findViewById(R.id.setting_about);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        tvAccoutnSet.setOnClickListener(this);
        tvClearCache.setOnClickListener(this);
        tvCheckUpdate.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("设置");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_account_set:
                break;
            case R.id.setting_system_clearcache:
                break;
            case R.id.setting_checkupdate:
                break;
            case R.id.setting_about:
                break;
        }
    }
}
