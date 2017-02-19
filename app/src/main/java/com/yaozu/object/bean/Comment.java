package com.yaozu.object.bean;

import java.io.Serializable;

/**
 * Created by jxj42 on 2017/2/16.
 */

public class Comment implements Serializable {
    private String userid;
    private String userName;
    private String siconpath;
    private String postid;
    private String commentid;
    private String content;
    private String replyUserid;
    private String replyUserName;
    private String publictime;
    public int unread;
    public int level;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSiconpath() {
        return siconpath;
    }

    public void setSiconpath(String siconpath) {
        this.siconpath = siconpath;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReplyUserid() {
        return replyUserid;
    }

    public void setReplyUserid(String replyUserid) {
        this.replyUserid = replyUserid;
    }

    public String getReplyUserName() {
        return replyUserName;
    }

    public void setReplyUserName(String replyUserName) {
        this.replyUserName = replyUserName;
    }

    public String getPublictime() {
        return publictime;
    }

    public void setPublictime(String publictime) {
        this.publictime = publictime;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
