package com.yaozu.object.activity.setting;

import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jieyaozu on 2016/8/24.
 */
public class ChangePasswordActivity extends BaseActivity {
    private EditText oldPwd, newPwd, comfirmNewPwd;
    private Button changeButton;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_change_password);
    }

    @Override
    protected void initView() {
        oldPwd = (EditText) findViewById(R.id.change_old_pwd);
        newPwd = (EditText) findViewById(R.id.change_new_pwd);
        comfirmNewPwd = (EditText) findViewById(R.id.change_new_cmf_pwd);
        changeButton = (Button) findViewById(R.id.change_button);
    }

    @Override
    protected void initData() {
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpwd = oldPwd.getText().toString();
                String newpwd = newPwd.getText().toString();
                String cmfnewpwd = comfirmNewPwd.getText().toString();
                if (TextUtils.isEmpty(oldpwd)) {
                    Toast.makeText(ChangePasswordActivity.this, "请输入原始密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(newpwd) || TextUtils.isEmpty(cmfnewpwd)) {
                    Toast.makeText(ChangePasswordActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newpwd.equals(cmfnewpwd)) {
                    Toast.makeText(ChangePasswordActivity.this, "两次新输入的密码不相同", Toast.LENGTH_SHORT).show();
                    return;
                }
                showBaseProgressDialog("请稍候...");
                changePwd(oldpwd, newpwd);
            }
        });
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("更改密码");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 修改密码
     *
     * @param oldpwd
     * @param newpwd
     */
    private void changePwd(String oldpwd, String newpwd) {
        String url = DataInterface.CHANGE_PASSWORD + "userid=" + LoginInfo.getInstance(this).getUserAccount() + "&oldpwd=" + oldpwd + "&newpwd=" + newpwd;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                closeBaseProgressDialog();
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    showToast(requestData.getBody().getMessage());
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {
                closeBaseProgressDialog();
            }
        });
    }
}
