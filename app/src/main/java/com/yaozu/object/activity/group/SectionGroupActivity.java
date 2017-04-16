package com.yaozu.object.activity.group;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.adapter.SectionGroupAdapter;
import com.yaozu.object.entity.GroupReqListData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by jxj42 on 2017/4/15.
 */

public class SectionGroupActivity extends BaseActivity {
    private HeaderViewRecyclerAdapter stringAdapter;
    private SectionGroupAdapter sectionGroupAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String mSectionid;
    private String mSectionName;
    private int currentPage = 1;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_section_group);
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.common_refresh_recyclerview);
    }

    @Override
    protected void onIRefresh() {
        super.onIRefresh();
        currentPage = 1;
        requestGroupList(mSectionid);
    }

    @Override
    protected void onILoad() {
        super.onILoad();
        currentPage++;
        requestGroupList(mSectionid);
    }

    @Override
    protected void initData() {
        mSectionid = getIntent().getStringExtra(IntentKey.INTENT_SECTION_ID);
        mSectionName = getIntent().getStringExtra(IntentKey.INTENT_SECTION_NAME);

        sectionGroupAdapter = new SectionGroupAdapter(this);
        stringAdapter = new HeaderViewRecyclerAdapter(sectionGroupAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter(linearLayoutManager, stringAdapter);

        refreshLayout.doRefreshing();
    }

    @Override
    protected void setListener() {

    }

    private void requestGroupList(String sectionid) {
        String url = DataInterface.FIND_SECTION_OF_GROUP + "sectionid=" + sectionid;
        RequestManager.getInstance().getRequest(this, url, GroupReqListData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                refreshLayout.completeRefresh();
                if (object != null) {
                    GroupReqListData groupReqListData = (GroupReqListData) object;
                    sectionGroupAdapter.setDataList(groupReqListData.getBody().getGrList());
                }
            }

            @Override
            public void onFailure(int code, String message) {
                refreshLayout.completeRefresh();
            }
        });
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle(mSectionName + " 相关的群");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
