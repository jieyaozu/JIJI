package com.yaozu.object.entity;

import com.yaozu.object.bean.GroupMessage;

import java.util.List;

/**
 * Created by jxj42 on 2017/4/15.
 */

public class ApplyGroupData extends BaseEntity<ApplyGroupData.BodyEntity> {
    public class BodyEntity {
        private String code;
        private String message;
        private List<GroupMessage> applybeans;

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

        public List<GroupMessage> getApplybeans() {
            return applybeans;
        }

        public void setApplybeans(List<GroupMessage> applybeans) {
            this.applybeans = applybeans;
        }
    }
}
