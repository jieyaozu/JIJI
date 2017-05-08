package com.yaozu.object.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.yaozu.object.ObjectApplication;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;

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

    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        if (LoginInfo.getInstance(this).isLogining()) {
            requestBindUserid(LoginInfo.getInstance(this).getUserAccount(), clientid);
        } else {
            ObjectApplication.clientid = clientid;
        }
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        String jsonString = new String(gtTransmitMessage.getPayload());
        Intent intent = new Intent(Constant.ACTION_PUSH_NOTIFY);
        intent.putExtra(IntentKey.INTENT_PUSH_MSG_DATA, jsonString);
        sendBroadcast(intent);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b) {

    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {

    }

    /**
     * 关联用户id
     *
     * @param userid
     * @param clientid
     */
    private void requestBindUserid(String userid, String clientid) {
        String url = DataInterface.BIND_USERID_CLIENTID + "userid=" + userid + "&clientid=" + clientid;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    Log.d("MyIntentService:", requestData.getBody().getMessage());
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }
}
