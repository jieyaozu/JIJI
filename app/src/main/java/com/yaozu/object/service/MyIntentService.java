package com.yaozu.object.service;

import android.content.Context;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;

/**
 * Created by jxj42 on 2017/5/4.
 * <p>
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class MyIntentService extends GTIntentService {
    @Override
    public void onReceiveServicePid(Context context, int i) {
        System.out.println("=========onReceiveServicePid=========>" + i);
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        System.out.println("=========onReceiveClientId=========>" + clientid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        System.out.println("=========onReceiveMessageData=========>" + new String(gtTransmitMessage.getPayload()));
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b) {
        System.out.println("=========onReceiveOnlineState=========>" + b);
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {
        System.out.println("=========onReceiveMessageData=========>" + gtCmdMessage.getClientId());
    }
}
