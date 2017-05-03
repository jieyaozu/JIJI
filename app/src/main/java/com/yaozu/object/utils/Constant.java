package com.yaozu.object.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yaozu.object.R;

/**
 * Created by jxj42 on 2016/12/21.
 */

public class Constant {
    public static final int PAGE_SIZE = 15;
    public static String APP_IMAGE_HOST = "http://www.chaojijihua.com:8080/superplan/images/";
    public static final DisplayImageOptions IMAGE_OPTIONS_FOR_USER = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.icon_user_image_default)
            .showImageForEmptyUri(R.mipmap.icon_user_image_default)
            .showImageOnFail(R.mipmap.icon_user_image_default)
            .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY)
            .cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();

    public static final DisplayImageOptions IMAGE_OPTIONS_FOR_PARTNER = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY)
            .cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();

    public static final DisplayImageOptions IMAGE_OPTIONS_FOR_ROUNDCORNER = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY)
            .cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true)
            .displayer(new RoundedBitmapDisplayer(10)).build();

    public static boolean IS_CREATEGROUP_SUCCESS = false;
    public static boolean IS_EDITGROUP_SUCCESS = false;
    public static boolean IS_CLEARGROUP_MESSAGE_SUCCESS = false;
    public static boolean IS_SET_USERTYPE_SUCCESS = false;
    public static boolean SENDING_POST = false;
    public static boolean IS_EDIT_USERICON = false;//上传头像
    public static boolean IS_EDIT_USERNAME = false;// 更改名称
    public static boolean IS_DELETE_POST = false;

    public static String ACTION_PUSH_NOTIFY = "push.notify.message";

    public static final String LOGIN_MSG = "login_msg";
    public static String CRASH_MSG = "crash_msg";
    public static String SP_PLAN_MSG = "plan_msg";
    public static String IS_LOGINING = "is_logining";
    public static String USER_TOKEN = "user_token";
    public static String USER_NAME = "user_name";
    public static String USER_ICON = "user_icon";
    public static String USER_TYPE = "user_type";
    public static String USER_FROM_DEVICE = "user_from_device";
    public static String USER_SMALL_ICON = "user_small_icon";
    public static String USER_LEVEL = "user_level";
    public static String USER_VIP = "user_vip";
    public static String USER_ACCOUNT = "user_account";

    public static final String SUCCESS = "1";
    public static final String FAILED = "0";

    public static final int VIEWTYPE_HEADER = 0;
    public static final int VIEWTYPE_LIST = 1;
    public static final int VIEWTYPE_FOOTER = 2;
}
