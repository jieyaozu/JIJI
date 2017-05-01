package com.yaozu.object.utils;

import android.text.TextUtils;

import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.bean.PermissionBean;
import com.yaozu.object.bean.SectionBean;

import java.util.List;

/**
 * Created by jxj42 on 2017/4/8.
 */

public class SendPostUtil {
    /**
     * 根据sectionid找出在当前集合中的位置
     *
     * @param section_data_list
     * @param sectionid
     * @return
     */
    public static int getSectionSelection(List<SectionBean> section_data_list, String sectionid) {
        int selectPos = -1;
        if (section_data_list == null) {
            return -1;
        }
        if (TextUtils.isEmpty(sectionid)) {
            return selectPos;
        }
        for (int i = 0; i < section_data_list.size(); i++) {
            if (sectionid.equals(section_data_list.get(i).getSectionid())) {
                selectPos = i;
                break;
            }
        }
        return selectPos;
    }

    /**
     * 根据groupid找出在当前集合中的位置
     *
     * @param data_list
     * @param groupid
     * @return
     */
    public static int getGroupSelection(List<GroupBean> data_list, String groupid) {
        int selectPos = -1;
        if (data_list == null) {
            return -1;
        }
        if (TextUtils.isEmpty(groupid)) {
            return selectPos;
        }
        for (int i = 0; i < data_list.size(); i++) {
            if (groupid.equals(data_list.get(i).getGroupid())) {
                selectPos = i;
                break;
            }
        }
        return selectPos;
    }

    /**
     * 根据permissioncode找出在当前集合中的位置
     *
     * @param data_list
     * @param permissioncode
     * @return
     */
    public static int getPermissionSelection(List<PermissionBean> data_list, String permissioncode) {
        int selectPos = -1;
        if (data_list == null) {
            return -1;
        }
        if (TextUtils.isEmpty(permissioncode)) {
            return selectPos;
        }
        for (int i = 0; i < data_list.size(); i++) {
            if (permissioncode.equals(data_list.get(i).getPermissioncode())) {
                selectPos = i;
                break;
            }
        }
        return selectPos;
    }
}
