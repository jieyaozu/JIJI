package com.yaozu.object.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.yaozu.object.R;
import com.yaozu.object.adapter.GroupListAdapter;
import com.yaozu.object.entity.GroupReqData;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by jxj42 on 2017/2/5.
 */

public class GroupFragment extends BaseFragment implements View.OnClickListener {
    public static String TAG = "GroupFragment";
    private RecyclerView mRecyclerView;
    private int currentPage = 1;
    private HeaderViewRecyclerAdapter stringAdapter;
    private GroupListAdapter listViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ImageView ivCreateGroup;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAdapter = new GroupListAdapter(this.getActivity());
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        stringAdapter = new HeaderViewRecyclerAdapter(listViewAdapter);
        mRecyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter(linearLayoutManager, stringAdapter);

        refreshLayout.doRefreshing();
        refreshLayout.setIsCanLoad(false);
    }

    @Override
    protected void onIRefresh() {
        super.onIRefresh();
        currentPage = 1;
        requestGetMyGroup(LoginInfo.getInstance(getActivity()).getUserAccount());
    }

    @Override
    protected void onILoad() {
        super.onILoad();
        currentPage++;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.common_refresh_recyclerview);
        ivCreateGroup = (ImageView) view.findViewById(R.id.fragment_create_group);

        ivCreateGroup.setOnClickListener(this);
        return view;
    }

    private void requestGetMyGroup(String userid) {
        String url = DataInterface.FIND_MY_GROUP + "userid=" + userid;
        RequestManager.getInstance().getRequest(getActivity(), url, GroupReqData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                refreshLayout.completeRefresh();
                if (object != null) {
                    GroupReqData groupReqData = (GroupReqData) object;
                    listViewAdapter.clearData();
                    listViewAdapter.addData(groupReqData.getBody().getGrList());
                }
            }

            @Override
            public void onFailure(int code, String message) {
                refreshLayout.completeRefresh();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_create_group:
                IntentUtil.toCreatGroupActivity(this.getActivity());
                break;
        }
    }
}
