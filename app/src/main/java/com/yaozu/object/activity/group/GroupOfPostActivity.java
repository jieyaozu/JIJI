package com.yaozu.object.activity.group;

import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.utils.IntentKey;

/**
 * Created by jieyaozu on 2017/4/5.
 * 展示群内所有帖子的页面
 */

public class GroupOfPostActivity extends BaseActivity {
    private ActionBar mActionBar;
    private GroupBean groupBean;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_group_of_post);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        groupBean = (GroupBean) getIntent().getSerializableExtra(IntentKey.INTENT_GROUP);
    }

    @Override
    protected void setListener() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_of_post_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_groupdetail:
                showToast("群详情");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        mActionBar = actionBar;
        actionBar.setTitle(groupBean.getGroupname());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
