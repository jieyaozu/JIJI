package com.yaozu.object;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.bean.GroupMessage;
import com.yaozu.object.bean.MessageBean;
import com.yaozu.object.bean.UserInfo;
import com.yaozu.object.db.dao.MessageBeanDao;
import com.yaozu.object.entity.ApplyGroupData;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.UserInfoData;
import com.yaozu.object.fragment.ForumFragment;
import com.yaozu.object.fragment.GroupFragment;
import com.yaozu.object.fragment.MessageFragment;
import com.yaozu.object.fragment.MineFragment;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.service.GetuiPushService;
import com.yaozu.object.service.MyIntentService;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.MsgType;

import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Dialog dialog;
    private String currentContentFragmentTag = null;
    private RadioGroup mRadioGroup;
    private ActionBar mActionbar;
    private RadioButton rbForum, rbGroup, rbMessage, rbMine;
    /**
     * 消息提示的小红点
     */
    private ImageView ivForumdot, ivGroupdot, ivMessagedot, ivMinedot;

    private MessageBeanDao messageBeanDao;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
        // com.getui.demo.DemoPushService 为第三方自定义推送服务
        PushManager.getInstance().initialize(this.getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), MyIntentService.class);
        setSwipeBackEnable(false);
        messageBeanDao = new MessageBeanDao(this);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        mActionbar = actionBar;
        mActionbar.setElevation(0);
        mActionbar.hide();
        actionBar.setTitle("主页");
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_compass);
    }

    @Override
    protected void initView() {
        mRadioGroup = (RadioGroup) findViewById(R.id.main_bottom_layout_group);
    }

    @Override
    protected void initData() {
        updateContent(0);
        ((RadioButton) findViewById(R.id.main_bottom_raido_forum)).setChecked(true);
        rbForum = (RadioButton) findViewById(R.id.main_bottom_raido_forum);
        rbGroup = (RadioButton) findViewById(R.id.main_bottom_raido_find);
        rbMessage = (RadioButton) findViewById(R.id.main_bottom_raido_message);
        rbMine = (RadioButton) findViewById(R.id.main_bottom_raido_mine);
        //小红点
        ivForumdot = (ImageView) findViewById(R.id.main_forum_hava_unread);
        ivGroupdot = (ImageView) findViewById(R.id.main_group_hava_unread);
        ivMessagedot = (ImageView) findViewById(R.id.main_message_have_unread);
        ivMinedot = (ImageView) findViewById(R.id.main_myself_have_unread);

        rbForum.setTextColor(getResources().getColor(R.color.colorPrimary));
        //检查用户信息
        requestFindUserinfo(LoginInfo.getInstance(this).getUserAccount());

        requestFindGroupMessage();
    }

    @Override
    protected void setListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_bottom_raido_forum:
                        mActionbar.setTitle("主页");
                        updateContent(0);
                        rbForum.setTextColor(getResources().getColor(R.color.colorPrimary));
                        rbGroup.setTextColor(getResources().getColor(R.color.gray));
                        rbMessage.setTextColor(getResources().getColor(R.color.gray));
                        rbMine.setTextColor(getResources().getColor(R.color.gray));
                        break;
                    case R.id.main_bottom_raido_find:
                        if (!LoginInfo.getInstance(MainActivity.this).isLogining()) {
                            IntentUtil.toLoginActivity(MainActivity.this);
                            RadioButton radioButton = (RadioButton) mRadioGroup.findViewById(R.id.main_bottom_raido_forum);
                            radioButton.setChecked(true);
                        } else {
                            mActionbar.setTitle("群组");
                            updateContent(1);
                            rbForum.setTextColor(getResources().getColor(R.color.gray));
                            rbGroup.setTextColor(getResources().getColor(R.color.colorPrimary));
                            rbMessage.setTextColor(getResources().getColor(R.color.gray));
                            rbMine.setTextColor(getResources().getColor(R.color.gray));
                        }
                        break;
                    case R.id.main_bottom_raido_message:
                        if (!LoginInfo.getInstance(MainActivity.this).isLogining()) {
                            IntentUtil.toLoginActivity(MainActivity.this);
                            RadioButton radioButton = (RadioButton) mRadioGroup.findViewById(R.id.main_bottom_raido_forum);
                            radioButton.setChecked(true);
                        } else {
                            mActionbar.setTitle("消息");
                            updateContent(2);
                            rbForum.setTextColor(getResources().getColor(R.color.gray));
                            rbGroup.setTextColor(getResources().getColor(R.color.gray));
                            rbMessage.setTextColor(getResources().getColor(R.color.colorPrimary));
                            rbMine.setTextColor(getResources().getColor(R.color.gray));
                        }
                        break;
                    case R.id.main_bottom_raido_mine:
                        if (!LoginInfo.getInstance(MainActivity.this).isLogining()) {
                            IntentUtil.toLoginActivity(MainActivity.this);
                            RadioButton radioButton = (RadioButton) mRadioGroup.findViewById(R.id.main_bottom_raido_forum);
                            radioButton.setChecked(true);
                        } else {
                            mActionbar.setTitle("我");
                            updateContent(3);
                            rbForum.setTextColor(getResources().getColor(R.color.gray));
                            rbGroup.setTextColor(getResources().getColor(R.color.gray));
                            rbMessage.setTextColor(getResources().getColor(R.color.gray));
                            rbMine.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                        break;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                return true;
            case R.id.action_login:
                IntentUtil.toLoginActivity(this);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void updateContent(int checkedId) {
        final Fragment fragment;
        final String tag;
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction tr = fm.beginTransaction();
        if (currentContentFragmentTag != null) {
            final Fragment currentFragment = fm.findFragmentByTag(currentContentFragmentTag);
            if (currentFragment != null)
                tr.hide(currentFragment);
        }
        if (checkedId == 0) {
            tag = ForumFragment.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
            } else {
                fragment = new ForumFragment();
            }
        } else if (checkedId == 1) {
            tag = GroupFragment.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
            } else {
                fragment = new GroupFragment();
            }
        } else if (checkedId == 2) {
            tag = MessageFragment.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
            } else {
                fragment = new MessageFragment();
            }
        } else if (checkedId == 3) {
            tag = MineFragment.TAG;
            final Fragment foundFragment = fm.findFragmentByTag(tag);
            if (foundFragment != null) {
                fragment = foundFragment;
            } else {
                fragment = new MineFragment();
            }
        } else {
            return;
        }

        if (fragment.isAdded()) {
            tr.show(fragment);
        } else {
            tr.add(R.id.fragment_container, fragment, tag);
            // tr.replace(R.id.fragment_content, fragment);
        }
        tr.commit();
        currentContentFragmentTag = tag;
    }

    private void requestFindUserinfo(String userid) {
        String url = DataInterface.FIND_USERINFO + "userid=" + userid;
        RequestManager.getInstance().getRequest(this, url, UserInfoData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    UserInfoData userInfo = (UserInfoData) object;
                    UserInfo user = userInfo.getBody().getUserinfo();
                    LoginInfo.getInstance(MainActivity.this).storeLoginUserInfo(true, user.getType() + "", user.getUserid(), user.getUsername(), user.getIconpath(), user.getSiconpath());
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private static String getSortkey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        } else
            return "#";   //获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
    }

    private void showObtainPermissionDialog() {
        dialog = new Dialog(this, R.style.NobackDialog);
        View view = View.inflate(this, R.layout.dialog_show_obtain_permission, null);
        TextView cancel = (TextView) view.findViewById(R.id.dialog_cancel);
        TextView obtain = (TextView) view.findViewById(R.id.dialog_obtain);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        obtain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = getAppDetailSettingIntent();
                startActivity(intent);
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        return localIntent;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 查找有没有群消息
     */
    private void requestFindGroupMessage() {
        String url = DataInterface.FIND_APPLY_ENTER_GROUP_MSG + "userid=" + LoginInfo.getInstance(this).getUserAccount();
        RequestManager.getInstance().getRequest(this, url, ApplyGroupData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    ApplyGroupData applyGroupData = (ApplyGroupData) object;
                    List<GroupMessage> applyList = applyGroupData.getBody().getApplybeans();
                    if (applyList != null && applyList.size() > 0) {
                        GroupMessage groupMessage = applyList.get(applyList.size() - 1);
                        MessageBean messageBean = messageBeanDao.findFriend(MsgType.TYPE_GROUP);
                        if (messageBean == null) {
                            messageBean = new MessageBean();
                            if ("applying".equals(groupMessage.getStatus())) {
                                messageBean.setAdditional(groupMessage.getUsername() + "申请加入" + groupMessage.getGroupname());
                            } else if ("exit".equals(groupMessage.getStatus())) {
                                messageBean.setAdditional(groupMessage.getUsername() + "已退出" + groupMessage.getGroupname());
                            }
                            messageBean.setType(MsgType.TYPE_GROUP);
                            messageBean.setNewMsgnumber(applyList.size());
                            messageBean.setTitle("群消息助手");
                            messageBean.setIcon(groupMessage.getGroupicon());
                            messageBeanDao.addMessage(messageBean);
                        } else {
                            if ("applying".equals(groupMessage.getStatus())) {
                                messageBean.setAdditional(groupMessage.getUsername() + "申请加入" + groupMessage.getGroupname());
                            } else if ("exit".equals(groupMessage.getStatus())) {
                                messageBean.setAdditional(groupMessage.getUsername() + "已退出" + groupMessage.getGroupname());
                            }
                            messageBean.setType(MsgType.TYPE_GROUP);
                            messageBean.setNewMsgnumber(applyList.size());
                            messageBean.setTitle("群消息助手");
                            messageBean.setIcon(groupMessage.getGroupicon());
                            messageBeanDao.updateBean(messageBean);
                        }
                        setMessageDotVisibility(View.VISIBLE);
                    } else {
                        MessageBean messageBean = messageBeanDao.findFriend(MsgType.TYPE_GROUP);
                        if (messageBean != null) {
                            messageBean.setNewMsgnumber(0);
                            messageBean.setTitle("群消息助手");
                            messageBeanDao.updateBean(messageBean);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    /**
     * 小红点的展示与否
     *
     * @param visibility
     */
    private void setMessageDotVisibility(int visibility) {
        ivMessagedot.setVisibility(visibility);
    }
}
