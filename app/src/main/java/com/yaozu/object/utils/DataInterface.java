package com.yaozu.object.utils;

/**
 * Created by jxj42 on 2017/2/11.
 */

public class DataInterface {
    //生产地址
    public static String APP_HOST = "http://www.chaojijihua.com:8080/";
    public static String SERVER_APP_HOST = "http://www.chaojijihua.com:8080/";
    //测试地址
    //public static String APP_HOST = "http://192.168.0.105:8080/";
    //查询用户详情
    public static String FIND_USERINFO = APP_HOST + "superplan/user/finduser.do?method=finduserinfo&";
    public static String LOGIN_URL = APP_HOST + "superplan/login/login.do?method=dologin&";
    public static String REGISTER_URL = APP_HOST + "superplan/register/register.do?method=doregister&";
    public static String UPLOAD_IMAGES = APP_HOST + "superplan/upload/image.do?method=upload&";
    //修改密码
    public static String CHANGE_PASSWORD = APP_HOST + "superplan/user/finduser.do?method=changePwd&";
    //修改用户名
    public static String RENAME_USERNAME = APP_HOST + "/superplan/servlet/UpdateUserInfoServlet";
    //群封面图
    public static String UPLOAD_GROUP_IMAGES = APP_HOST + "superplan/upload/image.do?method=uploadGroupIcon&";
    public static String UPLOAD_USERICON = APP_HOST + "superplan/upload/usericon.do?method=upload&";
    //发表贴子
    public static String ADD_POST = APP_HOST + "superplan/post/post.do?method=add&";
    //回复贴子
    public static String REPLY_ADD_POST = APP_HOST + "superplan/post/post.do?method=replypost&";
    //查找首页贴子列表
    public static String FIND_HOME_POST_LIST = APP_HOST + "superplan/post/post.do?method=findHomelist&";
    //查找群内贴子列表
    public static String FIND_GROUP_POST_LIST = APP_HOST + "superplan/post/post.do?method=findGrouplist&";
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

    //查找我的群组
    public static String FIND_MY_GROUP = APP_HOST + "superplan/app/group.do?method=findmygroup&";

    //查找所有版块
    public static String FIND_ALL_SECTION = APP_HOST + "superplan/app/section.do?method=findall";
    //创建群
    public static String CREATE_MY_GROUP = APP_HOST + "superplan/app/group.do?method=create&";
    //查找群的详情
    public static String FIND_GROUP_BY_ID = APP_HOST + "superplan/app/group.do?method=findgroup&";
    //编辑更改群名称和群介绍
    public static String EDIT_GROUP = APP_HOST + "superplan/app/group.do?method=editgroup&";

    //查找版块下面的群
    public static String FIND_SECTION_OF_GROUP = APP_HOST + "superplan/app/group.do?method=findSectionOfGroup&";
    //申请加入某群
    public static String APPLY_ENTER_GROUP = APP_HOST + "superplan/app/group.do?method=pushMessageGroup&";
    //查找申请入群的消息(群管理员才会调用)
    public static String FIND_APPLY_ENTER_GROUP_MSG = APP_HOST + "superplan/app/group.do?method=findMsg&";
    //加入群
    public static String JOIN_GROUP = APP_HOST + "superplan/app/group.do?method=addToGroup&";
    //退出群
    public static String EXIT_GROUP = APP_HOST + "superplan/app/group.do?method=exitGroup&";
    //清除群消息
    public static String CLEAR_GROUP_MSG = APP_HOST + "superplan/app/group.do?method=clearGroupMsg&";
    //搜索群
    public static String SEARCH_GROUP = APP_HOST + "superplan/app/group.do?method=searchGroup&";
    //更改贴子的状态
    public static String UPDATE_POST_STATUS = APP_HOST + "superplan/post/post.do?method=updatePostStatus&";
    // 查找群内所有的成员
    public static String FIND_GROUP_MEMBERS = APP_HOST + "superplan/app/group.do?method=findGroupMembers&";
    // 编辑群名片
    public static String EDIT_NICKNAME = APP_HOST + "superplan/app/group.do?method=editnickname&";
    //查询群成员类型
    public static String SELECT_USERTYPE = APP_HOST + "superplan/app/group.do?method=isAdmin&";
    //设置用户成员类型
    public static String SET_USERTYPE = APP_HOST + "superplan/app/group.do?method=setAdmin&";
    //编辑贴子
    public static String EDIT_POST = APP_HOST + "superplan/post/post.do?method=editPost&";
    //删除单张图片
    public static String DELETE_IMAGE = APP_HOST + "superplan/post/post.do?method=deleteimage&";
    //删除贴子
    public static String DELETE_POST = APP_HOST + "superplan/post/post.do?method=deletePost&";
}
