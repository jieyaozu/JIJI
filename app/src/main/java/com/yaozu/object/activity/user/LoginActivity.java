package com.yaozu.object.activity.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.MainActivity;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.widget.RoundCornerImageView;

/**
 * Created by 耀祖 on 2016/1/25.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText mAccout, mPassword;
    private RelativeLayout accoutLayout;
    private LinearLayout userIconLL;
    //头像
    private RoundCornerImageView userIcon;
    //用户ID
    private TextView userIdView;
    //切换用户账号登录
    private TextView switchAccout;
    private Button mLogin;

    private SharedPreferences sp;
    private LoginInfo mLoginInfo;
    private TextView registerTextView;
    private String TAG = this.getClass().getSimpleName();
    private ProgressDialog progressDialog;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("登录");
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected void initView() {
        mAccout = (EditText) findViewById(R.id.login_account);
        mPassword = (EditText) findViewById(R.id.login_password);
        mLogin = (Button) findViewById(R.id.login_login);
        registerTextView = (TextView) findViewById(R.id.activity_login_register);
        accoutLayout = (RelativeLayout) findViewById(R.id.login_account_rl);
        userIconLL = (LinearLayout) findViewById(R.id.login_layout_imageview_ll);
        userIcon = (RoundCornerImageView) findViewById(R.id.login_layout_imageview);
        userIdView = (TextView) findViewById(R.id.login_userid);
        switchAccout = (TextView) findViewById(R.id.activity_login_switch_account);
    }

    @Override
    protected void initData() {
        sp = this.getSharedPreferences(Constant.LOGIN_MSG, Context.MODE_PRIVATE);
        mLogin.setOnClickListener(this);
        registerTextView.setOnClickListener(this);
        switchAccout.setOnClickListener(this);
        mLoginInfo = new LoginInfo(this);

        boolean islogin = mLoginInfo.isLogining();
        if (islogin) {
            LoginInfo.readUserInfoToMemory();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("token", mLoginInfo.getUserToken());
            startActivity(intent);
            finish();
        } else {
            String userId = mLoginInfo.getUserAccoutFromLocal();
            if (!TextUtils.isEmpty(userId)) {
                accoutLayout.setVisibility(View.GONE);
                userIconLL.setVisibility(View.VISIBLE);
                switchAccout.setVisibility(View.VISIBLE);
                userIdView.setText(userId);
                ImageLoader.getInstance().displayImage("file://" + LoginInfo.getSmallIconPath(), userIcon);
            }
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_login:

                break;
            case R.id.activity_login_register:
                IntentUtil.toRegisterActivity(this);
                break;
            case R.id.activity_login_switch_account:
                switchAccout.setVisibility(View.GONE);
                accoutLayout.setVisibility(View.VISIBLE);
                userIconLL.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 向服务器发送登录请求
     *
     * @param userid
     * @param password
     * @param deviceid
     */
    private void loginRequest(final String userid, String password, String deviceid) {

    }

    private void showProgressDialog() {
        if ((!isFinishing()) && (this.progressDialog == null)) {
            this.progressDialog = new ProgressDialog(this);
        }
        //this.progressDialog.setTitle(getString(R.string.loadTitle));
        this.progressDialog.setMessage("正在登录");
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
    }

    private void closeProgressDialog() {
        if (this.progressDialog != null)
            this.progressDialog.dismiss();
    }
}
