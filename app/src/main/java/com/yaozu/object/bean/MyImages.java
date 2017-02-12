package com.yaozu.object.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


/**
 * Created by jieyz on 2016/4/25.
 */
public class MyImages implements Parcelable, Serializable {
    public String userid;
    public String displayName;
    //帖子ID
    public String postid;
    public String path;
    public String imageurl_big;
    public String imageurl_small;
    public String createtime;
    public String width;
    public String height;

    public MyImages() {

    }

    protected MyImages(Parcel in) {
        userid = in.readString();
        displayName = in.readString();
        postid = in.readString();
        path = in.readString();
        imageurl_big = in.readString();
        imageurl_small = in.readString();
        createtime = in.readString();
        width = in.readString();
        height = in.readString();
    }

    public static final Creator<MyImages> CREATOR = new Creator<MyImages>() {
        @Override
        public MyImages createFromParcel(Parcel in) {
            return new MyImages(in);
        }

        @Override
        public MyImages[] newArray(int size) {
            return new MyImages[size];
        }
    };

    public String getImageurl_small() {
        return imageurl_small;
    }

    public void setImageurl_small(String imageurl_small) {
        this.imageurl_small = imageurl_small;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImageurl_big() {
        return imageurl_big;
    }

    public void setImageurl_big(String imageurl_big) {
        this.imageurl_big = imageurl_big;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(displayName);
        dest.writeString(postid);
        dest.writeString(path);
        dest.writeString(imageurl_big);
        dest.writeString(imageurl_small);
        dest.writeString(createtime);
        dest.writeString(width);
        dest.writeString(height);
    }
}
