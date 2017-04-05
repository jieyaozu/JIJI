package com.yaozu.object.activity;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.entity.SectionReqData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.DataInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/4/4.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class CreateGroupActivity extends BaseActivity {
    private Spinner spinner;
    private SpinnerAdapter arr_adapter;
    private List<SectionReqData.Section> data_list = new ArrayList<>();

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_creategroup);
    }

    @Override
    protected void initView() {
        spinner = (Spinner) findViewById(R.id.creategroup_spinner);
    }

    @Override
    protected void initData() {
        initSpinnerData();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("创建群");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initSpinnerData() {
        arr_adapter = new SpinnerAdapter();
        spinner.setAdapter(arr_adapter);
        spinner.setDropDownVerticalOffset(getResources().getDimensionPixelSize(R.dimen.dimen_40));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getScetionListData();
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
                    if (data_list.size() > 0) {
                        data_list.remove(0);
                    }
                    arr_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }
}
