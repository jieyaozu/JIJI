package com.yaozu.object.utils;

import android.graphics.Bitmap;

/**
 * Created by jxj42 on 2017/2/7.
 */

public class IntentKey {
    //从图册里选取的位图
    public static Bitmap cropBitmap;
    public static final String HAVE_SELECTED_COUNT = "have_selected_count";
    public static final String INTENT_SELECT_ALBUM_SINGLE = "intent_select_album_single";
    public static final String INTENT_ALBUM_IMAGES = "intent_album_images";

    public static final String INTENT_POST = "intent_post";
    public static final String INTENT_POST_ID = "intent_post_id";
    public static final String INTENT_USERID = "intent_userid";
    public static String USER_ICON_PATH = "user_icon_path";
    public static final String INTENT_USERNAME = "intent_username";
    public static final String INTENT_POST_POSITION = "intent_post_position";
    public static final String INTENT_ALBUM_IMAGES_INDEX = "intent_album_images_index";
    public static final String INTENT_IS_EDIT_POST = "intent_is_edit_post";
    public static final String INTENT_GROUP = "intent_group";
    public static final String INTENT_GROUP_ID = "intent_group_id";
    public static final String INTENT_SECTION_ID = "intent_section_id";
    public static final String INTENT_SECTION_NAME = "intent_section_name";
    public static final String INTENT_NICKNAME = "intent_nickname";

    public static final String NOTIFY_UPLOAD_IMAGE_SUCCESS = "notify_upload_image_success";//上传成功的广播
    public static final String NOTIFY_UPLOAD_IMAGE_FAILED = "notify_upload_image_failed";//上传失败的广播
}
