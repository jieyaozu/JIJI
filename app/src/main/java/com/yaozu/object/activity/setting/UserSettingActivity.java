package com.yaozu.object.activity.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.yaozu.object.MainActivity;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.activity.CropImageActivity;
import com.yaozu.object.activity.MyAlbumActivity;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.listener.UploadListener;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.FileUtil;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.NetUtil;
import com.yaozu.object.utils.Utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by jieyaozu on 2017/4/28.
 */

public class UserSettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvUserName, tvUserNameOld, tvUserIcon;
    private ImageView ivUserIconOld;
    //修改密码
    private TextView tvPwd;
    //登出
    private Button btLoginOut;

    private static final int ACTIVITY_RESULT_GALRY = 0;
    private static final int ACTIVITY_RESULT_CROPIMAGE = 1;

    private String mSavePath;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_user_setting);
    }

    @Override
    protected void initView() {
        tvUserName = (TextView) findViewById(R.id.user_setting_username);
        tvUserIcon = (TextView) findViewById(R.id.user_setting_usericon);
        tvPwd = (TextView) findViewById(R.id.user_setting_updatepwd);
        tvUserNameOld = (TextView) findViewById(R.id.user_setting_username_old);

        ivUserIconOld = (ImageView) findViewById(R.id.user_setting_usericon_old);
        btLoginOut = (Button) findViewById(R.id.user_setting_loginout);
    }

    @Override
    protected void initData() {
        Utils.setUserImg(LoginInfo.getInstance(this).getSmallIconPath(), ivUserIconOld);
        tvUserNameOld.setText(LoginInfo.getInstance(this).getUserName());
    }

    @Override
    protected void setListener() {
        tvUserName.setOnClickListener(this);
        tvUserIcon.setOnClickListener(this);
        tvPwd.setOnClickListener(this);
        btLoginOut.setOnClickListener(this);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("个人设置");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_setting_username:
                showRenameDialog();
                break;
            case R.id.user_setting_usericon:
                getimgefromegalry();
                break;
            case R.id.user_setting_updatepwd:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.user_setting_loginout:
                showLoginOutDialog();
                break;
        }
    }

    private void showLoginOutDialog() {
        new MaterialDialog.Builder(this)
                .backgroundColorRes(R.color.colorWhite)
                .contentColorRes(R.color.gray)
                .content("确定登出此账户吗？")
                .positiveText("确定")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        LoginInfo.getInstance(UserSettingActivity.this).quitLogin();
                        Intent main = new Intent(UserSettingActivity.this, MainActivity.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(main);
                    }
                })
                .negativeText("取消")
                .show();
    }

    private void showRenameDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .backgroundColorRes(R.color.colorWhite)
                .customView(R.layout.dialog_editname, false)
                .positiveText("确定")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.dialog_edit_name);
                        String username = editText.getText().toString();
                        if (TextUtils.isEmpty(username)) {
                            showToast("用户名不能为空");
                            return;
                        }
                        if (username.length() > 12) {
                            showToast("用户名太长了");
                            return;
                        }
                        requestUpdateUserInfo(LoginInfo.getInstance(UserSettingActivity.this).getUserAccount(), username);
                    }
                }).build();
        View view = dialog.getCustomView();
        EditText editText = (EditText) view.findViewById(R.id.dialog_edit_name);
        editText.setText(LoginInfo.getInstance(this).getUserName());
        editText.setSelection(LoginInfo.getInstance(this).getUserName().length());
        dialog.show();
    }

    /**
     * 打开相册选取图片
     */
    public void getimgefromegalry() {
        Intent intent = new Intent(this, MyAlbumActivity.class);
        intent.putExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, true);
        this.startActivityForResult(intent, ACTIVITY_RESULT_GALRY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_RESULT_GALRY:
                if (data == null) {
                    return;
                }
                List<MyImage> listData = data.getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
                if (listData == null || listData.size() <= 0) {
                    return;
                }
                String path = listData.get(0).getPath();
                final Bitmap bm = FileUtil.compressUserIcon(1200, path);
                Intent cropimage = new Intent(this, CropImageActivity.class);
                IntentKey.cropBitmap = bm;
                startActivityForResult(cropimage, ACTIVITY_RESULT_CROPIMAGE);
                break;
            case ACTIVITY_RESULT_CROPIMAGE:
                if (IntentKey.cropBitmap == null) {
                    return;
                }
                //保存到本地
                FileUtil fileUtil = new FileUtil();
                mSavePath = fileUtil.getFileSDPATH();
                mSavePath = mSavePath + FileUtil.generateDisplayName("usericon");
                FileUtil.saveOutput(IntentKey.cropBitmap, mSavePath);
                //上传头像到服务器上
                showBaseProgressDialog("正在上传中...");
                NetUtil.uploadUserIconFile(this, new File(mSavePath), new MyUploadListener());
                break;
        }
    }

    /**
     * 文件上传监听
     */
    public class MyUploadListener implements UploadListener {

        @Override
        public void uploadSuccess(String jsonstring) {
            closeBaseProgressDialog();
            showToast("上传头像成功!");
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(jsonstring);
            String iconpath = jsonObject.getString("iconpath");
            String siconpath = jsonObject.getString("siconpath");
            LoginInfo.getInstance(UserSettingActivity.this).updateUserIcon(Constant.APP_IMAGE_HOST + iconpath, Constant.APP_IMAGE_HOST + siconpath);
            Utils.setUserImg(Constant.APP_IMAGE_HOST + siconpath, ivUserIconOld);
            Constant.IS_EDIT_USERICON = true;
        }

        @Override
        public void uploadFailed() {
            showToast("上传头像失败，请重试！");
            closeBaseProgressDialog();
            FileUtil.deleteFile(mSavePath);
        }
    }

    /**
     * 更新用户信息
     *
     * @param userid
     */
    private void requestUpdateUserInfo(String userid, final String username) {
        String url = null;
        try {
            url = DataInterface.RENAME_USERNAME + "?userid=" + userid + "&username=" + URLEncoder.encode(username, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    showToast(requestData.getBody().getMessage());
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        //更新本地存储
                        LoginInfo.getInstance(UserSettingActivity.this).setUserName(username);
                        tvUserNameOld.setText(username);
                        Constant.IS_EDIT_USERNAME = true;
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }
}
