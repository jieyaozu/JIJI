package com.yaozu.object.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yaozu.object.R;
import com.yaozu.object.widget.RefreshLayout;

/**
 * Created by jxj42 on 2017/2/12.
 */

public class BaseFragment extends Fragment {
    protected RefreshLayout refreshLayout;
    protected Typeface typeface;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = (RefreshLayout) view.findViewById(R.id.common_refresh);
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
    }

    protected void onIRefresh() {

    }

    protected void onILoad() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/zhunyuan.ttf");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
