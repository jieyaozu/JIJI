package com.yaozu.object.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.db.AppDbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyaozu on 2017/4/7.
 */

public class GroupDao {
    private AppDbHelper helper;
    private Context context;

    public GroupDao(Context context) {
        this.context = context;
        helper = new AppDbHelper(context);
    }

    public void addGroup(GroupBean groupBean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into mygroup (groupid,groupicon,usertype,number,sectionid,groupname,introduce) values (?,?,?,?,?,?,?)",
                    new Object[]{groupBean.getGroupid(), groupBean.getGroupicon(), groupBean.getUsertype(), groupBean.getNumber()
                            , groupBean.getSectionid(), groupBean.getGroupname(), groupBean.getIntroduce()});
        }
        db.close();
    }

    public List<GroupBean> findAllMyGroup() {
        List<GroupBean> beanList = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from mygroup", null);
            while (cursor.moveToNext()) {
                GroupBean groupBean = new GroupBean();
                String groupid = cursor.getString(cursor.getColumnIndex("groupid"));
                String groupicon = cursor.getString(cursor.getColumnIndex("groupicon"));
                String usertype = cursor.getString(cursor.getColumnIndex("usertype"));
                String number = cursor.getString(cursor.getColumnIndex("number"));
                String sectionid = cursor.getString(cursor.getColumnIndex("sectionid"));
                String groupname = cursor.getString(cursor.getColumnIndex("groupname"));
                String introduce = cursor.getString(cursor.getColumnIndex("introduce"));

                groupBean.setGroupid(groupid);
                groupBean.setGroupicon(groupicon);
                groupBean.setUsertype(usertype);
                groupBean.setNumber(number);
                groupBean.setSectionid(sectionid);
                groupBean.setGroupname(groupname);
                groupBean.setIntroduce(introduce);
                beanList.add(groupBean);
            }
        }
        db.close();
        return beanList;
    }

    public void clearTable() {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from mygroup where 1=1");
        }
        db.close();
    }
}
