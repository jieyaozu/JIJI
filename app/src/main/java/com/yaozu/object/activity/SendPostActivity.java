package com.yaozu.object.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yaozu.object.R;
import com.yaozu.object.bean.MyImages;
import com.yaozu.object.bean.Post;
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
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.NetUtil;
import com.yaozu.object.widget.HorizontalListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/6.
 */

public class SendPostActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivPhotoButton;
    private LinearLayout rootView;
    //软件盘弹起后所占高度阀值  
    private int keyHeight = 0;

    private EditText etTitle, etContent;
    private ScrollView scrollView;

    private int selectedCount = 0;
    private final int REQUEST_RESULT_SELECT_ALBUM = 0;

    private HorizontalListView mHorizontalListView;
    private Post mPost;
    private TextView tvIndicate;
    private String postid;
    int count = 0;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_sendpost);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("发贴");
        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sendpost_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_commit:
                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    showToast("标题不能为空");
                    return true;
                }
                if (TextUtils.isEmpty(content)) {
                    content = "";
                }
                Log.d("=====content======>", content);
                //sendPostRequest(title, content);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initView() {
        rootView = (LinearLayout) findViewById(R.id.activity_sendpost_edit_root);
        ivPhotoButton = (ImageView) findViewById(R.id.activity_sendpost_edit_photo);
        etTitle = (EditText) findViewById(R.id.activity_sendpost_edit_title);
        etContent = (EditText) findViewById(R.id.activity_sendpost_edit_content);
        scrollView = (ScrollView) findViewById(R.id.activity_sendpost_edit_scrollview);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void initData() {
        //获取屏幕高度  
        int screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3  
        keyHeight = screenHeight / 3;

        mPost = (Post) getIntent().getSerializableExtra(IntentKey.INTENT_POST);
        etContent.setText(mPost.getContent());
        EditContentImageUtil.showImageInEditTextView(this, etContent, mPost.getImages(), "");
    }

    @Override
    protected void setListener() {
        ivPhotoButton.setOnClickListener(this);
        etTitle.setOnClickListener(this);
        etContent.setOnClickListener(this);
    }

    private void sendPostRequest(String title, String content) {
        showBaseProgressDialog("发送中...");
        String url = DataInterface.ADD_POST;
        ParamList parameters = new ParamList();
        postid = EncodingConvert.getRandomString(4) + ((System.currentTimeMillis() + LoginInfo.getInstance(this).getUserAccount()).hashCode() + "");
        parameters.add(new ParamList.Parameter("postid", postid));
        parameters.add(new ParamList.Parameter("userid", LoginInfo.getInstance(this).getUserAccount()));
        parameters.add(new ParamList.Parameter("status", "0"));
        parameters.add(new ParamList.Parameter("createtime", DateUtil.generateDateOfTime(System.currentTimeMillis())));
        parameters.add(new ParamList.Parameter("title", title));
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
                            finish();
                        }
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
                displayName = "guagua_" + EncodingConvert.getRandomString(4) + "_" + System.currentTimeMillis() + suffix;
            }
            savePath = createSavePath(savePath, displayName);
            FileUtil.saveOutput(bitmap, savePath);
            //插入数据库
            image.setUserid(LoginInfo.getInstance(this).getUserAccount());
            image.setPostid(postid);
            image.setPath(savePath);
            image.setCreatetime((System.currentTimeMillis() + (i * 1000)) + "");
            //上传到服务器
            NetUtil.uploadImageFile(this, postid, image.getCreatetime(), image.getPath(), new UploadListener() {
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
                        Toast.makeText(SendPostActivity.this, "图片发布失败，请重新发送", Toast.LENGTH_SHORT).show();
                    }
                    if (count == mListData.size()) {
                        //数据回传
                        closeBaseProgressDialog();
                        finish();
                    }
                }

                @Override
                public void uploadFailed() {
                    Toast.makeText(SendPostActivity.this, "图片发布失败，请重新发送", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String createSavePath(String savePath, String displayName) {
        return savePath + File.separator + displayName;
    }

    //隐藏键盘
    private void hideSoftInput() {
        InputMethodManager inManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_sendpost_edit_photo:
                hideSoftInput();
                if (mListData.size() == 0) {
                    Intent intent = new Intent(this, MyAlbumActivity.class);
                    intent.putExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, false);
                    intent.putExtra(IntentKey.HAVE_SELECTED_COUNT, selectedCount);
                    startActivityForResult(intent, REQUEST_RESULT_SELECT_ALBUM);
                }
                break;
            case R.id.activity_sendpost_edit_title:
            case R.id.activity_sendpost_edit_content:

                break;
        }
    }

    /**
     * 将要发表的图片
     */
    private List<MyImages> mListData = new ArrayList<MyImages>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_RESULT_SELECT_ALBUM:
                if (data != null) {
                    List<MyImages> listData = data.getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
                    mListData.addAll(listData);
                }
                break;
        }
    }
}
