package com.yaozu.object.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.entity.ContactsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/1/24.
 */

public class ContactsAdapter extends BaseAdapter {
    private Context mContext;
    private List<ContactsInfo> contactsInfos = new ArrayList<ContactsInfo>();

    public ContactsAdapter(Context ctx) {
        mContext = ctx;
    }

    public void setAddDataList(List<ContactsInfo> infos) {
        if (contactsInfos != null) {
            contactsInfos.addAll(infos);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        if (contactsInfos != null) {
            contactsInfos.clear();
        }
    }

    @Override
    public int getCount() {
        return contactsInfos.size();
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
        View view = View.inflate(mContext, R.layout.item_listview_contacts, null);
        TextView userName = (TextView) view.findViewById(R.id.item_contacts_username);
        TextView phoneNumber = (TextView) view.findViewById(R.id.item_contacts_phonenumber);
        ContactsInfo contactsInfo = contactsInfos.get(position);
        userName.setText(contactsInfo.getName());
        phoneNumber.setText(contactsInfo.getNumber());
        return view;
    }
}
