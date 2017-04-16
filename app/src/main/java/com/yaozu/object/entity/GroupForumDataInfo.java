package com.yaozu.object.entity;

import com.yaozu.object.bean.Post;

import java.util.List;

/**
 * Created by jxj42 on 2017/2/9.
 */

public class GroupForumDataInfo extends BaseEntity<GroupForumDataInfo.BodyEntity> {
    public class BodyEntity {
        private String code;
        private String message;
        //是否是群成员
        private String isGroupMember;
        private List<Post> toplist;
        private List<Post> postlist;

        public String getIsGroupMember() {
            return isGroupMember;
        }

        public void setIsGroupMember(String isGroupMember) {
            this.isGroupMember = isGroupMember;
        }

        public List<Post> getToplist() {
            return toplist;
        }

        public void setToplist(List<Post> toplist) {
            this.toplist = toplist;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<Post> getPostlist() {
            return postlist;
        }

        public void setPostlist(List<Post> postlist) {
            this.postlist = postlist;
        }
    }
}
