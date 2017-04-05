package com.yaozu.object.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.bean.GroupBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/3/5.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.MyViewHolder> {
    private Context mContext;
    private List<GroupBean> groupBeanList = new ArrayList<>();

    public GroupListAdapter(Context context) {
        mContext = context;
    }

    public void clearData() {
        if (groupBeanList != null) {
            groupBeanList.clear();
        }
    }

    public void addData(List<GroupBean> groupList) {
        if (groupList != null) {
            groupBeanList.addAll(groupList);
            notifyDataSetChanged();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_group_listview, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GroupBean groupBean = groupBeanList.get(position);
        holder.tvGroupName.setText(groupBean.getGroupname());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return groupBeanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layout;
        private TextView tvGroupName;
        private ImageView ivGroupIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.item_group_layout);
            tvGroupName = (TextView) itemView.findViewById(R.id.item_groupname);
            ivGroupIcon = (ImageView) itemView.findViewById(R.id.item_group_icon);
        }
    }

}
