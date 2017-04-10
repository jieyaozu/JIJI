package com.yaozu.object.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.yaozu.object.R;
import com.yaozu.object.SettingsActivity2;
import com.yaozu.object.activity.CollectActivity;
import com.yaozu.object.activity.CreateGroupActivity;
import com.yaozu.object.activity.PostDetailActivity;
import com.yaozu.object.activity.PostReplyDetailActivity;
import com.yaozu.object.activity.ScannerPictureActivity;
import com.yaozu.object.activity.SendPostActivity;
import com.yaozu.object.activity.ThemePostActivity;
import com.yaozu.object.activity.WebViewActivity;
import com.yaozu.object.activity.group.GroupDetailActivity;
import com.yaozu.object.activity.group.GroupOfPostActivity;
import com.yaozu.object.activity.user.LoginActivity;
import com.yaozu.object.activity.user.RegisterActivity;
import com.yaozu.object.activity.user.UserInfoActivity;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.bean.Post;

import java.util.ArrayList;

/**
 * Created by jxj42 on 2016/12/22.
 */

public class IntentUtil {
    public static void toSettingActivity(Context context) {
        Intent setting = new Intent(context, SettingsActivity2.class);
        context.startActivity(setting);
        overridePendingTransition(context);
    }

    private static void overridePendingTransition(Context context) {
        ((FragmentActivity) context).overridePendingTransition(R.anim.right_enter_page, R.anim.right_quit_page);
    }

    public static void toScannerPictureActivity(Context context, ArrayList<MyImage> imagesList, int position) {
        Intent intent = new Intent(context, ScannerPictureActivity.class);
        intent.putParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES, imagesList);
        intent.putExtra(IntentKey.INTENT_ALBUM_IMAGES_INDEX, position);
        context.startActivity(intent);
    }

    public static void toSendPostActivity(Context context, String groupid) {
        Intent sendPost = new Intent(context, SendPostActivity.class);
        sendPost.putExtra(IntentKey.INTENT_IS_EDIT_POST, false);
        sendPost.putExtra(IntentKey.INTENT_GROUP_ID, groupid);
        context.startActivity(sendPost);
        overridePendingTransition(context);
    }

    public static void toEditSendPostActivity(Context context, Post post) {
        Intent sendPost = new Intent(context, SendPostActivity.class);
        sendPost.putExtra(IntentKey.INTENT_POST, post);
        sendPost.putExtra(IntentKey.INTENT_IS_EDIT_POST, true);
        context.startActivity(sendPost);
        overridePendingTransition(context);
    }

    public static void toPostDetailActivity(Context context, Post post) {
        Intent postDetail = new Intent(context, PostDetailActivity.class);
        postDetail.putExtra(IntentKey.INTENT_POST, post);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }

    public static void toPostDetailActivity(Context context, String postid) {
        Intent postDetail = new Intent(context, PostDetailActivity.class);
        postDetail.putExtra(IntentKey.INTENT_POST_ID, postid);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }

    public static void toPostReplyDetailActivity(Context context, Post post, String mainuserid, int index) {
        Intent postDetail = new Intent(context, PostReplyDetailActivity.class);
        postDetail.putExtra(IntentKey.INTENT_POST, post);
        postDetail.putExtra(IntentKey.INTENT_POST_POSITION, index);
        postDetail.putExtra(IntentKey.INTENT_USERID, mainuserid);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }

    public static void toLoginActivity(Context context) {
        Intent postDetail = new Intent(context, LoginActivity.class);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }

    public static void toRegisterActivity(Context context) {
        Intent postDetail = new Intent(context, RegisterActivity.class);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }

    public static void toUserInfoActivity(Context context, String userid) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(IntentKey.INTENT_USERID, userid);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    public static void toCollectActivity(Context context) {
        Intent intent = new Intent(context, CollectActivity.class);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    public static void toThemePostActivity(Context context, String userid) {
        Intent intent = new Intent(context, ThemePostActivity.class);
        intent.putExtra(IntentKey.INTENT_USERID, userid);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    public static void toWebViewActivity(Context context, String postid) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(IntentKey.INTENT_POST_ID, postid);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    /**
     * 创建群
     *
     * @param context
     */
    public static void toCreatGroupActivity(Context context) {
        Intent intent = new Intent(context, CreateGroupActivity.class);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    /**
     * 群内帖子页面
     *
     * @param context
     * @param groupBean
     */
    public static void toGroupOfPostActivity(Context context, GroupBean groupBean) {
        Intent intent = new Intent(context, GroupOfPostActivity.class);
        intent.putExtra(IntentKey.INTENT_GROUP, groupBean);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    /**
     * 群详情页面
     *
     * @param context
     * @param groupBean
     */
    public static void toGroupDetailActivity(Context context, GroupBean groupBean) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(IntentKey.INTENT_GROUP, groupBean);
        context.startActivity(intent);
    }
}
