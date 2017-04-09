package com.yaozu.object.bean;

/**
 * Created by jxj42 on 2017/4/8.
 */

public class PermissionBean {
    private String permissioncode;
    private String permissionname;

    public PermissionBean(String code, String name) {
        this.permissioncode = code;
        this.permissionname = name;
    }

    public String getPermissioncode() {
        return permissioncode;
    }

    public void setPermissioncode(String permissioncode) {
        this.permissioncode = permissioncode;
    }

    public String getPermissionname() {
        return permissionname;
    }

    public void setPermissionname(String permissionname) {
        this.permissionname = permissionname;
    }
}
