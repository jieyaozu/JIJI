package com.yaozu.object.activity;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yaozu.object.MainActivity;
import com.yaozu.object.R;
import com.yaozu.object.widget.RefreshLayout;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * Created by jxj42 on 2016/12/21.
 */

public abstract class BaseActivity extends AppCompatActivity implements SwipeBackActivityBase {
    protected RefreshLayout refreshLayout;
    protected boolean swipeBackEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();

        setContentView();
        refreshLayout = (RefreshLayout) findViewById(R.id.common_refresh);
        if (refreshLayout != null) {
            refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),
                    getResources().getColor(R.color.colorPrimaryDark),
                    getResources().getColor(R.color.colorPrimary));
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onIRefresh();
                }
            });
            refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
                @Override
                public void onLoad() {
                    onILoad();
                }
            });
        }
        initView();
        initData();
        setListener();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.common_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        settingActionBar(ab);
    }

    protected void onIRefresh() {

    }

    protected void onILoad() {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract void setContentView();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void setListener();

    protected abstract void settingActionBar(ActionBar actionBar);

    @Override
    public void finish() {
        super.finish();
        if (this instanceof MainActivity) {
            return;
        }
        if (swipeBackEnable) {
            overridePendingTransition(R.anim.right_enter_page, R.anim.right_quit_page);
        }
    }

    private SwipeBackActivityHelper mHelper;

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        swipeBackEnable = enable;
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    private ProgressDialog progressDialog;

    protected void showBaseProgressDialog(String message) {
        if ((!isFinishing()) && (this.progressDialog == null)) {
            this.progressDialog = new ProgressDialog(this);
        }
        this.progressDialog.setMessage(message);
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
    }

    protected void closeBaseProgressDialog() {
        if (this.progressDialog != null)
            this.progressDialog.dismiss();
    }
}
