package com.yaozu.object.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.yaozu.object.R;
import com.yaozu.object.adapter.ForumListViewAdapter;
import com.yaozu.object.bean.Post;
import com.yaozu.object.entity.HomeForumDataInfo;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.widget.FloatingActionButton;
import com.yaozu.object.widget.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/5.
 */

public class ForumFragment extends BaseFragment implements View.OnClickListener {
    public static String TAG = "ForumFragment";
    private RecyclerView mListView;
    private ForumListViewAdapter listViewAdapter;
    private View mViewHeader;
    private NoScrollListView mHeaderListView;
    private HeaderListViewAdapter mHeaderAdapter;
    private FloatingActionButton ivButton;
    private int currentPage = 1;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAdapter = new ForumListViewAdapter(this.getActivity());
        mListView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setAdapter(listViewAdapter);

        mHeaderAdapter = new HeaderListViewAdapter();
        mHeaderListView.setAdapter(mHeaderAdapter);

        ivButton.setOnClickListener(this);
        refreshLayout.doRefreshing();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        mListView = (RecyclerView) view.findViewById(R.id.common_refresh_recyclerview);
        mViewHeader = inflater.inflate(R.layout.header_list_forum, null);
        ivButton = (FloatingActionButton) view.findViewById(R.id.fragment_forum_imageview);

        mHeaderListView = (NoScrollListView) mViewHeader.findViewById(R.id.header_list_forum_listview);
        return view;
    }

    @Override
    protected void onIRefresh() {
        currentPage = 1;
        refreshLayout.setIsCanLoad(true);
        requestPostList(currentPage);
    }

    @Override
    protected void onILoad() {
        currentPage++;
        requestPostList(currentPage);
    }

    private void requestPostList(final int pageIndex) {
        String url = DataInterface.FIND_HOME_POST_LIST + "pageindex=" + pageIndex;
        RequestManager.getInstance().getRequest(this.getActivity(), url, HomeForumDataInfo.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                refreshLayout.completeRefresh();
                if (object != null) {
                    HomeForumDataInfo postDataInfo = (HomeForumDataInfo) object;
                    if (pageIndex == 1) {
                        listViewAdapter.clearData();
                        mHeaderAdapter.setData(postDataInfo.getBody().getToppostlist());
                    }
                    List<Post> postList = postDataInfo.getBody().getPostlist();
                    listViewAdapter.addData(postList);
                    if (postList == null || postList.size() == 0) {
                        refreshLayout.setIsCanLoad(false);
                    }
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
            case R.id.fragment_forum_imageview:
                if (LoginInfo.getInstance(this.getActivity()).isLogining()) {
                    IntentUtil.toSendPostActivity(this.getActivity());
                } else {
                    IntentUtil.toLoginActivity(this.getActivity());
                }
                break;
        }
    }

    public class HeaderListViewAdapter extends BaseAdapter {
        private List<Post> mDataList = new ArrayList<Post>();

        @Override
        public int getCount() {
            return mDataList.size();
        }

        public void setData(List<Post> datas) {
            if (datas != null) {
                mDataList.clear();
                mDataList.addAll(datas);
                notifyDataSetChanged();
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(ForumFragment.this.getActivity(), R.layout.item_listview_header_forum, null);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostDetailActivity(ForumFragment.this.getActivity(), new Post());
                }
            });
            return view;
        }
    }
}
