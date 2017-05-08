package com.yaozu.object.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.bean.constant.GroupUserType;
import com.yaozu.object.pushreceiver.remind.NewPostRemind;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.stickylistheaders.StickyListHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/3/5.
 */

public class GroupListAdapter extends BaseAdapter implements StickyListHeadersAdapter {
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
            sortListData();
            notifyDataSetChanged();
        }
    }

    //分类排序
    private void sortListData() {
        List<GroupBean> createGrouplist = new ArrayList<>();
        for (int i = groupBeanList.size() - 1; i >= 0; i--) {
            GroupBean groupBean = groupBeanList.get(i);
            if (GroupUserType.MASTER.equals(groupBean.getUsertype())) {
                groupBeanList.remove(groupBean);
                createGrouplist.add(groupBean);
            }
        }

        List<GroupBean> adminGrouplist = new ArrayList<>();
        for (int i = groupBeanList.size() - 1; i >= 0; i--) {
            GroupBean groupBean = groupBeanList.get(i);
            if (GroupUserType.ADMIN.equals(groupBean.getUsertype())) {
                groupBeanList.remove(groupBean);
                adminGrouplist.add(groupBean);
            }
        }
        groupBeanList.addAll(0, createGrouplist);
        groupBeanList.addAll(createGrouplist.size(), adminGrouplist);
    }

    @Override
    public int getCount() {
        return groupBeanList.size();
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
        View view = null;
        MyViewHolder holder = null;
        if (convertView == null) {
            holder = new MyViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_group_listview, parent, false);
            holder.layout = (RelativeLayout) view.findViewById(R.id.item_group_layout);
            holder.tvGroupName = (TextView) view.findViewById(R.id.item_groupname);
            holder.ivGroupIcon = (ImageView) view.findViewById(R.id.item_group_icon);
            holder.divider = view.findViewById(R.id.item_group_divider);
            holder.tvRemind = (TextView) view.findViewById(R.id.item_groupremind_number);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (MyViewHolder) view.getTag();
        }
        if (position == groupBeanList.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        }
        final GroupBean groupBean = groupBeanList.get(position);
        int remindCount = NewPostRemind.getInstance(mContext).getRemindNumber(groupBean.getGroupid());
        if (remindCount <= 0) {
            holder.tvRemind.setVisibility(View.GONE);
        } else {
            holder.tvRemind.setVisibility(View.VISIBLE);
            holder.tvRemind.setText(remindCount + "");
        }
        holder.tvGroupName.setText(groupBean.getGroupname());
        Utils.setUserImg(groupBean.getGroupicon(), holder.ivGroupIcon);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toGroupOfPostActivity(mContext, groupBean);
            }
        });
        return view;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_group_header, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.item_group_title);
        final GroupBean groupBean = groupBeanList.get(position);
        if (GroupUserType.MASTER.equals(groupBean.getUsertype())) {
            textView.setText("我创建的群");
        } else if (GroupUserType.ADMIN.equals(groupBean.getUsertype())) {
            textView.setText("我管理的群");
        } else if (GroupUserType.NORMAL.equals(groupBean.getUsertype())) {
            textView.setText("我加入的群");
        }
        return view;
    }

    @Override
    public long getHeaderId(int position) {
        final GroupBean groupBean = groupBeanList.get(position);
        if (GroupUserType.MASTER.equals(groupBean.getUsertype())) {
            return 0;
        } else if (GroupUserType.ADMIN.equals(groupBean.getUsertype())) {
            return 1;
        } else if (GroupUserType.NORMAL.equals(groupBean.getUsertype())) {
            return 2;
        }
        return 3;
    }

    class MyViewHolder {
        private RelativeLayout layout;
        private TextView tvGroupName;
        private ImageView ivGroupIcon;
        private View divider;
        private TextView tvRemind;
    }

}
