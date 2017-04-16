package com.yaozu.object.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/4/15.
 */

public class SectionGroupAdapter extends RecyclerView.Adapter<SectionGroupAdapter.MyViewHolder> {
    private Context mContext;
    private List<GroupBean> groupBeanList = new ArrayList<>();

    public SectionGroupAdapter(Context context) {
        this.mContext = context;
    }

    public void setDataList(List<GroupBean> groupBeens) {
        if (groupBeens != null) {
            this.groupBeanList.addAll(groupBeens);
            notifyDataSetChanged();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_section_group, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final GroupBean groupBean = groupBeanList.get(position);
        Utils.setUserImg(groupBean.getGroupicon(), holder.ivGroupIcon);
        holder.tvGroupName.setText(groupBean.getGroupname());
        holder.tvIntroduce.setText(groupBean.getIntroduce());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toGroupOfPostActivity(mContext, groupBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupBeanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGroupIcon;
        TextView tvGroupName;
        TextView tvIntroduce;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivGroupIcon = (ImageView) itemView.findViewById(R.id.item_section_groupicon);
            tvGroupName = (TextView) itemView.findViewById(R.id.item_section_groupname);
            tvIntroduce = (TextView) itemView.findViewById(R.id.item_section_introduce);
            this.itemView = itemView;
        }
    }
}
