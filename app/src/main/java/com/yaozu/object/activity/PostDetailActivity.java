package com.yaozu.object.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.adapter.PostDetailAdapter;
import com.yaozu.object.bean.MyImages;
import com.yaozu.object.bean.Post;
import com.yaozu.object.entity.DetailReplyPostListInfo;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.ParamList;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.listener.UploadListener;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.EncodingConvert;
import com.yaozu.object.utils.FileUtil;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.NetUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.HorizontalListView;
import com.yaozu.object.widget.NoScrollListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/8.
 */

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

    private ListView mListView;
    private View headerView;
    private PostDetailAdapter postDetailAdapter;
    private ImageView ivSupport;

    private Post mPost;
    private String replypostid;
    private int currentIndex = 1;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_postdetail);
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
        getMenuInflater().inflate(R.menu.postdetail_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_collection:
                if (!isCollection) {
                    isCollection = true;
                    showToast("收藏成功");
                    item.setIcon(R.drawable.navigationbar_collect_highlighted);
                } else {
                    isCollection = false;
                    showToast("取消收藏成功");
                    item.setIcon(R.drawable.navigationbar_collect);
                }
                return true;
            case R.id.action_share:
                showToast("分享");
                return true;
            case R.id.action_sort:
                showToast("排序");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initView() {
        mPost = (Post) getIntent().getSerializableExtra(IntentKey.INTENT_POST);
        etEditContent = (EditText) findViewById(R.id.activity_postdetail_edit);
        ivMore = (ImageView) findViewById(R.id.activity_postdetail_more);
        btSend = (Button) findViewById(R.id.activity_postdetail_send);
        llMoreLayout = (LinearLayout) findViewById(R.id.activity_postdetail_edit_piclayout);
        mHorizontalListView = (HorizontalListView) findViewById(R.id.activity_postdetail_edit_hlistview);
        tvIndicate = (TextView) findViewById(R.id.activity_postdetail_edit_indicate);
        tvSelectCount = (TextView) findViewById(R.id.activity_postdetail_selectpic_count);

        mListView = (ListView) findViewById(R.id.common_refresh_listview);
        headerView = View.inflate(this, R.layout.header_listview_postdetail, null);
        headerView.setVisibility(View.GONE);
        initHeaderView(headerView);
        mListView.addHeaderView(headerView);
    }

    private void initHeaderView(View headerView) {
        ImageView userIcon = (ImageView) headerView.findViewById(R.id.item_listview_forum_usericon);
        TextView userName = (TextView) headerView.findViewById(R.id.item_listview_forum_username);
        TextView title = (TextView) headerView.findViewById(R.id.item_listview_forum_title);
        TextView content = (TextView) headerView.findViewById(R.id.item_listview_forum_content);
        TextView support = (TextView) headerView.findViewById(R.id.header_postdetail_support_tv);
        TextView reply = (TextView) headerView.findViewById(R.id.header_postdetail_reply_tv);
        content.setTypeface(typeface);
        userName.setTypeface(typeface);
        support.setTypeface(typeface);
        reply.setTypeface(typeface);
        TextView createTime = (TextView) headerView.findViewById(R.id.item_listview_forum_time);
        ivSupport = (ImageView) headerView.findViewById(R.id.header_postdetail_support);
        NoScrollListView noScrollListView = (NoScrollListView) headerView.findViewById(R.id.item_listview_forum_container);

        userName.setText(mPost.getUserName());
        title.setText(mPost.getTitle());
        content.setText(mPost.getContent());
        createTime.setText(mPost.getCreatetime());
        Utils.setUserImg(mPost.getUserIcon(), userIcon);
        reply.setText(mPost.getReplyNum() + "回复");

        NoScrollListViewAdapter noScrollListViewAdapter = new NoScrollListViewAdapter(mPost.getImages());
        noScrollListView.setAdapter(noScrollListViewAdapter);
    }

    @Override
    protected void initData() {
        horizontalListViewAdapter = new HorizontalListViewAdapter();
        mHorizontalListView.setAdapter(horizontalListViewAdapter);

        postDetailAdapter = new PostDetailAdapter(this, typeface, mPost.getUserid());
        mListView.setAdapter(postDetailAdapter);

        refreshLayout.doRefreshing();
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

    private class NoScrollListViewAdapter extends BaseAdapter {
        private List<MyImages> imagesList = new ArrayList<MyImages>();

        public NoScrollListViewAdapter(List<MyImages> images) {
            imagesList.addAll(images);
        }

        @Override
        public int getCount() {
            return imagesList.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = View.inflate(PostDetailActivity.this, R.layout.item_noscroll_listview, null);
            final ImageView imageView = (ImageView) view.findViewById(R.id.item_noscroll_listview_image);
            MyImages image = imagesList.get(position);
            ImageLoader.getInstance().displayImage(image.getImageurl_big(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return view;
        }
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
                    if (postList == null || postList.size() == 0) {
                        refreshLayout.setIsCanLoad(false);
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
                showToast("点赞");
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
            final MyImages image = mListData.get(i);
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
    private List<MyImages> mListData = new ArrayList<MyImages>();
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
                final MyImages image = mListData.get(position);
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
                    List<MyImages> listData = data.getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
                    mListData.addAll(listData);
                    horizontalListViewAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
