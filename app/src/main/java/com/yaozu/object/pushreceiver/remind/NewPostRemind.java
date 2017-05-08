package com.yaozu.object.pushreceiver.remind;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import com.yaozu.object.db.dao.GroupDao;
import com.yaozu.object.utils.IntentKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jxj42 on 2017/5/6.
 * 新发贴子提醒记录
 */

public class NewPostRemind {
    private Context mContext;
    private static SharedPreferences preferences;
    private static NewPostRemind instance;
    private Map<String, Integer> memoryData = new HashMap<>();
    private GroupDao groupDao;

    private NewPostRemind(Context context) {
        this.mContext = context;
        groupDao = new GroupDao(context);
        preferences = mContext.getSharedPreferences("newpostremind", Context.MODE_PRIVATE);
        List<String> groupidList = groupDao.findAllMyGroupid();
        //读到内存中去
        for (int i = 0; i < groupidList.size(); i++) {
            String groupid = groupidList.get(i);
            int count = getRemindNumber(groupid);
            memoryData.put(groupid, count);
        }
    }

    public static NewPostRemind getInstance(Context context) {
        if (instance == null) {
            instance = new NewPostRemind(context);
        }
        return instance;
    }

    /**
     * 保存提醒数
     *
     * @param groupid
     * @param number
     */
    public void putRemind(String groupid, int number) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(groupid, number);
        editor.commit();
        //内存中缓存
        memoryData.put(groupid, number);
        //发个广播更新下UI
        Intent playingintent = new Intent(IntentKey.NOTIFY_NEWPOST_REMIND);
        LocalBroadcastManager playinglocalBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        playinglocalBroadcastManager.sendBroadcast(playingintent);
    }

    /**
     * 得到提醒数
     *
     * @param groupid
     * @return
     */
    public int getRemindNumber(String groupid) {
        if (memoryData.containsKey(groupid) && memoryData.get(groupid) > 0) {
            return memoryData.get(groupid);
        }
        return preferences.getInt(groupid, 0);
    }

    /**
     * 所有的群的提醒总数
     *
     * @return
     */
    public int getTotalRemindCount() {
        int totalcount = 0;
        Set<Map.Entry<String, Integer>> entries = memoryData.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            int count = entry.getValue();
            totalcount += count;
        }
        return totalcount;
    }
}
