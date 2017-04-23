package com.yaozu.object.activity.group;

import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.ParamList;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jxj42 on 2017/4/22.
 */

public class EditNicknameActivity extends BaseActivity {
    private EditText etNickname;
    private String mNickname;
    private String mGroupid;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_edit_nickname);
    }

    @Override
    protected void initView() {
        etNickname = (EditText) findViewById(R.id.edit_nickname_name);
    }

    @Override
    protected void initData() {
        mNickname = getIntent().getStringExtra(IntentKey.INTENT_NICKNAME);
        mGroupid = getIntent().getStringExtra(IntentKey.INTENT_GROUP_ID);
        etNickname.setText(mNickname);
        if (!TextUtils.isEmpty(mNickname)) {
            etNickname.setSelection(mNickname.length());
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("编辑群名片");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_nickname_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editnickname_action_commit:
                String nickname = etNickname.getText().toString().trim();
                if (TextUtils.isEmpty(nickname)) {
                    showToast("名称不能为空");
                    return true;
                }
                if (nickname.length() > 14) {
                    showToast("名称不能超过14个字符");
                    return true;
                }
                if (nickname.equals(mNickname)) {
                    finish();
                    return true;
                }
                requestEditNickname(mGroupid, nickname);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 编辑群内名片
     */
    private void requestEditNickname(String groupid, String nickname) {
        showBaseProgressDialog("请稍候...");
        String url = DataInterface.EDIT_NICKNAME;
        ParamList paramList = new ParamList();
        paramList.add(new ParamList.Parameter("userid", LoginInfo.getInstance(this).getUserAccount()));
        paramList.add(new ParamList.Parameter("groupid", groupid));
        try {
            paramList.add(new ParamList.Parameter("nickname", URLEncoder.encode(nickname, "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestManager.getInstance().postRequest(this, url, paramList, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                closeBaseProgressDialog();
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        Constant.IS_EDITGROUP_SUCCESS = true;
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
