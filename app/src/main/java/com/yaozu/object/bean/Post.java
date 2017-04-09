package com.yaozu.object.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/11.
 */

public class Post implements Serializable {
    private String postid;
    //如果是跟帖的话,parentid是主贴的postid，否则为空
    private String parentid;
    private String parenttitle;
    //帖子的状态，0是正常，1是精华，2是置顶，3是下沉
    private String status;
    private String userIcon;
    private String userName;
    private String userid;
    private String createtime;
    //更新时间(回复时或者自己更新的时间)
    private String updatetime;
    private String title;
    private String content;
    private String supportNum;
    private String replyNum;
    private List<MyImage> images;
    private List<Comment> comments;
    private String groupid;
    private String groupname;

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

    public String getParenttitle() {
        return parenttitle;
    }

    public void setParenttitle(String parenttitle) {
        this.parenttitle = parenttitle;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<MyImage> getImages() {
        return images;
    }

    public void setImages(List<MyImage> images) {
        this.images = images;
    }
}
