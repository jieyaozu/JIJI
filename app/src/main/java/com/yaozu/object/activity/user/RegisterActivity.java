package com.yaozu.object.activity.user;

import android.app.Dialog;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.utils.IntentUtil;


/**
 * Created by jieyz on 2016/1/26.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = this.getClass().getSimpleName();
    private EditText nickName, phoneNumber, password;
    private Button register;
    private Dialog dialog;
    private TextView toLogin;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("填写手机号");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initView() {
        nickName = (EditText) findViewById(R.id.register_nickname);
        phoneNumber = (EditText) findViewById(R.id.register_phonenumber);
        password = (EditText) findViewById(R.id.register_password);
        register = (Button) findViewById(R.id.register_register);
        toLogin = (TextView) findViewById(R.id.register_to_login);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        register.setOnClickListener(this);
        toLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_register:

                break;
            case R.id.register_to_login:
                IntentUtil.toLoginActivity(this);
                finish();
                break;
        }
    }

    private boolean verifyPhoneNumber(String number) {
        if (number.length() != 11 || !number.matches("[0-9]+") || number.charAt(0) != 49) {
            Toast.makeText(this, "电话号码的格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkPhoneNumberExist(final String phoneNumber, final String nickname, final String password) {

    }

    /**
     * 发送短信验证码
     */
    private void sendVerifyCode(final String phoneNumber, final String nickname, final String password) {

    }
}
