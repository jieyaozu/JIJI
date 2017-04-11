package com.yaozu.object.activity.group;

import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.ParamList;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jieyaozu on 2017/4/11.
 * 编辑名称和介绍
 */

public class EditGroupActivity extends BaseActivity {
    private EditText etGroupName;
    private EditText etGroupIntroduce;
    private Button btConfirm;
    private String groupname;
    private String mIntroduce;
    private String groupid;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_edit_group);
    }

    @Override
    protected void initView() {
        etGroupName = (EditText) findViewById(R.id.edit_group_name);
        etGroupIntroduce = (EditText) findViewById(R.id.edit_group_introduction);
        btConfirm = (Button) findViewById(R.id.editgroup_confirm_bt);
    }

    @Override
    protected void initData() {
        GroupBean groupBean = (GroupBean) getIntent().getSerializableExtra(IntentKey.INTENT_GROUP);
        groupname = groupBean.getGroupname();
        groupid = groupBean.getGroupid();
        mIntroduce = groupBean.getIntroduce();

        etGroupName.setText(groupname);
        etGroupName.setSelection(groupname.length());
        etGroupIntroduce.setText(mIntroduce);
    }

    @Override
    protected void setListener() {
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etGroupName.getText().toString().trim();
                String introduce = etGroupIntroduce.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    showToast("群名称不能为空");
                    return;
                }
                if (TextUtils.isEmpty(introduce)) {
                    showToast("群介绍不能为空");
                    return;
                }
                if (name.equals(groupname) && introduce.equals(mIntroduce)) {
                    finish();
                    return;
                }
                requestEdit(groupid, name, introduce);
            }
        });
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("编辑群名称和群介绍");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void requestEdit(String groupid, String groupname, String introduce) {
        showBaseProgressDialog("请稍候...");
        String url = DataInterface.EDIT_GROUP;
        ParamList parameters = new ParamList();
        try {
            parameters.add(new ParamList.Parameter("groupid", groupid));
            parameters.add(new ParamList.Parameter("groupname", URLEncoder.encode(groupname, "utf-8")));
            parameters.add(new ParamList.Parameter("introduce", URLEncoder.encode(introduce, "utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestManager.getInstance().postRequest(this, url, parameters, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                closeBaseProgressDialog();
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        showToast(requestData.getBody().getMessage());
                        Constant.IS_EDITGROUP_SUCCESS = true;
                        finish();
                    } else {
                        showToast(requestData.getBody().getMessage());
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
