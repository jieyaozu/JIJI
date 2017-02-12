package com.yaozu.object.utils;

/**
 * Created by jxj42 on 2017/2/11.
 */

public class DataInterface {
    //生产地址
    //public static String APP_HOST = "http://www.chaojijihua.com:8080/";
    public static String SERVER_APP_HOST = "http://www.chaojijihua.com:8080/";
    //测试地址
    public static String APP_HOST = "http://192.168.0.103:8080/";

    public static String LOGIN_URL = APP_HOST + "superplan/login/login.do?method=dologin&";
    public static String REGISTER_URL = APP_HOST + "superplan/register/register.do?method=doregister&";
    public static String UPLOAD_IMAGES = APP_HOST + "superplan/upload/image.do?method=upload&";
    public static String UPLOAD_USERICON = APP_HOST + "superplan/upload/usericon.do?method=upload&";
    public static String ADD_POST = APP_HOST + "superplan/post/post.do?method=add&";
    public static String FIND_HOME_POST_LIST = APP_HOST + "superplan/post/post.do?method=findHomelist&";
}
