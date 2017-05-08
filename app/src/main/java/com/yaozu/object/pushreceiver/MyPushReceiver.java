package com.yaozu.object.pushreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yaozu.object.bean.GroupMessage;
import com.yaozu.object.bean.MessageBean;
import com.yaozu.object.bean.constant.GMStatus;
import com.yaozu.object.db.dao.GroupDao;
import com.yaozu.object.db.dao.MessageBeanDao;
import com.yaozu.object.pushreceiver.remind.NewPostRemind;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.MsgType;

/**
 * Created by jieyaozu on 2017/5/3.
 */

public class MyPushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String jsonString = intent.getStringExtra(IntentKey.INTENT_PUSH_MSG_DATA);
        Gson gson = new Gson();
        PushDataBean pushDataBean = gson.fromJson(jsonString, PushDataBean.class);
        String pushType = pushDataBean.getPushtype();
        if ("0".equals(pushType)) {//提醒推送消息
            String msgtype = pushDataBean.getContent().getMsgtype();
            if ("0".equals(msgtype)) {//0为新发贴子群成员提醒
                String groupid = pushDataBean.getContent().getGroupid();
                int number = NewPostRemind.getInstance(context).getRemindNumber(groupid);
                number++;
                NewPostRemind.getInstance(context).putRemind(groupid, number);
            } else if ("1".equals(msgtype)) {//1为回复我的贴子提醒
                System.out.println("=======回复我的贴子提醒=========>");
            } else if ("2".equals(msgtype)) {//2为评论我的帖子提醒
                System.out.println("=======评论我的帖子提醒=========>");
            } else if ("3".equals(msgtype)) {//3为点赞提醒
                System.out.println("=======点赞提醒=========>");
            } else if ("4".equals(msgtype)) {//群消息提醒
                GroupMessage groupMessage = pushDataBean.getContent().getGroupMessageBean();
                GroupDao groupDao = new GroupDao(context);
                if (groupMessage != null) {
                    boolean action = groupDao.addGroupMessage(groupMessage);

                    MessageBeanDao messageBeanDao = new MessageBeanDao(context);
                    MessageBean messageBean = messageBeanDao.findMessageBean(MsgType.TYPE_GROUP);
                    if (GMStatus.APPLYING.equals(groupMessage.getStatus())) {
                        messageBean.setAdditional(groupMessage.getUsername() + "申请加入" + groupMessage.getGroupname());
                    } else if (GMStatus.EXIT.equals(groupMessage.getStatus())) {
                        messageBean.setAdditional("退出群通知");
                    }
                    messageBean.setNewMsgnumber(messageBean.getNewMsgnumber() + (action ? 1 : 0));
                    messageBeanDao.updateBean(messageBean);
                    //发个广播更新下UI
                    Intent playingintent = new Intent(IntentKey.NOTIFY_MESSAGE_REMIND);
                    LocalBroadcastManager playinglocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                    playinglocalBroadcastManager.sendBroadcast(playingintent);
                }
            }
        } else {

        }
    }
}
