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

import com.yaozu.object.R;
import com.yaozu.object.adapter.ArticleListAdapter;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by jxj42 on 2017/2/5.
 */

public class ArticleFragment extends BaseFragment {
    public static String TAG = "ArticleFragment";
    private RecyclerView mRecyclerView;
    private int currentPage = 1;
    private HeaderViewRecyclerAdapter stringAdapter;
    private ArticleListAdapter listViewAdapter;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewAdapter = new ArticleListAdapter(this.getActivity());
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        stringAdapter = new HeaderViewRecyclerAdapter(listViewAdapter);
        mRecyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter(linearLayoutManager, stringAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.common_refresh_recyclerview);
        return view;
    }
}
