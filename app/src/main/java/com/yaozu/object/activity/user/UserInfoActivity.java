package com.yaozu.object.activity.user;

import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseNoTitleActivity;
import com.yaozu.object.bean.constant.GroupUserType;
import com.yaozu.object.db.dao.GroupDao;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.entity.UserInfoData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.GroupPermission;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.UserScrollRootView;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by jieyaozu on 2017/2/22.
 */

public class UserInfoActivity extends BaseNoTitleActivity implements View.OnClickListener {
    private String groupid, userid;
    private ImageView ivReturn, ivUserIcon;
    private TextView tvUserName;
    private UserInfoData userInfoData;
    private int accountType = -1;
    private ListView mListView;
    private MyListViewAdapter listViewAdapter;
    private View headerView;

    private RelativeLayout rlHeaderContainer;
    //邀请入群
    private LinearLayout layoutInvite;
    private RelativeLayout user_detail_set_admin_layout;
    private SwitchCompat scSetAdmin;

    private ImageView ivUserBackground;
    private UserScrollRootView scrollRootView;

    private GroupDao groupDao;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_userinfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.userinfo_actions, menu);
        if (!userid.equals(LoginInfo.getInstance(this).getUserAccount()) && "2".equals(LoginInfo.getInstance(this).getAccountType())) {
            menu.findItem(R.id.action_change_accounttype).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_accounttype:
                showChangeTypeDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initView() {
        groupDao = new GroupDao(this);
        scrollRootView = (UserScrollRootView) findViewById(R.id.userdetail_scrollview);
        groupid = getIntent().getStringExtra(IntentKey.INTENT_GROUP_ID);
        userid = getIntent().getStringExtra(IntentKey.INTENT_USERID);
        ivReturn = (ImageView) findViewById(R.id.userinfo_return);
        mListView = (ListView) findViewById(R.id.user_detail_listview);
        ivUserBackground = (ImageView) findViewById(R.id.userdetail_image_background);
        headerView = View.inflate(this, R.layout.header_userdetail_listview, null);
        rlHeaderContainer = (RelativeLayout) findViewById(R.id.surface_container);
        layoutInvite = (LinearLayout) findViewById(R.id.userinfo_bottom_layout);
        user_detail_set_admin_layout = (RelativeLayout) headerView.findViewById(R.id.user_detail_set_admin_layout);
        scSetAdmin = (SwitchCompat) headerView.findViewById(R.id.user_detail_set_admin);

        ivUserIcon = (ImageView) findViewById(R.id.userdetail_usericon);
        tvUserName = (TextView) findViewById(R.id.userdetail_username);

        mListView.addHeaderView(headerView);
        scrollRootView.setListView(mListView);
        requestFindUserinfo(userid);
    }

    @Override
    protected void initData() {
        listViewAdapter = new MyListViewAdapter();
        mListView.setAdapter(listViewAdapter);

        if (TextUtils.isEmpty(groupid) || LoginInfo.getInstance(this).getUserAccount().equals(userid)) {
            user_detail_set_admin_layout.setVisibility(View.GONE);
        } else if (GroupPermission.isMyCreatGroupid(groupDao, groupid)) {
            user_detail_set_admin_layout.setVisibility(View.VISIBLE);
            selectUserType(groupid, userid);
        }
    }

    @Override
    protected void setListener() {
        ivReturn.setOnClickListener(this);
        scSetAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isSelect) {
                    isSelect = false;
                    return;
                }
                if (isChecked) {
                    setUserType(groupid, userid, GroupUserType.ADMIN);
                } else {
                    setUserType(groupid, userid, GroupUserType.NORMAL);
                }
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    View firstView = mListView.getChildAt(0);
                    if (null != firstView) {
                        int firstTop = firstView.getTop();
                        rlHeaderContainer.setTranslationY(firstTop);
                    }
                } else if (firstVisibleItem < 6) {
                    rlHeaderContainer.setTranslationY(-getResources().getDimensionPixelSize(R.dimen.dimen_40) * 7);
                }
            }
        });
    }

    private boolean isSelect = false;

    private void selectUserType(String groupid, String userid) {
        String url = DataInterface.SELECT_USERTYPE + "groupid=" + groupid + "&userid=" + userid;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if (GroupUserType.ADMIN.equals(requestData.getBody().getStatus())) {
                        isSelect = true;
                        scSetAdmin.setChecked(true);
                    } else {
                        scSetAdmin.setChecked(false);
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    private void setUserType(String groupid, String userid, final String usertype) {
        String url = DataInterface.SET_USERTYPE + "groupid=" + groupid + "&userid=" + userid + "&usertype=" + usertype;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        if (usertype.equals(GroupUserType.ADMIN)) {
                            showToast("成功设置为管理员");
                        } else {
                            showToast("已取消管理员资格");
                        }
                        Constant.IS_SET_USERTYPE_SUCCESS = true;
                    } else {
                        showToast("设置失败");
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    private void requestFindUserinfo(String userid) {
        String url = DataInterface.FIND_USERINFO + "userid=" + userid;
        RequestManager.getInstance().getRequest(this, url, UserInfoData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    userInfoData = (UserInfoData) object;
                    accountType = userInfoData.getBody().getUserinfo().getType();
                    tvUserName.setText(userInfoData.getBody().getUserinfo().getUsername());
                    Utils.setUserImg(userInfoData.getBody().getUserinfo().getIconpath(), ivUserIcon);
                    ImageLoader.getInstance().displayImage(userInfoData.getBody().getUserinfo().getIconpath(), ivUserBackground);
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    MaterialDialog materialDialog;
    String userType = "";

    private void showChangeTypeDialog() {
        View contentView = View.inflate(this, R.layout.dialog_change_accouttype_contentview, null);
        materialDialog = new MaterialDialog(this)
                .setTitle("更改账户类型")
                .setContentView(contentView)
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        requestChangeAccountType(userid, userType);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
        RadioGroup radioGroup = (RadioGroup) contentView.findViewById(R.id.accounttype_rg);
        RadioButton normal = (RadioButton) contentView.findViewById(R.id.accounttype_normal);
        RadioButton postmain = (RadioButton) contentView.findViewById(R.id.accounttype_postmain);
        if (0 == accountType) {
            userType = "0";
            normal.setChecked(true);
        } else if (1 == accountType) {
            userType = "1";
            postmain.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.accounttype_normal:
                        userType = "0";
                        break;
                    case R.id.accounttype_postmain:
                        userType = "1";
                        break;
                }
            }
        });
        materialDialog.show();
    }

    /**
     * 更改账户类型
     *
     * @param userid
     * @param type
     */
    private void requestChangeAccountType(String userid, final String type) {
        String url = DataInterface.CHANGE_ACCOUNT_TYPE + "userid=" + userid + "&type=" + type;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if ("1".equals(requestData.getBody().getCode())) {
                        accountType = Integer.parseInt(type);
                    }
                    showToast(requestData.getBody().getMessage());
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
            case R.id.userinfo_return:
                finish();
                break;
        }
    }

    class MyListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 0;
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
            TextView textView = new TextView(UserInfoActivity.this);
            textView.setText("===============>" + position);
            return textView;
        }
    }
}
