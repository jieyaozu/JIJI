package com.yaozu.object.pushreceiver;

import com.yaozu.object.bean.GroupMessage;

/**
 * Created by jxj42 on 2017/5/6.
 */

public class PushDataBean {
    private String pushtype;
    private Content content;

    public String getPushtype() {
        return pushtype;
    }

    public void setPushtype(String pushtype) {
        this.pushtype = pushtype;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public class Content {
        /**
         * 0为新发贴子群成员提醒，1为回复我的贴子提醒，2为评论我的帖子提醒，3为点赞提醒
         */
        private String msgtype;
        private String groupid;
        private String data;
        private GroupMessage groupMessageBean;

        public GroupMessage getGroupMessageBean() {
            return groupMessageBean;
        }

        public void setGroupMessageBean(GroupMessage groupMessageBean) {
            this.groupMessageBean = groupMessageBean;
        }

        public String getMsgtype() {
            return msgtype;
        }

        public void setMsgtype(String msgtype) {
            this.msgtype = msgtype;
        }

        public String getGroupid() {
            return groupid;
        }

        public void setGroupid(String groupid) {
            this.groupid = groupid;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
