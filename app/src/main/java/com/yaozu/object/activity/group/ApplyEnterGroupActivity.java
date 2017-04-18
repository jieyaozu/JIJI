package com.yaozu.object.activity.group;

import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.ParamList;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jxj42 on 2017/4/15.
 */

public class ApplyEnterGroupActivity extends BaseActivity implements View.OnClickListener {
    private EditText editText;
    private Button btConfirm;
    private String mGroupid;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_applyenter_group);
    }

    @Override
    protected void initView() {
        editText = (EditText) findViewById(R.id.apply_enter_message);
        btConfirm = (Button) findViewById(R.id.apply_enter_button);
    }

    @Override
    protected void initData() {
        mGroupid = getIntent().getStringExtra(IntentKey.INTENT_GROUP_ID);
    }

    @Override
    protected void setListener() {
        btConfirm.setOnClickListener(this);
    }

    private void requestApplyEnter(String groupid, String message) {
        showBaseProgressDialog("请稍候...");
        String url = DataInterface.APPLY_ENTER_GROUP;
        ParamList parameters = new ParamList();
        parameters.add(new ParamList.Parameter("userid", LoginInfo.getInstance(this).getUserAccount()));
        parameters.add(new ParamList.Parameter("groupid", groupid));
        parameters.add(new ParamList.Parameter("status", "applying"));
        try {
            parameters.add(new ParamList.Parameter("message", URLEncoder.encode(message, "utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestManager.getInstance().postRequest(this, url, parameters, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                closeBaseProgressDialog();
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    showToast(requestData.getBody().getMessage());
                    finish();
                }
            }

            @Override
            public void onFailure(int code, String message) {
                closeBaseProgressDialog();
            }
        });
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("加群");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply_enter_button:
                String message = editText.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    showToast("申请信息不能为空");
                    return;
                }
                requestApplyEnter(mGroupid, message);
                break;
        }
    }
}
