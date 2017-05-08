package com.yaozu.object.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jieyaozu on 2017/4/5.
 */

public class AppDbHelper extends SQLiteOpenHelper {

    public AppDbHelper(Context context) {
        super(context, "app.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table section (_id integer primary key autoincrement," +
                "                         sectionid varchar(32) unique, " +
                "                         sectionname varchar(32), " +
                "                         selected varchar(32))");
        db.execSQL("create table mygroup (_id integer primary key autoincrement," +
                "                         groupid varchar(32) unique, " +
                "                         groupicon varchar(32), " +
                "                         usertype varchar(32), " +
                "                         number varchar(32), " +
                "                         sectionid varchar(32), " +
                "                         groupname varchar(64), " +
                "                         introduce varchar(512))");
        db.execSQL("create table messagebean (_id integer primary key autoincrement," +
                "                         type varchar(32) unique, " +
                "                         title varchar(64), " +
                "                         additional varchar(512), " +
                "                         newMsgnumber int(4), " +
                "                         icon varchar(32))");
        db.execSQL("create table groupmessage (_id integer primary key autoincrement," +
                "                         userid varchar(32), " +
                "                         username varchar(32), " +
                "                         groupid varchar(32), " +
                "                         groupname varchar(64), " +
                "                         groupicon varchar(64), " +
                "                         message varchar(512), " +
                "                         messageid varchar(36) unique, " +
                "                         createtime varchar(32), " +
                "                         status varchar(32))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
