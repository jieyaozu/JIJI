package com.jiji.iready.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.jiji.iready.R;
import com.jiji.iready.SettingsActivity2;

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
}
