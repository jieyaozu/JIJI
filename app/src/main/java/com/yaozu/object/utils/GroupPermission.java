package com.yaozu.object.utils;

import com.yaozu.object.db.dao.GroupDao;

import java.util.List;

/**
 * Created by jxj42 on 2017/4/23.
 */

public class GroupPermission {

    public static boolean isGroupMember(GroupDao groupDao, String groupid){
        List<String> groupidList = groupDao.findAllMyGroupid();
        boolean isGroupMember = false;
        for (int i = 0; i < groupidList.size(); i++) {
            if (groupidList.get(i).equals(groupid)) {
                isGroupMember = true;
                break;
            }
        }
        return isGroupMember;
    }

    /**
     * 这个群id是否是我管理的
     *
     * @param groupid
     * @return
     */
    public static boolean isMyAdminGroupid(GroupDao groupDao, String groupid) {
        List<String> groupidList = groupDao.findMyAdminGroupid();
        boolean isAdminGroupid = false;
        for (int i = 0; i < groupidList.size(); i++) {
            if (groupidList.get(i).equals(groupid)) {
                isAdminGroupid = true;
                break;
            }
        }
        return isAdminGroupid;
    }

    public static boolean isMyCreatGroupid(GroupDao groupDao, String groupid) {
        List<String> groupidList = groupDao.findMyCreateGroupid();
        boolean isAdminGroupid = false;
        for (int i = 0; i < groupidList.size(); i++) {
            if (groupidList.get(i).equals(groupid)) {
                isAdminGroupid = true;
                break;
            }
        }
        return isAdminGroupid;
    }
}
