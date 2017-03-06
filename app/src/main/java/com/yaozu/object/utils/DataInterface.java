package com.yaozu.object.utils;

/**
 * Created by jxj42 on 2017/2/11.
 */

public class DataInterface {
    //生产地址
    public static String APP_HOST = "http://www.chaojijihua.com:8080/";
    public static String SERVER_APP_HOST = "http://www.chaojijihua.com:8080/";
    //测试地址
    //public static String APP_HOST = "http://192.168.0.104:8080/";
    //查询用户详情
    public static String FIND_USERINFO = APP_HOST + "superplan/user/finduser.do?method=finduserinfo&";
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
    //查找单个回复贴子详情
    public static String FIND_REPLY_POST = APP_HOST + "superplan/post/post.do?method=findpost&";
    //查找单个主题贴子详情
    public static String FIND_THEME_POST = APP_HOST + "superplan/post/post.do?method=findthemepost&";
    //查找用户主题帖子列表
    public static String FIND_USER_POST_LIST = APP_HOST + "superplan/post/post.do?method=finduserlist&";
    //查找用户跟帖列表
    public static String FIND_USER_REPLYPOST_LIST = APP_HOST + "superplan/post/post.do?method=finduserReplylist&";

    //评论
    public static String ADD_POSTREPLY_COMMENT = APP_HOST + "superplan/post/comment.do?method=addcomment&";
    public static String FIND_POSTREPLY_COMMENT = APP_HOST + "superplan/post/comment.do?method=findPostreplyComments&";

    //点赞
    public static String ADD_POST_PRAISE = APP_HOST + "superplan/post/praise.do?method=addpraise&";

    //收藏
    public static String ADD_COLLECT = APP_HOST + "superplan/post/collect.do?method=addcollect&";
    //取消收藏
    public static String REMOVE_COLLECT = APP_HOST + "superplan/post/collect.do?method=removecollect&";
    //是否收藏
    public static String IS_COLLECT = APP_HOST + "superplan/post/collect.do?method=iscollect&";
    //查找用户收藏的贴子
    public static String FIND_COLLECT_POST = APP_HOST + "superplan/post/collect.do?method=findcollectpost&";
    //修改用户账户类型 userid,type
    public static String CHANGE_ACCOUNT_TYPE = APP_HOST + "superplan/user/finduser.do?method=updateusertype&";
}
