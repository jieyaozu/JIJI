package com.yaozu.object.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.IS_CLEARGROUP_MESSAGE_SUCCESS) {
            Constant.IS_CLEARGROUP_MESSAGE_SUCCESS = false;
            List<MessageBean> allbeans = messageBeanDao.findAllBeans();
            if (allbeans != null) {
                messageBeanList.clear();
                messageBeanList.addAll(allbeans);
                listAdapter.notifyDataSetChanged();
            }
        }
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
            String imgurl = messageBean.getIcon();
            tvTitle.setText(messageBean.getTitle());
            adtitional.setText(messageBean.getAdditional());
            if (MsgType.TYPE_GROUP.equals(messageBean.getType())) {
                ivIcon.setImageResource(R.drawable.group_message_icon);
            } else {
                Utils.setUserImg(imgurl, ivIcon);
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
}
