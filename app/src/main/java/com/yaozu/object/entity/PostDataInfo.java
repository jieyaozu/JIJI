package com.yaozu.object.entity;

import java.util.List;

/**
 * Created by jxj42 on 2017/2/9.
 */

public class PostDataInfo extends BaseEntity<PostDataInfo.BodyEntity> {
    public class BodyEntity {
        private String postid;
        private String userIcon;
        private String userName;
        private String userid;
        private String time;
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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
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
}
