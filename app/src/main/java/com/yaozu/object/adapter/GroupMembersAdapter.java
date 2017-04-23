package com.yaozu.object.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.bean.MemberInfo;
import com.yaozu.object.bean.constant.GroupUserType;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.stickylistheaders.StickyListHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/4/22.
 */

public class GroupMembersAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private Context mContext;
    private List<MemberInfo> memberInfos = new ArrayList<>();

    public GroupMembersAdapter(Context context) {
        this.mContext = context;
    }

    public void clearData() {
        if (memberInfos != null) {
            memberInfos.clear();
        }
    }

    public void setDataList(List<MemberInfo> members) {
        if (members != null) {
            memberInfos.addAll(members);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return memberInfos.size();
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
        ViewHolder viewHolder = null;
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        } else {
            viewHolder = new ViewHolder();
            view = View.inflate(mContext, R.layout.item_listview_memberinfo, null);
            viewHolder.ivUserIcon = (ImageView) view.findViewById(R.id.item_member_icon);
            viewHolder.tvUserName = (TextView) view.findViewById(R.id.item_member_name);
            viewHolder.divider = view.findViewById(R.id.item_member_divider);
            view.setTag(viewHolder);
        }
        if (position == memberInfos.size() - 1) {
            viewHolder.divider.setVisibility(View.GONE);
        }
        final MemberInfo memberInfo = memberInfos.get(position);
        Utils.setUserImg(Constant.APP_IMAGE_HOST + memberInfo.getSiconpath(), viewHolder.ivUserIcon);
        if (!TextUtils.isEmpty(memberInfo.getNickname())) {
            viewHolder.tvUserName.setText(memberInfo.getNickname());
        } else {
            viewHolder.tvUserName.setText(memberInfo.getUsername());
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toUserInfoActivity(mContext, memberInfo.getGroupid(), memberInfo.getUserid());
            }
        });
        return view;
    }

    class ViewHolder {
        ImageView ivUserIcon;
        TextView tvUserName;
        View divider;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.item_header_member, null);
        TextView tvHeaderName = (TextView) view.findViewById(R.id.item_member_headername);
        MemberInfo memberInfo = memberInfos.get(position);
        if (GroupUserType.ADMIN.equals(memberInfo.getUsertype()) || GroupUserType.MASTER.equals(memberInfo.getUsertype())) {
            tvHeaderName.setText("群主、管理员");
        } else if (GroupUserType.NORMAL.equals(memberInfo.getUsertype())) {
            tvHeaderName.setText("群成员");
        }
        return view;
    }

    @Override
    public long getHeaderId(int position) {
        MemberInfo memberInfo = memberInfos.get(position);
        if (GroupUserType.ADMIN.equals(memberInfo.getUsertype()) || GroupUserType.MASTER.equals(memberInfo.getUsertype())) {
            return GroupUserType.ADMIN.hashCode();
        } else if (GroupUserType.NORMAL.equals(memberInfo.getUsertype())) {
            return GroupUserType.NORMAL.hashCode();
        }
        return 0;
    }
}
