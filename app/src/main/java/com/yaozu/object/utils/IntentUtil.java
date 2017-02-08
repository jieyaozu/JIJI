package com.yaozu.object.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.yaozu.object.R;
import com.yaozu.object.SettingsActivity2;
import com.yaozu.object.activity.PostDetailActivity;
import com.yaozu.object.activity.SendPostActivity;

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

    public static void toSendPostActivity(Context context) {
        Intent sendPost = new Intent(context, SendPostActivity.class);
        context.startActivity(sendPost);
        overridePendingTransition(context);
    }

    public static void toPostDetailActivity(Context context) {
        Intent postDetail = new Intent(context, PostDetailActivity.class);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }
}
