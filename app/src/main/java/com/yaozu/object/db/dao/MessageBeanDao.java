package com.yaozu.object.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yaozu.object.bean.MessageBean;
import com.yaozu.object.db.AppDbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyaozu on 2017/4/17.
 */

public class MessageBeanDao {
    private AppDbHelper helper;
    private Context context;

    public MessageBeanDao(Context context) {
        this.context = context;
        helper = new AppDbHelper(context);
    }

    public void addMessage(MessageBean bean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into messagebean (type,title,additional,newMsgnumber,icon) values (?,?,?,?,?)",
                    new Object[]{bean.getType(), bean.getTitle(), bean.getAdditional(), bean.getNewMsgnumber(), bean.getIcon()});
        }
        db.close();
    }

    public boolean isHave(String type) {
        boolean ishave = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from messagebean where type=?", new String[]{type});
            while (cursor.moveToNext()) {
                ishave = true;
                break;
            }
        }
        return ishave;
    }

    public void updateBean(MessageBean bean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update messagebean set title=?,additional=?,newMsgnumber=?icon=? where type=?",
                    new Object[]{bean.getTitle(), bean.getAdditional(), bean.getNewMsgnumber(), bean.getIcon(), bean.getType()});
        }
        db.close();
    }

    public List<MessageBean> findAllBeans() {
        List<MessageBean> beanList = new ArrayList<MessageBean>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from messagebean", null);
            while (cursor.moveToNext()) {
                MessageBean messageBean = new MessageBean();
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String additional = cursor.getString(cursor.getColumnIndex("additional"));
                int newMsgnumber = cursor.getInt(cursor.getColumnIndex("newMsgnumber"));
                String icon = cursor.getString(cursor.getColumnIndex("icon"));
                String type = cursor.getString(cursor.getColumnIndex("type"));

                messageBean.setTitle(title);
                messageBean.setAdditional(additional);
                messageBean.setNewMsgnumber(newMsgnumber);
                messageBean.setIcon(icon);
                messageBean.setType(type);
                beanList.add(messageBean);
            }
        }
        db.close();
        return beanList;
    }

    public MessageBean findFriend(String type) {
        MessageBean messageBean = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from messagebean where type=?", new String[]{type});
            while (cursor.moveToNext()) {
                messageBean = new MessageBean();
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String additional = cursor.getString(cursor.getColumnIndex("additional"));
                int newMsgnumber = cursor.getInt(cursor.getColumnIndex("newMsgnumber"));
                String icon = cursor.getString(cursor.getColumnIndex("icon"));

                messageBean.setTitle(title);
                messageBean.setAdditional(additional);
                messageBean.setNewMsgnumber(newMsgnumber);
                messageBean.setIcon(icon);
                messageBean.setType(type);
            }
        }
        db.close();
        return messageBean;
    }
}
