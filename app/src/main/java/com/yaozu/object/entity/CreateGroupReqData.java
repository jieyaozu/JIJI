package com.yaozu.object.entity;

/**
 * Created by jxj42 on 2017/4/8.
 */

public class CreateGroupReqData extends BaseEntity<CreateGroupReqData.BodyEntity> {
    public class BodyEntity {
        private String code;
        private String message;
        private String status;
        private String groupid;

        public void setCode(String code) {
            this.code = code;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setStatus(String status) {
            this.status = status;
        }


        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public String getStatus() {
            return status;
        }

        public String getGroupid() {
            return groupid;
        }

        public void setGroupid(String groupid) {
            this.groupid = groupid;
        }
    }
}
