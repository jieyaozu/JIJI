package com.yaozu.object.entity;

import com.yaozu.object.bean.GroupBean;

import java.util.List;

/**
 * Created by jxj42 on 2017/4/4.
 */

public class GroupReqData extends BaseEntity<GroupReqData.BodyEntity> {
    public class BodyEntity {
        private String code;
        private String message;
        private List<GroupBean> grList;

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

        public List<GroupBean> getGrList() {
            return grList;
        }

        public void setGrList(List<GroupBean> grList) {
            this.grList = grList;
        }
    }
}
