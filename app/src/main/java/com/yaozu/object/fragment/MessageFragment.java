package com.yaozu.object.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.bean.MessageBean;
import com.yaozu.object.db.dao.MessageBeanDao;

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
            TextView tvMsgNumber = (TextView) view.findViewById(R.id.item_message_number);
            MessageBean messageBean = messageBeanList.get(position);
            if (messageBean.getNewMsgnumber() > 0) {
                tvMsgNumber.setVisibility(View.VISIBLE);
                tvMsgNumber.setText(messageBean.getNewMsgnumber() + "");
            } else {
                tvMsgNumber.setVisibility(View.GONE);
            }
            return view;
        }
    }
}
