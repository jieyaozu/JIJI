package com.yaozu.object.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yaozu.object.bean.SectionBean;
import com.yaozu.object.db.AppDbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyaozu on 2017/4/5.
 */

public class SectionDao {
    private AppDbHelper helper;
    private Context context;

    public SectionDao(Context context) {
        this.context = context;
        helper = new AppDbHelper(context);
    }

    public void add(SectionBean sectionBean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into section (sectionid,sectionname,selected) values (?,?,?)",
                    new Object[]{sectionBean.getSectionid(), sectionBean.getSectionname(), sectionBean.getSelect()});
        }
        db.close();
    }

    public List<SectionBean> findAllSections() {
        List<SectionBean> beanList = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from section", null);
            while (cursor.moveToNext()) {
                SectionBean sectionBean = new SectionBean();
                String sectionid = cursor.getString(cursor.getColumnIndex("sectionid"));
                String sectionname = cursor.getString(cursor.getColumnIndex("sectionname"));

                sectionBean.setSectionid(sectionid);
                sectionBean.setSectionname(sectionname);
                beanList.add(sectionBean);
            }
        }
        db.close();
        return beanList;
    }

    public List<SectionBean> findSelectSections() {
        List<SectionBean> beanList = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from section where selected=?", new String[]{"true"});
            while (cursor.moveToNext()) {
                SectionBean sectionBean = new SectionBean();
                String sectionid = cursor.getString(cursor.getColumnIndex("sectionid"));
                String sectionname = cursor.getString(cursor.getColumnIndex("sectionname"));

                sectionBean.setSectionid(sectionid);
                sectionBean.setSectionname(sectionname);
                beanList.add(sectionBean);
            }
        }
        db.close();
        return beanList;
    }

    public void updateSelected(SectionBean sectionBean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update section set selected=? where sectionid=?",
                    new Object[]{sectionBean.getSelect(), sectionBean.getSectionid()});
        }
        db.close();
    }

    public void updateName(SectionBean sectionBean) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update section set sectionname=? where sectionid=?",
                    new Object[]{sectionBean.getSectionname(), sectionBean.getSectionid()});
        }
        db.close();
    }

    /**
     * 查询一条是否存在
     *
     * @param sectionid
     * @return
     */
    public boolean isHaveRecord(String sectionid) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from section where sectionid=?", new String[]{sectionid});
            if (cursor.moveToNext()) {
                result = true;
            }
            cursor.close();
        }
        db.close();
        return result;
    }
}
