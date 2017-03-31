package com.yaozu.object.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.yaozu.object.R;
import com.yaozu.object.SettingsActivity2;
import com.yaozu.object.activity.CollectActivity;
import com.yaozu.object.activity.PostDetailActivity;
import com.yaozu.object.activity.PostReplyDetailActivity;
import com.yaozu.object.activity.ScannerPictureActivity;
import com.yaozu.object.activity.SendPostActivity;
import com.yaozu.object.activity.ThemePostActivity;
import com.yaozu.object.activity.WebViewActivity;
import com.yaozu.object.activity.user.LoginActivity;
import com.yaozu.object.activity.user.RegisterActivity;
import com.yaozu.object.activity.user.UserInfoActivity;
import com.yaozu.object.bean.MyImages;
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

    public static void toScannerPictureActivity(Context context, ArrayList<MyImages> imagesList, int position) {
        Intent intent = new Intent(context, ScannerPictureActivity.class);
        intent.putParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES, imagesList);
        intent.putExtra(IntentKey.INTENT_ALBUM_IMAGES_INDEX, position);
        context.startActivity(intent);
    }

    public static void toSendPostActivity(Context context) {
        Intent sendPost = new Intent(context, SendPostActivity.class);
        context.startActivity(sendPost);
        overridePendingTransition(context);
    }

    public static void toSendPostActivity(Context context, Post post) {
        Intent sendPost = new Intent(context, SendPostActivity.class);
        sendPost.putExtra(IntentKey.INTENT_POST, post);
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
}
