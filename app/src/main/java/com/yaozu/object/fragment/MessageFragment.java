package com.yaozu.object.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.bean.MessageBean;
import com.yaozu.object.bean.constant.GMStatus;
import com.yaozu.object.db.dao.MessageBeanDao;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.MsgType;
import com.yaozu.object.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/4/16.
 */

public class MessageFragment extends BaseFragment {
    public static String TAG = "MessageFragment";
    private ListView mListView;
    private MessageListAdapter listAdapter;
    private MessageBeanDao messageBeanDao;
    private List<MessageBean> messageBeanList = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messageBeanDao = new MessageBeanDao(this.getActivity());
        mListView = (ListView) view.findViewById(R.id.fragment_message_listview);
        listAdapter = new MessageListAdapter();
        mListView.setAdapter(listAdapter);
        List<MessageBean> allbeans = messageBeanDao.findAllBeans();
        if (allbeans != null) {
            messageBeanList.addAll(allbeans);
            listAdapter.notifyDataSetChanged();
        }
        registerUpdateReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterUpdateRecevier();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }

    public class MessageListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return messageBeanList.size();
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
            View view = View.inflate(MessageFragment.this.getActivity(), R.layout.item_message_fragment, null);
            ImageView ivIcon = (ImageView) view.findViewById(R.id.item_messgeicon);
            TextView tvTitle = (TextView) view.findViewById(R.id.item_message_title);
            TextView adtitional = (TextView) view.findViewById(R.id.item_message_additional);
            TextView tvMsgNumber = (TextView) view.findViewById(R.id.item_message_number);
            final MessageBean messageBean = messageBeanList.get(position);
            tvTitle.setText(messageBean.getTitle());
            if (TextUtils.isEmpty(messageBean.getAdditional())) {
                adtitional.setVisibility(View.GONE);
            } else {
                adtitional.setVisibility(View.VISIBLE);
                adtitional.setText(messageBean.getAdditional());
            }
            if (MsgType.TYPE_GROUP.equals(messageBean.getType())) {
                ivIcon.setImageResource(R.drawable.group_message_icon);
            } else if (MsgType.TYPE_REPLY.equals(messageBean.getType())) {
                ivIcon.setImageResource(R.drawable.group_message_icon);
            } else if (MsgType.TYPE_COMMENT.equals(messageBean.getType())) {
                ivIcon.setImageResource(R.drawable.group_message_icon);
            }
            if (messageBean.getNewMsgnumber() > 0) {
                tvMsgNumber.setVisibility(View.VISIBLE);
                tvMsgNumber.setText(messageBean.getNewMsgnumber() + "");
            } else {
                tvMsgNumber.setVisibility(View.GONE);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MsgType.TYPE_GROUP.equals(messageBean.getType())) {
                        IntentUtil.toGroupMessageActivity(MessageFragment.this.getActivity());
                    }
                }
            });
            return view;
        }
    }


    /**
     * @Description:
     * @author
     * @date 2013-10-28 jieyaozu 10:30:27
     */
    protected void registerUpdateReceiver() {
        if (updataroadcastReceiver == null) {
            updataroadcastReceiver = new UpdataBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(IntentKey.NOTIFY_MESSAGE_REMIND);
            localBroadcastManager = LocalBroadcastManager.getInstance(this.getActivity());
            localBroadcastManager.registerReceiver(updataroadcastReceiver, filter);
        }
    }

    protected void unRegisterUpdateRecevier() {
        if (updataroadcastReceiver != null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(this.getActivity());
            localBroadcastManager.unregisterReceiver(updataroadcastReceiver);
            updataroadcastReceiver = null;
        }
    }

    private UpdataBroadcastReceiver updataroadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;

    /**
     * 2015-11-5
     */
    private class UpdataBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IntentKey.NOTIFY_MESSAGE_REMIND.equals(intent.getAction())) {
                List<MessageBean> allbeans = messageBeanDao.findAllBeans();
                if (allbeans != null) {
                    messageBeanList.clear();
                    messageBeanList.addAll(allbeans);
                    listAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
