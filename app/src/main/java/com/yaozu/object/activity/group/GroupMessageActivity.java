package com.yaozu.object.activity.group;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.bean.GMStatus;
import com.yaozu.object.bean.GroupMessage;
import com.yaozu.object.bean.MessageBean;
import com.yaozu.object.db.dao.GroupDao;
import com.yaozu.object.db.dao.MessageBeanDao;
import com.yaozu.object.entity.ApplyGroupData;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.MsgType;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyaozu on 2017/4/18.
 */

public class GroupMessageActivity extends BaseActivity {
    private GroupDao groupDao;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private HeaderViewRecyclerAdapter stringAdapter;
    private GroupMessageAdapter messageAdapter;
    private List<GroupMessage> groupMessageList = new ArrayList<>();

    private MessageBeanDao messageBeanDao;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_group_message);
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.common_refresh_recyclerview);
    }

    @Override
    protected void initData() {
        messageBeanDao = new MessageBeanDao(this);
        groupDao = new GroupDao(this);
        groupMessageList = groupDao.findAllGroupMessage();

        messageAdapter = new GroupMessageAdapter();
        stringAdapter = new HeaderViewRecyclerAdapter(messageAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter(linearLayoutManager, stringAdapter);

        requestFindGroupMessage();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("群消息");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void requestFindGroupMessage() {
        String url = DataInterface.FIND_APPLY_ENTER_GROUP_MSG + "userid=" + LoginInfo.getInstance(this).getUserAccount();
        RequestManager.getInstance().getRequest(this, url, ApplyGroupData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    ApplyGroupData applyGroupData = (ApplyGroupData) object;
                    List<GroupMessage> applyList = applyGroupData.getBody().getApplybeans();
                    if (applyList != null && applyList.size() > 0) {
                        insertGroupMessage(applyList);
                        groupMessageList.addAll(0, applyList);
                        messageAdapter.notifyDataSetChanged();
                        //清除
                        requestClearGroupMsg();
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    private void requestClearGroupMsg() {
        String url = DataInterface.CLEAR_GROUP_MSG + "userid=" + LoginInfo.getInstance(this).getUserAccount();
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    Constant.IS_CLEARGROUP_MESSAGE_SUCCESS = true;
                    MessageBean messageBean = messageBeanDao.findFriend(MsgType.TYPE_GROUP);
                    messageBean.setNewMsgnumber(0);
                    messageBeanDao.updateBean(messageBean);
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    /**
     * 插入群消息
     *
     * @param messageList
     */
    private void insertGroupMessage(List<GroupMessage> messageList) {
        if (messageList != null) {
            for (GroupMessage message : messageList) {
                groupDao.addGroupMessage(message);
            }
        }
    }

    public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.MyViewHolder> {

        @Override
        public GroupMessageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(GroupMessageActivity.this).inflate(R.layout.item_group_message, parent, false));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(GroupMessageAdapter.MyViewHolder holder, int position) {
            final GroupMessage groupMessage = groupMessageList.get(position);

            holder.tvMessage.setText(groupMessage.getMessage());
            holder.tvTime.setText(DateUtil.getRelativeTime(groupMessage.getCreatetime()));
            Utils.setUserImg(groupMessage.getGroupicon(), holder.ivGroupIcon);

            if (GMStatus.EXIT.equals(groupMessage.getStatus())) {
                holder.tvTitle.setText(groupMessage.getUsername() + " 已退出 " + groupMessage.getGroupname());
            } else {
                holder.tvTitle.setText(groupMessage.getUsername() + " 申请加入 " + groupMessage.getGroupname());
            }
            if (GMStatus.APPLYING.equals(groupMessage.getStatus())) {
                holder.opLayout.setVisibility(View.VISIBLE);
            } else {
                holder.opLayout.setVisibility(View.GONE);
            }
            if (GMStatus.ADDED.equals(groupMessage.getStatus())) {
                holder.tvResult.setVisibility(View.VISIBLE);
            } else if (GMStatus.REFUSE.equals(groupMessage.getStatus())) {
                holder.tvResult.setText("已拒绝");
                holder.tvResult.setVisibility(View.VISIBLE);
            }
            holder.tvAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestJoinToGroup(groupMessage);
                }
            });
            holder.tvRefuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupMessage.setStatus(GMStatus.REFUSE);
                    groupDao.updateMessageBean(groupMessage);
                    messageAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return groupMessageList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView ivGroupIcon;
            public TextView tvTitle;
            public TextView tvMessage;
            public LinearLayout opLayout;//操作
            public TextView tvAgree;
            public TextView tvRefuse;
            public TextView tvResult;
            public TextView tvTime;

            public MyViewHolder(View itemView) {
                super(itemView);
                ivGroupIcon = (ImageView) itemView.findViewById(R.id.item_groupmsg_icon);
                tvTitle = (TextView) itemView.findViewById(R.id.item_groupmsg_title);
                tvMessage = (TextView) itemView.findViewById(R.id.item_groupmsg_message);
                opLayout = (LinearLayout) itemView.findViewById(R.id.item_groupmsg_applying);
                tvAgree = (TextView) itemView.findViewById(R.id.item_groupmsg_agree);
                tvRefuse = (TextView) itemView.findViewById(R.id.item_groupmsg_refuse);
                tvResult = (TextView) itemView.findViewById(R.id.item_groupmsg_result);
                tvTime = (TextView) itemView.findViewById(R.id.item_groupmsg_createtime);
            }
        }
    }

    /**
     * 把用户加入群
     *
     * @param groupMessage
     */
    private void requestJoinToGroup(final GroupMessage groupMessage) {
        String url = DataInterface.JOIN_GROUP + "userid=" + groupMessage.getUserid() + "&groupid=" + groupMessage.getGroupid();
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if ("1".equals(requestData.getBody().getCode())) {
                        groupMessage.setStatus(GMStatus.ADDED);
                        groupDao.updateMessageBean(groupMessage);
                        messageAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }
}
