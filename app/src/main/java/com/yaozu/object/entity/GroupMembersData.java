package com.yaozu.object.entity;

import com.yaozu.object.bean.MemberInfo;

import java.util.List;

/**
 * Created by jxj42 on 2017/4/22.
 */

public class GroupMembersData extends BaseEntity<GroupMembersData.BodyEntity> {
    public class BodyEntity {
        private String code;
        private String message;
        private List<MemberInfo> memInfos;

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

        public List<MemberInfo> getMemInfos() {
            return memInfos;
        }

        public void setMemInfos(List<MemberInfo> memInfos) {
            this.memInfos = memInfos;
        }
    }
}
