package com.yaozu.object.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.activity.group.UploadGroupIconActivity;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.entity.SectionReqData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/4/4.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class CreateGroupActivity extends BaseActivity implements View.OnClickListener {
    private Spinner sectionSpinner;
    private Button btNext;
    private EditText etGroupName;
    private EditText etIntroduce;
    private SpinnerAdapter arr_adapter;
    private List<SectionReqData.Section> data_list = new ArrayList<>();

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_creategroup);
    }

    @Override
    protected void initView() {
        sectionSpinner = (Spinner) findViewById(R.id.creategroup_spinner);
        etGroupName = (EditText) findViewById(R.id.group_name_edittext);
        etIntroduce = (EditText) findViewById(R.id.group_introduction_edittext);
        btNext = (Button) findViewById(R.id.creategroup_next_bt);
    }

    @Override
    protected void initData() {
        initSpinnerData();
    }

    @Override
    protected void setListener() {
        btNext.setOnClickListener(this);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("创建群");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.IS_CREATEGROUP_SUCCESS) {
            finish();
        }
    }

    private void initSpinnerData() {
        arr_adapter = new SpinnerAdapter();
        sectionSpinner.setAdapter(arr_adapter);
        sectionSpinner.setDropDownVerticalOffset(getResources().getDimensionPixelSize(R.dimen.dimen_40));
        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getScetionListData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.creategroup_next_bt:
                GroupBean groupBean = new GroupBean();
                String groupName = etGroupName.getText().toString().trim();
                String introduce = etIntroduce.getText().toString().trim();
                String sectionid = data_list.get(sectionSpinner.getSelectedItemPosition()).getSection_id();
                if (TextUtils.isEmpty(groupName)) {
                    showToast("群名称不能为空");
                    return;
                }
                if (TextUtils.isEmpty(sectionid)) {
                    showToast("请选择一个版块");
                    return;
                }
                if (TextUtils.isEmpty(introduce)) {
                    showToast("群简介不能为空");
                    return;
                }
                groupBean.setGroupname(groupName);
                groupBean.setIntroduce(introduce);
                groupBean.setSectionid(sectionid);
                Intent intent = new Intent(this, UploadGroupIconActivity.class);
                intent.putExtra(IntentKey.INTENT_GROUP, groupBean);
                startActivity(intent);
                break;
        }
    }

    class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return data_list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(CreateGroupActivity.this);
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = inflater.inflate(R.layout.text_line, null);
            }
            SectionReqData.Section section = data_list.get(position);
            TextView textView = (TextView) view.findViewById(R.id.text_line_text);
            textView.setText(section.getSection_name());
            return view;
        }
    }

    private void getScetionListData() {
        String url = DataInterface.FIND_ALL_SECTION;
        RequestManager.getInstance().getRequest(this, url, SectionReqData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    SectionReqData sectionReqData = (SectionReqData) object;
                    List<SectionReqData.Section> sectionList = sectionReqData.getBody().getSections();
                    data_list.clear();
                    data_list.addAll(sectionList);
                    arr_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }
}
