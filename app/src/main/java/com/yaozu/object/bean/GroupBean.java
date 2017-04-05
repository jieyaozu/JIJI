package com.yaozu.object.bean;

/**
 * Created by jxj42 on 2017/4/4.
 */

public class GroupBean {
    private String groupid;
    private String groupname;
    //群的头像
    private String groupicon;
    //自己在本群内的身份类型
    private String usertype;
    //人数
    private String number;
    //所属的版块id
    private String sectionid;

    public String getGroupicon() {
        return groupicon;
    }

    public void setGroupicon(String groupicon) {
        this.groupicon = groupicon;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSectionid() {
        return sectionid;
    }

    public void setSectionid(String sectionid) {
        this.sectionid = sectionid;
    }
}
