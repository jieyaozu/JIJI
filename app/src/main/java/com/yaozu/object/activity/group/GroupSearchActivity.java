package com.yaozu.object.activity.group;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.adapter.SearchGroupAdapter;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.entity.GroupReqListData;
import com.yaozu.object.httpmanager.ParamList;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by jxj42 on 2017/4/16.
 */

public class GroupSearchActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivReturn;
    private EditText etInput;
    private ImageView ivClearText;
    private TextView tvResultShow;

    private HeaderViewRecyclerAdapter stringAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SearchGroupAdapter groupAdapter;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_search_group);
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.common_refresh_recyclerview);
        ivReturn = (ImageView) findViewById(R.id.search_return);
        etInput = (EditText) findViewById(R.id.search_input_edit);
        ivClearText = (ImageView) findViewById(R.id.clear_input_text);
        tvResultShow = (TextView) findViewById(R.id.search_result_show);
    }

    @Override
    protected void initData() {
        groupAdapter = new SearchGroupAdapter(this);
        stringAdapter = new HeaderViewRecyclerAdapter(groupAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter(linearLayoutManager, stringAdapter);
    }

    @Override
    protected void setListener() {
        ivReturn.setOnClickListener(this);
        ivClearText.setOnClickListener(this);
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String groupName = etInput.getText().toString().trim();
                    if (TextUtils.isEmpty(groupName)) {
                        return true;
                    }
                    requestSearchGroup(groupName);
                    return true;
                }
                return false;
            }
        });
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    ivClearText.setVisibility(View.INVISIBLE);
                } else {
                    ivClearText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {

    }

    /**
     * 向服务器请求数据
     *
     * @param groupname
     */
    private void requestSearchGroup(final String groupname) {
        String url = DataInterface.SEARCH_GROUP;
        ParamList parameters = new ParamList();
        try {
            parameters.add(new ParamList.Parameter("groupname", URLEncoder.encode(groupname, "utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestManager.getInstance().postRequest(this, url, parameters, GroupReqListData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    groupAdapter.clearData();
                    GroupReqListData groupReqListData = (GroupReqListData) object;
                    List<GroupBean> groupBeanList = groupReqListData.getBody().getGrList();
                    groupAdapter.setDataList(groupBeanList, groupname);

                    if (groupBeanList == null || groupBeanList.size() <= 0) {
                        tvResultShow.setVisibility(View.VISIBLE);
                    } else {
                        tvResultShow.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_return:
                finish();
                break;
            case R.id.clear_input_text:
                etInput.setText("");
                etInput.requestFocus();
                groupAdapter.clearData();
                break;
        }
    }
}
