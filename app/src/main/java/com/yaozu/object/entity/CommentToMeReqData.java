package com.yaozu.object.entity;

import com.yaozu.object.bean.Comment;

import java.util.List;

/**
 * Created by jieyaozu on 2017/5/8.
 */

public class CommentToMeReqData extends BaseEntity<CommentToMeReqData.BodyEntity> {
    public class BodyEntity {
        private String code;
        private String message;
        private List<Comment> commentList;

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

        public List<Comment> getCommentList() {
            return commentList;
        }

        public void setCommentList(List<Comment> commentList) {
            this.commentList = commentList;
        }
    }
}
