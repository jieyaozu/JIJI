package com.yaozu.object.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.bean.MyImages;
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
    private LinearLayout llPicLayout;
    private LinearLayout rootView;
    //软件盘弹起后所占高度阀值  
    private int keyHeight = 0;

    private EditText etTitle, etContent;
    private ScrollView scrollView;

    private int selectedCount = 0;
    private final int REQUEST_RESULT_SELECT_ALBUM = 0;

    private HorizontalListView mHorizontalListView;
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
                String content = etContent.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    showToast("标题不能为空");
                    return true;
                }
                if (TextUtils.isEmpty(content)) {
                    content = "";
                }
                sendPostRequest(title, content);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initView() {
        rootView = (LinearLayout) findViewById(R.id.activity_sendpost_edit_root);
        ivPhotoButton = (ImageView) findViewById(R.id.activity_sendpost_edit_photo);
        llPicLayout = (LinearLayout) findViewById(R.id.activity_sendpost_edit_piclayout);
        etTitle = (EditText) findViewById(R.id.activity_sendpost_edit_title);
        etContent = (EditText) findViewById(R.id.activity_sendpost_edit_content);
        mHorizontalListView = (HorizontalListView) findViewById(R.id.activity_sendpost_edit_hlistview);
        scrollView = (ScrollView) findViewById(R.id.activity_sendpost_edit_scrollview);
        tvIndicate = (TextView) findViewById(R.id.activity_sendpost_edit_indicate);
    }

    @Override
    protected void initData() {
        //获取屏幕高度  
        int screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3  
        keyHeight = screenHeight / 3;

        horizontalListViewAdapter = new HorizontalListViewAdapter();
        mHorizontalListView.setAdapter(horizontalListViewAdapter);
    }

    private SpannableString getBitmapMime(Bitmap pic, Uri uri) {
        String path = uri.getPath();
        SpannableString ss = new SpannableString(path);
        ImageSpan span = new ImageSpan(this, pic);
        ss.setSpan(span, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private void insertIntoEditText(SpannableString ss) {
        Editable et = etContent.getText();// 先获取Edittext中的内容
        int start = etContent.getSelectionStart();
        et.insert(start, ss);// 设置ss要添加的位置
        etContent.setText(et);// 把et添加到Edittext中
        etContent.setSelection(start + ss.length());// 设置Edittext中光标在最后面显示
    }

    @Override
    protected void setListener() {
        ivPhotoButton.setOnClickListener(this);
        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起  
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                    //showToast("监听到软键盘弹起...");
                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                    //showToast("监听到软件盘关闭...");
                    llPicLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        etTitle.setOnClickListener(this);
        etContent.setOnClickListener(this);
        etTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                llPicLayout.setVisibility(View.GONE);
            }
        });
        etContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                llPicLayout.setVisibility(View.GONE);
            }
        });
    }

    private void sendPostRequest(String title, String content) {
        showBaseProgressDialog("发送中...");
        String url = DataInterface.ADD_POST;
        ParamList parameters = new ParamList();
        postid = (System.currentTimeMillis() + LoginInfo.getInstance(this).getUserAccount()).hashCode() + "";
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
                displayName = "superplan_" + EncodingConvert.getRandomString(4) + "_" + System.currentTimeMillis() + suffix;
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
                llPicLayout.setVisibility(View.GONE);
                break;
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
            tvIndicate.setText(mListData.size() + "/6");
            return mListData.size() + 1;
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
            View view = View.inflate(SendPostActivity.this, R.layout.item_sendpost_selectpic, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.sendpic_item_image);
            ImageView delete = (ImageView) view.findViewById(R.id.sendpic_item_image_delete);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
//            imageView.setLayoutParams(params);
            if (position < mListData.size()) {
                final MyImages image = mListData.get(position);
                ImageLoader.getInstance().displayImage("file://" + image.getPath(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
                //方法二
                //etContent.append(Html.fromHtml("<img src='" + image.getPath() + "'/>", imageGetter, null));
                //方法一
//                String path = image.getPath();
//                Bitmap originalBitmap = BitmapFactory.decodeFile(path);
//                insertIntoEditText(getBitmapMime(originalBitmap, Uri.parse(path)));

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
                        Intent intent = new Intent(SendPostActivity.this, MyAlbumActivity.class);
                        intent.putExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, false);
                        intent.putExtra(IntentKey.HAVE_SELECTED_COUNT, mListData.size());
                        SendPostActivity.this.startActivityForResult(intent, REQUEST_RESULT_SELECT_ALBUM);
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

    //第二种方法
    private Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            Drawable d = Drawable.createFromPath(source);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    };
}
