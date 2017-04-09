package com.yaozu.object.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.yaozu.object.R;
import com.yaozu.object.adapter.GroupListAdapter;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.db.dao.GroupDao;
import com.yaozu.object.entity.GroupReqListData;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.widget.stickylistheaders.StickyListHeadersListView;

import java.util.List;

/**
 * Created by jxj42 on 2017/2/5.
 */

public class GroupFragment extends BaseFragment implements View.OnClickListener {
    public static String TAG = "GroupFragment";
    public StickyListHeadersListView listView;
    private int currentPage = 1;
    private GroupListAdapter listViewAdapter;
    private ImageView ivCreateGroup;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GroupDao groupDao;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        groupDao = new GroupDao(this.getActivity());
        listViewAdapter = new GroupListAdapter(this.getActivity());
        listView.setAdapter(listViewAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestGetMyGroup(LoginInfo.getInstance(getActivity()).getUserAccount());
            }
        });
        List<GroupBean> groupBeanList = groupDao.findAllMyGroup();
        if (groupBeanList != null && groupBeanList.size() > 0) {
            listViewAdapter.addData(groupBeanList);
        } else {
            requestGetMyGroup(LoginInfo.getInstance(getActivity()).getUserAccount());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        listView = (StickyListHeadersListView) view.findViewById(R.id.fragment_group_listview);
        ivCreateGroup = (ImageView) view.findViewById(R.id.fragment_create_group);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_fresh);

        ivCreateGroup.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.IS_CREATEGROUP_SUCCESS) {
            Constant.IS_CREATEGROUP_SUCCESS = false;
            requestGetMyGroup(LoginInfo.getInstance(getActivity()).getUserAccount());
            Toast.makeText(this.getActivity(), "创建群成功了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 查询我的群组
     *
     * @param userid
     */
    private void requestGetMyGroup(String userid) {
        String url = DataInterface.FIND_MY_GROUP + "userid=" + userid;
        RequestManager.getInstance().getRequest(getActivity(), url, GroupReqListData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                swipeRefreshLayout.setRefreshing(false);
                if (object != null) {
                    GroupReqListData groupReqData = (GroupReqListData) object;
                    listViewAdapter.clearData();
                    listViewAdapter.addData(groupReqData.getBody().getGrList());
                    //清空数据库
                    groupDao.clearTable();
                    //插入数据库
                    for (GroupBean groupBean : groupReqData.getBody().getGrList()) {
                        groupDao.addGroup(groupBean);
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {
                swipeRefreshLayout.setRefreshing(false);
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
