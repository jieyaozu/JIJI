package com.yaozu.object.entity;

/**
 * Created by jxj42 on 2017/2/11.
 */

public class LoginReqData extends BaseEntity<LoginReqData.BodyEntity> {
    public class BodyEntity {
        private String code;
        private String message;
        private String username;
        private String usericon;
        private String userSicon;

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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUsericon() {
            return usericon;
        }

        public void setUsericon(String usericon) {
            this.usericon = usericon;
        }

        public String getUserSicon() {
            return userSicon;
        }

        public void setUserSicon(String userSicon) {
            this.userSicon = userSicon;
        }
    }
}
