package com.yaozu.object.utils;

/**
 * Created by jxj42 on 2017/2/11.
 */

public class DataInterface {
    //生产地址
    public static String APP_HOST = "http://www.chaojijihua.com:8080/";
    public static String SERVER_APP_HOST = "http://www.chaojijihua.com:8080/";
    //测试地址
    //public static String APP_HOST = "http://192.168.0.103:8080/";

    public static String LOGIN_URL = APP_HOST + "superplan/login/login.do?method=dologin&";
    public static String REGISTER_URL = APP_HOST + "superplan/register/register.do?method=doregister&";
    public static String UPLOAD_IMAGES = APP_HOST + "superplan/upload/image.do?method=upload&";
    public static String UPLOAD_USERICON = APP_HOST + "superplan/upload/usericon.do?method=upload&";
    //发表贴子
    public static String ADD_POST = APP_HOST + "superplan/post/post.do?method=add&";
    //回复贴子
    public static String REPLY_ADD_POST = APP_HOST + "superplan/post/post.do?method=replypost&";
    //查找首页贴子列表
    public static String FIND_HOME_POST_LIST = APP_HOST + "superplan/post/post.do?method=findHomelist&";
    //查找所有回贴
    public static String FIND_DETAIL_REPLY_POST_LIST = APP_HOST + "superplan/post/post.do?method=findreplypost&";
    //查找单个贴子详情
    public static String FIND_POST = APP_HOST + "superplan/post/post.do?method=findpost&";

    public static String ADD_POSTREPLY_COMMENT = APP_HOST + "superplan/post/comment.do?method=addcomment&";
    public static String FIND_POSTREPLY_COMMENT = APP_HOST + "superplan/post/comment.do?method=findPostreplyComments&";
}
