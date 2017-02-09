package com.yaozu.object.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yaozu.object.utils.Constant;

/**
 * Created by jieyz on 2016/1/26.
 */
public class LoginInfo {
    private static String mAccount, mUserName, mToken, mIcon, mSmallIcon, mFromDevice;
    public static String userMaixm = null;
    private static int level;
    private static int VIP = 0;//0是普通用户，1是VIP用户
    private Context mContext;
    private static SharedPreferences preferences;

    public LoginInfo(Context context) {
        this.mContext = context;
        preferences = mContext.getSharedPreferences(Constant.LOGIN_MSG, Context.MODE_PRIVATE);
    }

    public void storeLoginUserInfo(boolean isLogin, String account, String userName, String icon, String smallicon, int level, String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();
        editor.putBoolean(Constant.IS_LOGINING, isLogin);
        editor.putString(Constant.USER_TOKEN, token);
        editor.putString(Constant.USER_ACCOUNT, account);
        editor.putString(Constant.USER_NAME, userName);
        editor.putString(Constant.USER_ICON, icon);
        editor.putString(Constant.USER_SMALL_ICON, smallicon);
        editor.putInt(Constant.USER_LEVEL, level);
        editor.commit();
        readUserInfoToMemory();
    }

    /**
     * 用户信息读到内存中
     */
    public static void readUserInfoToMemory() {
        mAccount = preferences.getString(Constant.USER_ACCOUNT, "");
        mUserName = preferences.getString(Constant.USER_NAME, "");
        mToken = preferences.getString(Constant.USER_TOKEN, "");
        mIcon = preferences.getString(Constant.USER_ICON, "");
        mSmallIcon = preferences.getString(Constant.USER_SMALL_ICON, "");
        level = preferences.getInt(Constant.USER_LEVEL, 1);
    }

    public void updateUserLevel(int level) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constant.USER_LEVEL, level);
        editor.commit();
        this.level = level;
    }

    public static int getUserLevel() {
        return preferences.getInt(Constant.USER_LEVEL, 1);
    }

    public static int getUserVip() {
        return preferences.getInt(Constant.USER_VIP, 0);
    }

    public static String getUserAccount() {
        if (TextUtils.isEmpty(mAccount)) {
            mAccount = preferences.getString(Constant.USER_ACCOUNT, "");
        }
        return mAccount;
    }

    public static String getUserName() {
        if (TextUtils.isEmpty(mUserName)) {
            mUserName = preferences.getString(Constant.USER_NAME, "");
        }
        return mUserName;
    }

    public static String getFromDevice() {
        if (TextUtils.isEmpty(mFromDevice)) {
            mFromDevice = preferences.getString(Constant.USER_FROM_DEVICE, "");
        }
        return mFromDevice;
    }

    public static String getIconPath() {
        if (TextUtils.isEmpty(mIcon)) {
            mIcon = preferences.getString(Constant.USER_ICON, "");
        }
        return mIcon;
    }

    public static String getSmallIconPath() {
        if (TextUtils.isEmpty(mSmallIcon)) {
            mSmallIcon = preferences.getString(Constant.USER_SMALL_ICON, "");
        }
        return mSmallIcon;
    }

    public String getUserToken() {
        return mToken;//preferences.getString(Constant.USER_TOKEN, "");
    }

    public void setUserToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.USER_TOKEN, token);
        editor.commit();
        mToken = token;
    }

    public String getUserAccoutFromLocal() {
        return preferences.getString(Constant.USER_ACCOUNT, "");
    }

    public void quitLogin() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constant.IS_LOGINING, false);
        editor.putString(Constant.USER_TOKEN, "");
        editor.commit();
    }

    public boolean isLogining() {
        boolean islogin = preferences.getBoolean(Constant.IS_LOGINING, false);
        return islogin;
    }
}
