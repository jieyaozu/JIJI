package com.yaozu.object.activity.user;

import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.entity.UserInfoData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.Utils;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by jieyaozu on 2017/2/22.
 */

public class UserInfoActivity extends BaseActivity {
    private String userid;
    private ImageView ivUserIcon;
    private TextView tvUserName;
    private UserInfoData userInfoData;
    private int accountType = -1;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_userinfo);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("返回");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userinfo_actions, menu);
        if (!userid.equals(LoginInfo.getInstance(this).getUserAccount()) && "2".equals(LoginInfo.getInstance(this).getAccountType())) {
            menu.findItem(R.id.action_change_accounttype).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_accounttype:
                showChangeTypeDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initView() {
        userid = getIntent().getStringExtra(IntentKey.INTENT_USERID);

        requestFindUserinfo(userid);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    private void requestFindUserinfo(String userid) {
        String url = DataInterface.FIND_USERINFO + "userid=" + userid;
        RequestManager.getInstance().getRequest(this, url, UserInfoData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    userInfoData = (UserInfoData) object;
                    accountType = userInfoData.getBody().getUserinfo().getType();
                    tvUserName.setText(userInfoData.getBody().getUserinfo().getUsername());
                    Utils.setUserImg(userInfoData.getBody().getUserinfo().getIconpath(), ivUserIcon);
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    MaterialDialog materialDialog;
    String userType = "";

    private void showChangeTypeDialog() {
        View contentView = View.inflate(this, R.layout.dialog_change_accouttype_contentview, null);
        materialDialog = new MaterialDialog(this)
                .setTitle("更改账户类型")
                .setContentView(contentView)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        requestChangeAccountType(userid, userType);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
        RadioGroup radioGroup = (RadioGroup) contentView.findViewById(R.id.accounttype_rg);
        RadioButton normal = (RadioButton) contentView.findViewById(R.id.accounttype_normal);
        RadioButton postmain = (RadioButton) contentView.findViewById(R.id.accounttype_postmain);
        if (0 == accountType) {
            userType = "0";
            normal.setChecked(true);
        } else if (1 == accountType) {
            userType = "1";
            postmain.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.accounttype_normal:
                        userType = "0";
                        break;
                    case R.id.accounttype_postmain:
                        userType = "1";
                        break;
                }
            }
        });
        materialDialog.show();
    }

    /**
     * 更改账户类型
     *
     * @param userid
     * @param type
     */
    private void requestChangeAccountType(String userid, final String type) {
        String url = DataInterface.CHANGE_ACCOUNT_TYPE + "userid=" + userid + "&type=" + type;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if ("1".equals(requestData.getBody().getCode())) {
                        accountType = Integer.parseInt(type);
                    }
                    showToast(requestData.getBody().getMessage());
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }
}
