package com.yaozu.object.entity;

import com.yaozu.object.bean.GroupBean;

/**
 * Created by jxj42 on 2017/4/9.
 */

public class GroupBeanReqData extends BaseEntity<GroupBeanReqData.BodyEntity> {
    public class BodyEntity {
        private String code;
        private String message;
        private GroupBean groupbean;

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

        public GroupBean getGroupbean() {
            return groupbean;
        }

        public void setGroupbean(GroupBean groupbean) {
            this.groupbean = groupbean;
        }
    }
}
