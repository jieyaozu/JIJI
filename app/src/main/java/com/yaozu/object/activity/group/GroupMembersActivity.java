package com.yaozu.object.activity.group;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.adapter.GroupMembersAdapter;
import com.yaozu.object.entity.GroupMembersData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.widget.stickylistheaders.StickyListHeadersListView;

/**
 * Created by jxj42 on 2017/4/22.
 */

public class GroupMembersActivity extends BaseActivity {
    public StickyListHeadersListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GroupMembersAdapter groupMembersAdapter;
    private String mGroupid;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_group_members);
    }

    @Override
    protected void initView() {
        listView = (StickyListHeadersListView) findViewById(R.id.activity_groupmembers_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_fresh);
    }

    @Override
    protected void initData() {
        mGroupid = getIntent().getStringExtra(IntentKey.INTENT_GROUP_ID);
        groupMembersAdapter = new GroupMembersAdapter(this);
        listView.setAdapter(groupMembersAdapter);
        swipeRefreshLayout.setRefreshing(true);
        requestGetMembers(mGroupid);
    }

    @Override
    protected void setListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestGetMembers(mGroupid);
            }
        });
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("群成员");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.IS_SET_USERTYPE_SUCCESS) {
            Constant.IS_SET_USERTYPE_SUCCESS = false;
            requestGetMembers(mGroupid);
        }
    }

    /**
     * 从服务器上获取成员信息
     *
     * @param groupid
     */
    private void requestGetMembers(String groupid) {
        String url = DataInterface.FIND_GROUP_MEMBERS + "groupid=" + groupid;
        RequestManager.getInstance().getRequest(this, url, GroupMembersData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                swipeRefreshLayout.setRefreshing(false);
                if (object != null) {
                    GroupMembersData groupMembersData = (GroupMembersData) object;
                    groupMembersAdapter.clearData();
                    groupMembersAdapter.setDataList(groupMembersData.getBody().getMemInfos());
                }
            }

            @Override
            public void onFailure(int code, String message) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
