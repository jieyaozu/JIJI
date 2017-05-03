package com.yaozu.object.pushreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.yaozu.object.utils.IntentKey;

/**
 * Created by jieyaozu on 2017/5/3.
 */

public class MyPushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra(IntentKey.INTENT_PUSH_NOTIFY_BUNDLE);
        String msgType = bundle.getString(IntentKey.INTENT_PUSH_MSG_TYPE);
        Toast.makeText(context, "====onReceive====>", Toast.LENGTH_SHORT).show();
    }
}
