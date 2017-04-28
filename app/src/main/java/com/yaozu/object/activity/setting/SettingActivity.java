package com.yaozu.object.activity.setting;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.utils.FileUtil;
import com.yaozu.object.utils.IntentUtil;

import java.math.BigDecimal;

/**
 * Created by jieyaozu on 2017/4/27.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvAccoutnSet, tvClearCache, tvCheckUpdate, tvAbout;
    private TextView tvCacheValue;

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
        tvCacheValue = (TextView) findViewById(R.id.setting_system_clearcache_value);
    }

    @Override
    protected void initData() {
        calculateCacheSize();
    }

    /**
     * 计算缓存大小并展示
     */
    private void calculateCacheSize() {
        float size = (float) FileUtil.getAppCacheSize(this) / (1024.0f * 1024.0f);
        if (size < 1) {
            tvCacheValue.setText(FileUtil.getAppCacheSize(this) / 1024 + " Kb");
        } else {
            BigDecimal bd = new BigDecimal(size);
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            tvCacheValue.setText(bd.floatValue() + " MB");
        }
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

    private void showCleanCacheDialog() {
        new MaterialDialog.Builder(this)
                .backgroundColorRes(R.color.colorWhite)
                .contentColorRes(R.color.gray)
                .content("确定清除缓存吗？")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        FileUtil.cleanAppCache(SettingActivity.this);
                        calculateCacheSize();
                    }
                })
                .negativeText("取消")
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_account_set:
                IntentUtil.toUserSettingActivity(this);
                break;
            case R.id.setting_system_clearcache:
                showCleanCacheDialog();
                break;
            case R.id.setting_checkupdate:
                break;
            case R.id.setting_about:
                break;
        }
    }
}
