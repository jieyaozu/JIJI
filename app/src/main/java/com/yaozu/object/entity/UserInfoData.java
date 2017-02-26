package com.yaozu.object.entity;

import com.yaozu.object.bean.UserInfo;

/**
 * Created by jxj42 on 2017/2/25.
 */

public class UserInfoData extends BaseEntity<UserInfoData.BodyEntity> {

    public class BodyEntity {
        private String code;
        private String message;
        private UserInfo userinfo;

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

        public UserInfo getUserinfo() {
            return userinfo;
        }

        public void setUserinfo(UserInfo userinfo) {
            this.userinfo = userinfo;
        }
    }
}
