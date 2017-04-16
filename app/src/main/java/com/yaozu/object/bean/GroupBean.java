package com.yaozu.object.bean;

import java.io.Serializable;

/**
 * Created by jxj42 on 2017/4/4.
 */

public class GroupBean implements Serializable {
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
    private String sectionname;
    //群简介
    private String introduce;
    //群成员数量
    private String pnumber;
    //发贴总数
    private String mptnumber;

    private String isGroupMember;

    public String getIsGroupMember() {
        return isGroupMember;
    }

    public void setIsGroupMember(String isGroupMember) {
        this.isGroupMember = isGroupMember;
    }

    public String getMptnumber() {
        return mptnumber;
    }

    public void setMptnumber(String mptnumber) {
        this.mptnumber = mptnumber;
    }

    public String getSectionname() {
        return sectionname;
    }

    public void setSectionname(String sectionname) {
        this.sectionname = sectionname;
    }

    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

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
