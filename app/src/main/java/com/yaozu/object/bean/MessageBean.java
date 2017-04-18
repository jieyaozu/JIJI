package com.yaozu.object.bean;

import java.io.Serializable;

/**
 * Created by jieyaozu on 2017/4/17.
 */

public class MessageBean implements Serializable{
    //消息类型
    private String type;
    //消息标题
    private String title;
    //消息附加说明
    private String additional;
    //新消息数量
    private int newMsgnumber;
    //消息图标
    private String icon;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public int getNewMsgnumber() {
        return newMsgnumber;
    }

    public void setNewMsgnumber(int newMsgnumber) {
        this.newMsgnumber = newMsgnumber;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
