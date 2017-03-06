package com.yaozu.object.entity;

import com.yaozu.object.bean.Post;

import java.util.List;

/**
 * Created by jxj42 on 2017/2/9.
 */

public class HomeForumDataInfo extends BaseEntity<HomeForumDataInfo.BodyEntity> {
    public class BodyEntity {
        private String code;
        private String message;
        private List<Post> toplist;
        private List<Post> postlist;

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
