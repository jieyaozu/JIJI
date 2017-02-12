package com.yaozu.object.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/11.
 */

public class Post implements Serializable {
    private String postid;
    //帖子的类型，1是主贴，0是跟贴
    private String type;
    private String userIcon;
    private String userName;
    private String userid;
    private String createtime;
    private String title;
    private String content;
    private String supportNum;
    private String replyNum;
    private List<MyImages> images;

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSupportNum() {
        return supportNum;
    }

    public void setSupportNum(String supportNum) {
        this.supportNum = supportNum;
    }

    public String getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(String replyNum) {
        this.replyNum = replyNum;
    }

    public List<MyImages> getImages() {
        return images;
    }

    public void setImages(List<MyImages> images) {
        this.images = images;
    }
}
