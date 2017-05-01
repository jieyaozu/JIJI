package com.yaozu.object.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.activity.group.GroupOfPostActivity;
import com.yaozu.object.adapter.PostDetailAdapter;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.bean.Post;
import com.yaozu.object.db.dao.GroupDao;
import com.yaozu.object.entity.DetailReplyPostListInfo;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.ParamList;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.listener.UploadListener;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.EditContentImageUtil;
import com.yaozu.object.utils.EncodingConvert;
import com.yaozu.object.utils.FileUtil;
import com.yaozu.object.utils.GroupPermission;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.NetUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.HorizontalListView;
import com.yaozu.object.widget.swiperefreshendless.HeaderViewRecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/8.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class PostDetailActivity extends BaseActivity implements View.OnClickListener {
    private EditText etEditContent;
    private ActionBar mActionBar;
    private ImageView ivMore;
    private LinearLayout llMoreLayout;
    private HorizontalListView mHorizontalListView;
    private TextView tvIndicate;
    private TextView tvSelectCount;

    private final int REQUEST_RESULT_SELECT_ALBUM = 0;

    private boolean isCollection = false;
    private Button btSend;

    private RecyclerView mRecyclerView;
    private View headerView;
    private PostDetailAdapter postDetailAdapter;
    private ImageView ivSupport;

    private Post mPost;
    private String replypostid;
    //主题贴id
    private String postid;
    private int currentIndex = 1;

    private int imageWidth = 0;
    private LinearLayoutManager linearLayoutManager;
    private HeaderViewRecyclerAdapter stringAdapter;
    private View nodataLayout;
    private TextView tvNoData;
    private GroupDao groupDao;

    private Menu mMenu;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_postdetail);
        registerUpdateReceiver();
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("详情");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setShowHideAnimationEnabled(true);
        mActionBar = actionBar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.postdetail_activity_actions, menu);
        if (!"0".equals(LoginInfo.getInstance(this).getAccountType())) {
            menu.findItem(R.id.action_post_delete).setVisible(true);
            menu.findItem(R.id.action_post_xiachen).setVisible(true);
        }
        if ((mPost != null && mPost.getUserid().equals(LoginInfo.getInstance(this).getUserAccount())) || (mPost != null && GroupPermission.isMyAdminGroupid(groupDao, mPost.getGroupid()))) {
            menu.findItem(R.id.action_post_delete).setVisible(true);
        }
        if (mPost != null && isMyAdminGroupid(mPost.getGroupid())) {
            menu.findItem(R.id.action_set_top).setVisible(true);
            setTopMenuText(mPost.getStatus());
        }
        if (mPost != null && mPost.getUserid().equals(LoginInfo.getInstance(this).getUserAccount())) {
            menu.findItem(R.id.action_editpost).setVisible(true);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterUpdateRecevier();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_collection:
                if (!LoginInfo.getInstance(this).isLogining()) {
                    IntentUtil.toLoginActivity(this);
                    return true;
                }
                if (!isCollection) {
                    isCollection = true;
                    requestCollect(mPost.getPostid(), item);
                    item.setIcon(R.drawable.navigationbar_collect_highlighted);
                } else {
                    isCollection = false;
                    requestCancelCollect(mPost.getPostid(), item);
                    item.setIcon(R.drawable.navigationbar_collect);
                }
                return true;
            case R.id.action_webview:
                IntentUtil.toWebViewActivity(this, mPost.getPostid());
                return true;
            case R.id.action_editpost:
                IntentUtil.toEditSendPostActivity(this, mPost);
                return true;
            case R.id.action_share:
                showToast("分享");
                return true;
            case R.id.action_post_delete:
                new MaterialDialog.Builder(this)
                        .backgroundColorRes(R.color.colorWhite)
                        .content("确定要删除吗?")
                        .contentColorRes(R.color.nomralblack)
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                requestDeletePost(mPost.getPostid());
                            }
                        }).show();
                return true;
            case R.id.action_post_xiachen:
                showToast("下沉");
                return true;
            case R.id.action_set_top:
                if ("0".equals(mPost.getStatus())) {
                    requestChangePostStatus(mPost.getPostid(), "1");
                } else if ("1".equals(mPost.getStatus())) {
                    requestChangePostStatus(mPost.getPostid(), "0");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //更改贴子的状态
    private void requestChangePostStatus(String postid, final String status) {
        String url = DataInterface.UPDATE_POST_STATUS + "postid=" + postid + "&status=" + status;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    showToast(requestData.getBody().getMessage());
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        setTopMenuText(status);
                        mPost.setStatus(status);
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    //收藏
    private void requestCollect(String postid, final MenuItem item) {
        String url = DataInterface.ADD_COLLECT + "userid=" + LoginInfo.getInstance(this).getUserAccount() + "&postid=" + postid;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    showToast(requestData.getBody().getMessage());
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        item.setIcon(R.drawable.navigationbar_collect_highlighted);
                    } else {
                        item.setIcon(R.drawable.navigationbar_collect);
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    //取消收藏
    private void requestCancelCollect(String postid, final MenuItem item) {
        String url = DataInterface.REMOVE_COLLECT + "userid=" + LoginInfo.getInstance(this).getUserAccount() + "&postid=" + postid;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    showToast(requestData.getBody().getMessage());
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        item.setIcon(R.drawable.navigationbar_collect);
                    } else {
                        item.setIcon(R.drawable.navigationbar_collect_highlighted);
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    private void requestIsCollect(String postid) {
        String url = DataInterface.IS_COLLECT + "userid=" + LoginInfo.getInstance(this).getUserAccount() + "&postid=" + postid;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        isCollection = true;
                        mMenu.findItem(R.id.action_collection).setIcon(R.drawable.navigationbar_collect_highlighted);
                    } else {
                        isCollection = false;
                        mMenu.findItem(R.id.action_collection).setIcon(R.drawable.navigationbar_collect);
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    @Override
    protected void initView() {
        groupDao = new GroupDao(this);
        imageWidth = Utils.getScreenWidth(this) - getResources().getDimensionPixelSize(R.dimen.forum_item_margin) * 2;

        mPost = (Post) getIntent().getSerializableExtra(IntentKey.INTENT_POST);
        postid = getIntent().getStringExtra(IntentKey.INTENT_POST_ID);
        etEditContent = (EditText) findViewById(R.id.activity_postdetail_edit);
        ivMore = (ImageView) findViewById(R.id.activity_postdetail_more);
        btSend = (Button) findViewById(R.id.activity_postdetail_send);
        llMoreLayout = (LinearLayout) findViewById(R.id.activity_postdetail_edit_piclayout);
        mHorizontalListView = (HorizontalListView) findViewById(R.id.activity_postdetail_edit_hlistview);
        tvIndicate = (TextView) findViewById(R.id.activity_postdetail_edit_indicate);
        tvSelectCount = (TextView) findViewById(R.id.activity_postdetail_selectpic_count);

        mRecyclerView = (RecyclerView) findViewById(R.id.common_refresh_recyclerview);
        headerView = View.inflate(this, R.layout.header_listview_postdetail, null);
        tvNoData = (TextView) headerView.findViewById(R.id.header_nodata);
        ivSupport = (ImageView) headerView.findViewById(R.id.header_postdetail_support);
        headerView.setVisibility(View.GONE);
        if (mPost != null) {
            initHeaderView(headerView);
        } else {
            requestFindPostByid(postid);
        }
    }

    private void requestFindPostByid(String postid) {
        String url = DataInterface.FIND_THEME_POST + "postid=" + postid;
        RequestManager.getInstance().getRequest(this, url, Post.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    mPost = (Post) object;
                    initHeaderView(headerView);
                    postDetailAdapter.userid = mPost.getUserid();
                    refreshLayout.doRefreshing();
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.SENDING_POST) {
            Constant.SENDING_POST = false;
            requestFindPostByid(mPost.getPostid());
        }
    }

    private void initHeaderView(View headerView) {
        ImageView userIcon = (ImageView) headerView.findViewById(R.id.item_listview_forum_usericon);
        TextView userName = (TextView) headerView.findViewById(R.id.item_listview_forum_username);
        TextView title = (TextView) headerView.findViewById(R.id.item_listview_forum_title);
        LinearLayout textLayout = (LinearLayout) headerView.findViewById(R.id.item_listview_forum_content);
        TextView support = (TextView) headerView.findViewById(R.id.header_postdetail_support_tv);
        TextView reply = (TextView) headerView.findViewById(R.id.header_postdetail_reply_tv);
        TextView createTime = (TextView) headerView.findViewById(R.id.item_listview_forum_time);
        TextView groupName = (TextView) headerView.findViewById(R.id.header_postdetail_group);
        TextView tvPermission = (TextView) headerView.findViewById(R.id.header_listview_forum_permission);

        setPermissionText(tvPermission, mPost.getPermission());
        userName.setText(mPost.getUserName());
        title.setText(mPost.getTitle());
        //content.setText(mPost.getContent());
        createTime.setText(mPost.getCreatetime());
        Utils.setUserImg(mPost.getUserIcon(), userIcon);
        support.setText(mPost.getSupportNum() + "赞");
        reply.setText(mPost.getReplyNum() + "回复");
        groupName.setText(mPost.getGroupname());
        textLayout.removeAllViews();
        EditContentImageUtil.addTextImageToLayout(this, textLayout, mPost.getContent().trim(), mPost.getImages());
        //EditContentImageUtil.showImageInEditTextView(this, content, mPost.getImages(), "");
        if ("protected".equals(mPost.getPermission()) && !groupDao.isGroupMember(mPost.getGroupid())) {
            etEditContent.setEnabled(false);
            etEditContent.setBackgroundResource(R.drawable.no_edit_shape);
            etEditContent.setHint("此贴设为保护，非群成员不可回复");
        }

        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toUserInfoActivity(PostDetailActivity.this, mPost.getGroupid(), mPost.getUserid());
            }
        });
        groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupBean groupBean = new GroupBean();
                groupBean.setGroupid(mPost.getGroupid());
                groupBean.setGroupname(mPost.getGroupname());
                IntentUtil.toGroupOfPostActivity(PostDetailActivity.this, groupBean);
            }
        });
        //是否收藏
        requestIsCollect(mPost.getPostid());
        //是否可以显示删除按钮
        if (mPost.getUserid().equals(LoginInfo.getInstance(this).getUserAccount()) || GroupPermission.isMyAdminGroupid(groupDao, mPost.getGroupid())) {
            if (mMenu != null)
                mMenu.findItem(R.id.action_post_delete).setVisible(true);
        }
        //是否可以显示编辑按钮
        if (mPost != null && mPost.getUserid().equals(LoginInfo.getInstance(this).getUserAccount())) {
            if (mMenu != null)
                mMenu.findItem(R.id.action_editpost).setVisible(true);
        }

        if (isMyAdminGroupid(mPost.getGroupid())) {
            if (mMenu != null) {
                mMenu.findItem(R.id.action_set_top).setVisible(true);
                setTopMenuText(mPost.getStatus());
            }
        }
    }

    private void setPermissionText(TextView textView, String permission) {
        if ("public".equals(permission)) {
            textView.setVisibility(View.VISIBLE);
            textView.setText("公开");
            textView.setTextColor(getResources().getColor(R.color._public));
            textView.setBackgroundResource(R.drawable.public_permission_shape);
        } else if ("protected".equals(permission)) {
            textView.setText("保护");
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(getResources().getColor(R.color._protected));
            textView.setBackgroundResource(R.drawable.protected_permission_shape);
        } else if ("private".equals(permission)) {
            textView.setText("私有");
            textView.setVisibility(View.VISIBLE);
            textView.setTextColor(getResources().getColor(R.color._private));
            textView.setBackgroundResource(R.drawable.private_permission_shape);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void setTopMenuText(String status) {
        if (mMenu != null) {
            if ("0".equals(status)) {
                mMenu.findItem(R.id.action_set_top).setTitle("设为置顶贴");
            } else if ("1".equals(status)) {
                mMenu.findItem(R.id.action_set_top).setTitle("取消置顶");
            }
        }
    }

    /**
     * 这个群id是否是我管理的
     *
     * @param groupid
     * @return
     */
    private boolean isMyAdminGroupid(String groupid) {
        List<String> groupidList = groupDao.findMyAdminGroupid();
        boolean isAdminGroupid = false;
        for (int i = 0; i < groupidList.size(); i++) {
            if (groupidList.get(i).equals(groupid)) {
                isAdminGroupid = true;
                break;
            }
        }
        return isAdminGroupid;
    }

    @Override
    protected void initData() {
        horizontalListViewAdapter = new HorizontalListViewAdapter();
        mHorizontalListView.setAdapter(horizontalListViewAdapter);

        postDetailAdapter = new PostDetailAdapter(this, mPost != null ? mPost.getUserid() : "");
        stringAdapter = new HeaderViewRecyclerAdapter(postDetailAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        postDetailAdapter.addHeaderView(headerView);
        mRecyclerView.setAdapter(stringAdapter);
        refreshLayout.attachLayoutManagerAndHeaderAdapter(linearLayoutManager, stringAdapter);

        if (mPost != null) {
            refreshLayout.doRefreshing();
        }
    }

    @Override
    protected void setListener() {
        ivMore.setOnClickListener(this);
        etEditContent.setOnClickListener(this);
        ivSupport.setOnClickListener(this);
        btSend.setOnClickListener(this);
        etEditContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                llMoreLayout.setVisibility(View.GONE);
            }
        });
        etEditContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !TextUtils.isEmpty(s.toString().trim())) {
                    if (btSend.getVisibility() != View.VISIBLE) {
                        btSend.setVisibility(View.VISIBLE);
                        Animator set = getEnterAnimtor(btSend);
                        set.start();
                    }
                } else {
                    btSend.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onIRefresh() {
        currentIndex = 1;
        refreshLayout.setIsCanLoad(true);
        findReplyPostRequest(currentIndex, mPost.getPostid());
    }

    @Override
    protected void onILoad() {
        currentIndex++;
        findReplyPostRequest(currentIndex, mPost.getPostid());
    }

    private AnimatorSet getEnterAnimtor(final View target) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X,
                0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y,
                0.2f, 1f);

        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(100);
        enter.setInterpolator(new LinearInterpolator());// 线性变化
        enter.playTogether(scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }

    /**
     * 删除
     *
     * @param postid
     */
    private void requestDeletePost(String postid) {
        showBaseProgressDialog("正在删除...");
        String url = DataInterface.DELETE_POST + "postid=" + postid;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                closeBaseProgressDialog();
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    showToast(requestData.getBody().getMessage());
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        Constant.IS_DELETE_POST = true;
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

    /**
     * 查找回复的帖子
     */
    private void findReplyPostRequest(final int pageindex, String parentid) {
        String url = DataInterface.FIND_DETAIL_REPLY_POST_LIST + "pageindex=" + pageindex + "&parentid=" + parentid;
        RequestManager.getInstance().getRequest(PostDetailActivity.this, url, DetailReplyPostListInfo.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                refreshLayout.completeRefresh();
                if (object != null) {
                    headerView.setVisibility(View.VISIBLE);
                    DetailReplyPostListInfo postListInfo = (DetailReplyPostListInfo) object;
                    List<Post> postList = postListInfo.getBody().getPostlist();
                    if (pageindex == 1) {
                        postDetailAdapter.clearData();
                    }
                    postDetailAdapter.setAddData(postList);
                    if (postList == null || postList.size() < Constant.PAGE_SIZE) {
                        refreshLayout.setIsCanLoad(false);
                    }
                    if (postList != null && postList.size() > 0) {
                        tvNoData.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {
                refreshLayout.completeRefresh();
            }
        });
    }

    //隐藏键盘
    private void hideSoftInput() {
        InputMethodManager inManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_postdetail_more:
                hideSoftInput();
                if ("protected".equals(mPost.getPermission()) && !groupDao.isGroupMember(mPost.getGroupid())) {
                    return;
                }
                llMoreLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llMoreLayout.setVisibility(View.VISIBLE);
                    }
                }, 100);
                break;
            case R.id.activity_postdetail_edit:
                llMoreLayout.setVisibility(View.GONE);
                break;
            case R.id.header_postdetail_support:
                requestAddPraise(LoginInfo.getInstance(this).getUserAccount(), mPost.getPostid());
                break;
            case R.id.activity_postdetail_send:
                if (!LoginInfo.getInstance(this).isLogining()) {
                    IntentUtil.toLoginActivity(this);
                    return;
                }
                String content = etEditContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                replyPostRequest(content, mPost.getPostid());
                break;
        }
    }

    private void requestAddPraise(String userid, String postid) {
        String url = DataInterface.ADD_POST_PRAISE;
        ParamList paramList = new ParamList();
        paramList.add(new ParamList.Parameter("userid", userid));
        paramList.add(new ParamList.Parameter("postid", postid));
        paramList.add(new ParamList.Parameter("publictime", DateUtil.generateDateOfTime(System.currentTimeMillis())));
        paramList.add(new ParamList.Parameter("unread", "1"));
        RequestManager.getInstance().postRequest(this, url, paramList, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    showToast(requestData.getBody().getMessage());
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    private void replyPostRequest(String content, String parentid) {
        showBaseProgressDialog("发送中...");
        String url = DataInterface.REPLY_ADD_POST;
        ParamList parameters = new ParamList();
        replypostid = (System.currentTimeMillis() + LoginInfo.getInstance(this).getUserAccount()).hashCode() + "";
        parameters.add(new ParamList.Parameter("postid", replypostid));
        parameters.add(new ParamList.Parameter("parentid", parentid));
        parameters.add(new ParamList.Parameter("userid", LoginInfo.getInstance(this).getUserAccount()));
        parameters.add(new ParamList.Parameter("createtime", DateUtil.generateDateOfTime(System.currentTimeMillis())));
        parameters.add(new ParamList.Parameter("content", content));

        RequestManager.getInstance().postRequest(this, url, parameters, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        //图片的处理
                        uploadImagesToServer();
                        if (mListData == null || mListData.size() == 0) {
                            closeBaseProgressDialog();
                        }
                        etEditContent.setText("");
                        hideSoftInput();
                        //刷新一下
                        findReplyPostRequest(1, mPost.getPostid());
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

    int count = 0;

    private String createSavePath(String savePath, String displayName) {
        return savePath + File.separator + displayName;
    }

    /**
     * 保存本地并上传图片
     * 把发布的图片另存一份到指定的本地位置
     * 然后在把图片上传到服务器上
     */
    private void uploadImagesToServer() {
        for (int i = 0; i < mListData.size(); i++) {
            final MyImage image = mListData.get(i);
            //保存一份到本地
            Bitmap bitmap = FileUtil.compressUserIcon(1200, image.getPath());
            String savePath = getDir("images", MODE_PRIVATE).getPath();
            String displayName = image.getDisplayName();
            displayName = (System.currentTimeMillis() % 1000) + "_" + displayName;//保证唯一
            if (EncodingConvert.isContainsChinese(displayName)) {
                displayName = displayName.hashCode() + ".jpg";
            } else if (displayName.length() > 64) {
                int index = displayName.lastIndexOf(".");
                String suffix = "";
                if (index > 0) {
                    suffix = displayName.substring(index, displayName.length());
                }
                displayName = "superplan_" + EncodingConvert.getRandomString(4) + "_" + System.currentTimeMillis() + suffix;
            }
            savePath = createSavePath(savePath, displayName);
            FileUtil.saveOutput(bitmap, savePath);
            //插入数据库
            image.setUserid(LoginInfo.getInstance(this).getUserAccount());
            image.setPostid(replypostid);
            image.setPath(savePath);
            image.setCreatetime((System.currentTimeMillis() + (i * 1000)) + "");
            //上传到服务器
            NetUtil.uploadImageFile(this, replypostid, image.getCreatetime(), image.getPath(), new UploadListener() {
                @Override
                public void uploadSuccess(String jsonstring) {
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(jsonstring);
                    int code = jsonObject.getIntValue("code");
                    String imageurl_1200 = jsonObject.getString("imageurl_big");
                    String imageurl_400 = jsonObject.getString("imageurl_small");
                    String width = jsonObject.getString("width");
                    String height = jsonObject.getString("height");
                    image.setImageurl_big(imageurl_1200);
                    image.setImageurl_small(imageurl_400);
                    image.setWidth(width);
                    image.setHeight(height);
                    if (code == 1) {
                        count++;
                    } else {
                        Toast.makeText(PostDetailActivity.this, "图片发布失败，请重新发送", Toast.LENGTH_SHORT).show();
                    }
                    if (count == mListData.size()) {
                        //数据回传
                        closeBaseProgressDialog();
                        mListData.clear();
                        horizontalListViewAdapter.notifyDataSetChanged();
                        llMoreLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void uploadFailed() {
                    Toast.makeText(PostDetailActivity.this, "图片发布失败，请重新发送", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 将要发表的图片
     */
    private List<MyImage> mListData = new ArrayList<MyImage>();
    private HorizontalListViewAdapter horizontalListViewAdapter;

    public class HorizontalListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int size = mListData.size();
            tvIndicate.setText(size + "/6");
            if (size > 0) {
                tvSelectCount.setVisibility(View.VISIBLE);
                tvSelectCount.setText(size + "");
            } else {
                tvSelectCount.setVisibility(View.GONE);
                tvSelectCount.setText(size + "");
            }
            return size + 1;
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
            View view = View.inflate(PostDetailActivity.this, R.layout.item_sendpost_selectpic, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.sendpic_item_image);
            ImageView delete = (ImageView) view.findViewById(R.id.sendpic_item_image_delete);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
//            imageView.setLayoutParams(params);
            if (position < mListData.size()) {
                final MyImage image = mListData.get(position);
                ImageLoader.getInstance().displayImage("file://" + image.getPath(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //IntentUtil.toScannerActivity(AddPlanUnitActivity.this, listData, position);
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListData.remove(image);
                        notifyDataSetChanged();
                    }
                });
            } else {
                imageView.setImageResource(R.drawable.addpic_selector);
                delete.setVisibility(View.GONE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(PostDetailActivity.this, MyAlbumActivity.class);
                        intent.putExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, false);
                        intent.putExtra(IntentKey.HAVE_SELECTED_COUNT, mListData.size());
                        PostDetailActivity.this.startActivityForResult(intent, REQUEST_RESULT_SELECT_ALBUM);
                    }
                });
            }
            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_RESULT_SELECT_ALBUM:
                if (data != null) {
                    List<MyImage> listData = data.getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
                    mListData.addAll(listData);
                    horizontalListViewAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * @Description:
     * @author
     * @date 2013-10-28 jieyaozu 10:30:27
     */
    protected void registerUpdateReceiver() {
        if (updataroadcastReceiver == null) {
            updataroadcastReceiver = new UpdataBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(IntentKey.NOTIFY_UPLOAD_IMAGE_SUCCESS);
            filter.addAction(IntentKey.NOTIFY_UPLOAD_IMAGE_FAILED);
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.registerReceiver(updataroadcastReceiver, filter);
        }
    }

    protected void unRegisterUpdateRecevier() {
        if (updataroadcastReceiver != null) {
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.unregisterReceiver(updataroadcastReceiver);
            updataroadcastReceiver = null;
        }
    }

    private UpdataBroadcastReceiver updataroadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;

    /**
     * 2015-11-5
     */
    private class UpdataBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IntentKey.NOTIFY_UPLOAD_IMAGE_SUCCESS.equals(intent.getAction())) {
                requestFindPostByid(mPost.getPostid());
            } else if (IntentKey.NOTIFY_UPLOAD_IMAGE_FAILED.equals(intent.getAction())) {

            }
        }
    }
}
