package com.yaozu.object.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.yaozu.object.MainActivity;
import com.yaozu.object.R;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.service.GetuiPushService;
import com.yaozu.object.service.MyIntentService;

/**
 * Created by jxj42 on 2017/1/16.
 */

public class SplashActivity extends FragmentActivity {
    private TextView tvSplash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        PushManager.getInstance().initialize(this.getApplicationContext(), GetuiPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), MyIntentService.class);
/*        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag); //隐藏状态栏*/
        tvSplash = (TextView) findViewById(R.id.splash_tv);
        final boolean islogin = LoginInfo.getInstance(this).isLogining();
        tvSplash.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (islogin) {
                    LoginInfo.readUserInfoToMemory();
                }
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 500);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
