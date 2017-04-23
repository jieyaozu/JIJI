package com.yaozu.object.bean;

/**
 * Created by jxj42 on 2017/4/22.
 */

public class MemberInfo {
    private String username;
    private String userid;
    private String siconpath;
    private String groupid;
    private String usertype;
    //成员备注名
    private String nickname;
    //性别
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSiconpath() {
        return siconpath;
    }

    public void setSiconpath(String siconpath) {
        this.siconpath = siconpath;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
