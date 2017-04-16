package com.yaozu.object.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

public class SearchGroupAdapter extends RecyclerView.Adapter<SearchGroupAdapter.MyViewHolder> {
    private Context mContext;
    private List<GroupBean> groupBeanList = new ArrayList<>();
    private String keywords;

    public SearchGroupAdapter(Context context) {
        this.mContext = context;
    }

    public void clearData() {
        groupBeanList.clear();
        notifyDataSetChanged();
    }

    public void setDataList(List<GroupBean> groupBeens, String keywords) {
        this.keywords = keywords;
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
        holder.tvIntroduce.setText(groupBean.getIntroduce());
        holder.tvPersonNumber.setText(groupBean.getPnumber());
        String groupName = groupBean.getGroupname();
        Spannable spannable = new SpannableString(groupBean.getGroupname());
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.red));
        spannable.setSpan(colorSpan, groupName.indexOf(keywords), groupName.indexOf(keywords) + keywords.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvGroupName.setText(spannable);
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
        TextView tvPersonNumber;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivGroupIcon = (ImageView) itemView.findViewById(R.id.item_section_groupicon);
            tvGroupName = (TextView) itemView.findViewById(R.id.item_section_groupname);
            tvIntroduce = (TextView) itemView.findViewById(R.id.item_section_introduce);
            tvPersonNumber = (TextView) itemView.findViewById(R.id.item_group_pnumber);
            this.itemView = itemView;
        }
    }
}
