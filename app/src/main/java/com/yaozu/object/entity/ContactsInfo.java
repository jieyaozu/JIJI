package com.yaozu.object.entity;

/**
 * Created by jxj42 on 2017/1/24.
 */

public class ContactsInfo {
    private String name;
    private String number;
    private String sortKey;
    private int id;


    public ContactsInfo(String name, String number, String sortKey, int id) {
        setName(name);
        setNumber(number);
        setSortKey(sortKey);
        setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
