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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
