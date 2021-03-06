package com.yaozu.object.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.bean.GroupMessage;
import com.yaozu.object.bean.constant.GroupUserType;
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

    public void deleteGroupById(String groupid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from mygroup where groupid=?",
                    new Object[]{groupid});
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

    /**
     * 查出所有的群ID
     *
     * @return
     */
    public List<String> findAllMyGroupid() {
        List<String> groupids = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from mygroup", null);
            while (cursor.moveToNext()) {
                String groupid = cursor.getString(cursor.getColumnIndex("groupid"));
                groupids.add(groupid);
            }
        }
        db.close();
        return groupids;
    }

    /**
     * 查出我所管理的群的群ID
     *
     * @return
     */
    public List<String> findMyAdminGroupid() {
        List<String> groupids = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from mygroup where usertype=? or usertype=?", new String[]{GroupUserType.ADMIN, GroupUserType.MASTER});
            while (cursor.moveToNext()) {
                String groupid = cursor.getString(cursor.getColumnIndex("groupid"));
                groupids.add(groupid);
            }
        }
        db.close();
        return groupids;
    }

    /**
     * 查出我所创建的群的群ID
     *
     * @return
     */
    public List<String> findMyCreateGroupid() {
        List<String> groupids = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from mygroup where usertype=?", new String[]{GroupUserType.MASTER});
            while (cursor.moveToNext()) {
                String groupid = cursor.getString(cursor.getColumnIndex("groupid"));
                groupids.add(groupid);
            }
        }
        db.close();
        return groupids;
    }

    public void clearTable() {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from mygroup where 1=1");
        }
        db.close();
    }

    /**
     * 我是否是此群的成员
     *
     * @param groupid 目标群ID
     * @return
     */
    public boolean isGroupMember(String groupid) {
        boolean ishave = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from mygroup where groupid=?", new String[]{groupid});
            while (cursor.moveToNext()) {
                ishave = true;
                break;
            }
        }
        return ishave;
    }

    /*
    * ***************************************************************群消息*********************************************************
    * */

    /**
     * 增加一个群消息
     *
     * @param groupMessage
     */
    public boolean addGroupMessage(GroupMessage groupMessage) {
        if (isHaveGroupMessage(groupMessage.getMessageid())) {
            return false;
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into groupmessage (userid,username,groupid,groupname,groupicon,message,status,createtime,messageid) values (?,?,?,?,?,?,?,?,?)",
                    new Object[]{groupMessage.getUserid(), groupMessage.getUsername(), groupMessage.getGroupid(), groupMessage.getGroupname(), groupMessage.getGroupicon()
                            , groupMessage.getMessage(), groupMessage.getStatus(), groupMessage.getCreatetime(), groupMessage.getMessageid()});
        }
        db.close();
        return true;
    }

    /**
     * 是否已经有此条消息
     *
     * @param messageid
     * @return
     */
    public boolean isHaveGroupMessage(String messageid) {
        boolean ishave = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from groupmessage where messageid=?", new String[]{messageid});
            while (cursor.moveToNext()) {
                ishave = true;
                break;
            }
        }
        return ishave;
    }

    public void updateMessageBean(GroupMessage bean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update groupmessage set username=?,groupname=?,groupicon=?,message=?,status=? where userid=? and groupid=? and createtime=?",
                    new Object[]{bean.getUsername(), bean.getGroupname(), bean.getGroupicon(), bean.getMessage(), bean.getStatus(), bean.getUserid(), bean.getGroupid(), bean.getCreatetime()});
        }
        db.close();
    }

    /**
     * 找出所有的群消息
     *
     * @return
     */
    public List<GroupMessage> findAllGroupMessage() {
        List<GroupMessage> beanList = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from groupmessage order by createtime desc", null);
            while (cursor.moveToNext()) {
                GroupMessage groupBean = new GroupMessage();
                String userid = cursor.getString(cursor.getColumnIndex("userid"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                String groupid = cursor.getString(cursor.getColumnIndex("groupid"));
                String groupname = cursor.getString(cursor.getColumnIndex("groupname"));
                String groupicon = cursor.getString(cursor.getColumnIndex("groupicon"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                String status = cursor.getString(cursor.getColumnIndex("status"));
                String createtime = cursor.getString(cursor.getColumnIndex("createtime"));

                groupBean.setUserid(userid);
                groupBean.setUsername(username);
                groupBean.setGroupid(groupid);
                groupBean.setGroupname(groupname);
                groupBean.setGroupicon(groupicon);
                groupBean.setMessage(message);
                groupBean.setStatus(status);
                groupBean.setCreatetime(createtime);
                beanList.add(groupBean);
            }
        }
        db.close();
        return beanList;
    }
}
