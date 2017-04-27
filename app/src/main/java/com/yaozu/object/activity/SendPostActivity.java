package com.yaozu.object.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.yaozu.object.ObjectApplication;
import com.yaozu.object.R;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.bean.PermissionBean;
import com.yaozu.object.bean.Post;
import com.yaozu.object.bean.SectionBean;
import com.yaozu.object.bean.constant.SendStatus;
import com.yaozu.object.db.dao.GroupDao;
import com.yaozu.object.db.dao.SectionDao;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.ParamList;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.EditContentImageUtil;
import com.yaozu.object.utils.EncodingConvert;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.NetUtil;
import com.yaozu.object.utils.SendPostUtil;
import com.yaozu.object.widget.HorizontalListView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/6.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
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
    private String postid;
    private String groupid;
    int count = 0;

    //是新增还是编辑
    private boolean isEdit = false;

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
                if (groupSpinner.getSelectedItemPosition() >= group_data_list.size() - 1) {
                    showToast("请选择群");
                    return true;
                }
                String groupid = group_data_list.get(groupSpinner.getSelectedItemPosition()).getGroupid();

                if (sectionSpinner.getSelectedItemPosition() >= section_data_list.size() - 1) {
                    showToast("请选择版块");
                    return true;
                }
                String permission = permissionBeanList.get(permissionSpinner.getSelectedItemPosition()).getPermissioncode();
                String sectionid = section_data_list.get(sectionSpinner.getSelectedItemPosition()).getSectionid();
                if (!"private".equals(permission) && getNoImageContent(content).length() < 140) {
                    showPermissionRemindDialog();
                    return true;
                }
                //Log.d("=====content======>", content);
                if (!isEdit) {
                    checkImageData(content);//校验一下图片0
                    sendPostRequest(title, content, groupid, sectionid, permission);
                } else {
                    //拿到传进来的图片名
                    for (int i = 0; i < mPost.getImages().size(); i++) {
                        MyImage image = mPost.getImages().get(i);
                        System.out.println("===List里面的name===>" + image.getDisplayName());
                    }
                    //拿到编辑的图片名
                    String[] array = content.split("<img>");
                    for (String str : array) {
                        if (str.contains("</img>")) {
                            System.out.println("===Content 里面的name===>" + str.substring(0, str.indexOf("</img>")));
                        }
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 获得没有图片标签的文本
     *
     * @param contentString
     * @return
     */
    private String getNoImageContent(String contentString) {
        String content = contentString.replaceAll("<img>(.+)</img>", "");
        return content;
    }

    @Override
    protected void initView() {
        rootView = (LinearLayout) findViewById(R.id.activity_sendpost_edit_root);
        ivPhotoButton = (ImageView) findViewById(R.id.activity_sendpost_edit_photo);
        etTitle = (EditText) findViewById(R.id.activity_sendpost_edit_title);
        etContent = (EditText) findViewById(R.id.activity_sendpost_edit_content);
        sectionSpinner = (Spinner) findViewById(R.id.sendpost_select_section);
        groupSpinner = (Spinner) findViewById(R.id.sendpost_select_group);
        permissionSpinner = (Spinner) findViewById(R.id.sendpost_select_permission);
        scrollView = (ScrollView) findViewById(R.id.activity_sendpost_edit_scrollview);
    }

    @Override
    protected void initData() {
        groupDao = new GroupDao(this);
        //获取屏幕高度  
        int screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3  
        keyHeight = screenHeight / 3;

        isEdit = getIntent().getBooleanExtra(IntentKey.INTENT_IS_EDIT_POST, false);
        mPost = (Post) getIntent().getSerializableExtra(IntentKey.INTENT_POST);
        groupid = getIntent().getStringExtra(IntentKey.INTENT_GROUP_ID);
        if (mPost != null) {
            etTitle.setText(mPost.getTitle());
            etContent.setText(mPost.getContent());
            EditContentImageUtil.showImageInEditTextView(this, etContent, mPost.getImages(), "");
        }
        initGroupSpinnerData();
        initSectionSpinnerData();
        initPermissionSpinnerData();
    }

    @Override
    protected void setListener() {
        ivPhotoButton.setOnClickListener(this);
        etTitle.setOnClickListener(this);
        etContent.setOnClickListener(this);
    }

    private void showPermissionRemindDialog() {
        new MaterialDialog.Builder(this)
                .title("提示")
                .titleColorRes(R.color.nomralblack)
                .contentColorRes(R.color.gray)
                .content("发表『公开』或者『保护』的帖子时，长度至少需要140个字符，或者你可以把权限设置为『私有』来发送少于140字符的贴子。")
                .backgroundColorRes(R.color.colorWhite)
                .positiveText("确定")
                .positiveColorRes(R.color.colorPrimary)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        permissionSpinner.performClick();
                    }
                })
                .show();
    }

    private Post createPost(String postid, String groupid, String sectionid, String permission, String userid, String status, String createTime, String title, String content, List<MyImage> myImageList) {
        Post post = new Post();
        post.setPostid(postid);
        post.setGroupid(groupid);
        post.setSectionid(sectionid);
        post.setPermission(permission);
        post.setUserid(userid);
        post.setUserName(LoginInfo.getInstance(this).getUserName());
        post.setStatus(status);
        post.setCreatetime(createTime);
        post.setTitle(title);
        post.setContent(content);
        post.setReplyNum("0");
        post.setSupportNum("0");
        post.setImages(myImageList);
        post.setUploadstatus(SendStatus.UPLOADING);
        return post;
    }

    /**
     * 新增一个帖子
     *
     * @param title
     * @param content
     */
    private void sendPostRequest(final String title, String content, String groupid, String sectionid, String permission) {
        showBaseProgressDialog("发送中...");
        String url = DataInterface.ADD_POST;
        ParamList parameters = new ParamList();
        postid = EncodingConvert.getRandomString(4) + ((System.currentTimeMillis() + LoginInfo.getInstance(this).getUserAccount()).hashCode() + "");
        String createtime = DateUtil.generateDateOfTime(System.currentTimeMillis());
        parameters.add(new ParamList.Parameter("postid", postid));
        parameters.add(new ParamList.Parameter("groupid", groupid));
        parameters.add(new ParamList.Parameter("sectionid", sectionid));
        parameters.add(new ParamList.Parameter("permission", permission));
        parameters.add(new ParamList.Parameter("userid", LoginInfo.getInstance(this).getUserAccount()));
        parameters.add(new ParamList.Parameter("status", "0"));
        parameters.add(new ParamList.Parameter("createtime", createtime));
        parameters.add(new ParamList.Parameter("title", title));
        try {
            parameters.add(new ParamList.Parameter("content", URLEncoder.encode(content, "utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final Post post = createPost(postid, groupid, sectionid, permission, LoginInfo.getInstance(this).getUserAccount(), "0", createtime, title, content, mListData);
        ObjectApplication.tempPost = post;

        RequestManager.getInstance().postRequest(this, url, parameters, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        //图片的处理
                        uploadImagesToServer(post);
                        if (mListData == null || mListData.size() == 0) {
                            post.setUploadstatus("");
                        }
                        closeBaseProgressDialog();
                        Constant.SENDING_POST = true;
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

    /**
     * 保存本地并上传图片
     * 把发布的图片另存一份到指定的本地位置
     * 然后在把图片上传到服务器上
     */
    private void uploadImagesToServer(Post post) {
        NetUtil.uploadPostImagesToServer(getApplicationContext(), post, mListData, postid);
    }

    /**
     * 校对图文中的图片和mListData中的图片
     */
    private void checkImageData(String content) {
        String[] array = content.split("<img>");
        List<MyImage> imageList = new ArrayList<>();
        for (String str : array) {
            if (str.contains("</img>")) {
                String displayName = str.substring(0, str.indexOf("</img>"));
                for (MyImage image : mListData) {
                    if (displayName.equals(image.getDisplayName())) {
                        imageList.add(image);
                    }
                }
            }
        }
        mListData.clear();
        mListData.addAll(imageList);
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
                Intent intent = new Intent(this, MyAlbumActivity.class);
                intent.putExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, false);
                intent.putExtra(IntentKey.HAVE_SELECTED_COUNT, selectedCount);
                startActivityForResult(intent, REQUEST_RESULT_SELECT_ALBUM);
                break;
            case R.id.activity_sendpost_edit_title:
            case R.id.activity_sendpost_edit_content:

                break;
        }
    }

    /**
     * 将要发表的图片
     */
    private List<MyImage> mListData = new ArrayList<MyImage>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_RESULT_SELECT_ALBUM:
                if (data != null) {
                    List<MyImage> listData = data.getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
                    for (int i = 0; i < listData.size(); i++) {
                        MyImage image = listData.get(i);
                        String displayName = generateDisplayName(image.getDisplayName());
                        image.setDisplayName(displayName);
                        Bitmap bitmap = getDestBitmap(this, image);
                        EditContentImageUtil.insertIntoEditText(this, etContent, bitmap, image.getDisplayName());
                    }
                    mListData.addAll(listData);
                }
                break;
        }
    }

    /**
     * 重新生成图片的文件名
     *
     * @param originName
     * @return
     */
    private String generateDisplayName(String originName) {
        String displayName = null;
        originName = (System.currentTimeMillis() % 1000) + "_" + originName;//保证唯一
        if (EncodingConvert.isContainsChinese(originName)) {
            originName = originName.hashCode() + ".jpg";
        } else if (originName.length() > 64) {
            int index = originName.lastIndexOf(".");
            String suffix = "";
            if (index > 0) {
                suffix = originName.substring(index, originName.length());
            }
            originName = "guagua_" + EncodingConvert.getRandomString(4) + "_" + System.currentTimeMillis() + suffix;
        }
        displayName = originName;
        return displayName;
    }

    private static Bitmap getDestBitmap(Context context, MyImage images) {
        Bitmap defaultbmp = BitmapFactory.decodeFile(images.getPath());
        // 获得图片的宽高
        int width = defaultbmp.getWidth();
        int height = defaultbmp.getHeight();
        //目标宽高
        int targetWidth = (int) (context.getResources().getDisplayMetrics().widthPixels);
        int targetHeight = (int) ((Float.parseFloat(images.getHeight()) / Float.parseFloat(images.getWidth())) * targetWidth);
        // 计算缩放比例
        Matrix matrix = new Matrix();
        float scalew = ((float) targetWidth) / width;
        float scaleh = ((float) targetHeight) / height;
        matrix.setScale(scalew, scaleh);
        Bitmap newbm = Bitmap.createBitmap(defaultbmp, 0, 0, width, height, matrix, true);
        return newbm;
    }

    //Spinner
    private Spinner sectionSpinner;
    private SpinnerAdapter arr_adapter;
    private List<SectionBean> section_data_list;
    private SectionDao sectionDao;

    //--------------------群---------------------
    private Spinner groupSpinner;
    private GroupSpinnerAdapter groupSpinnerAdapter;
    private List<GroupBean> group_data_list = new ArrayList<>();
    private GroupDao groupDao;

    //权限
    private Spinner permissionSpinner;
    private PermissonAdapter permissonAdapter;
    private List<PermissionBean> permissionBeanList = new ArrayList<>();

    private void initGroupSpinnerData() {
        groupSpinnerAdapter = new GroupSpinnerAdapter();
        requestGetMyGroup();
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sectionid = group_data_list.get(position).getSectionid();
                int selectPos = SendPostUtil.getSectionSelection(section_data_list, sectionid);
                if (selectPos >= 0) {
                    sectionSpinner.setSelection(selectPos);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void requestGetMyGroup() {
        group_data_list.addAll(groupDao.findAllMyGroup());
        GroupBean groupBean = new GroupBean();
        groupBean.setGroupname("选择群");
        group_data_list.add(groupBean);
        groupSpinner.setAdapter(groupSpinnerAdapter);
        groupSpinner.setSelection(group_data_list.size() - 1);
        if (!TextUtils.isEmpty(groupid)) {
            int selected = SendPostUtil.getGroupSelection(group_data_list, groupid);
            if (selected >= 0) groupSpinner.setSelection(selected);
        }
    }

    class GroupSpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return group_data_list.size() - 1;
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
            LayoutInflater inflater = LayoutInflater.from(SendPostActivity.this);
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = inflater.inflate(R.layout.text_line, null);
            }
            GroupBean groupBean = group_data_list.get(position);
            TextView textView = (TextView) view.findViewById(R.id.text_line_text);
            textView.setText(groupBean.getGroupname());
            return view;
        }
    }


    /**
     * ------------------版块------------------------
     */
    private void initSectionSpinnerData() {
        arr_adapter = new SpinnerAdapter();
        sectionDao = new SectionDao(this);
        section_data_list = sectionDao.findAllSections();
        if (section_data_list != null) {
            SectionBean hintBean = new SectionBean();
            hintBean.setSectionname("版块");
            section_data_list.add(hintBean);
            sectionSpinner.setAdapter(arr_adapter);
            sectionSpinner.setDropDownVerticalOffset(getResources().getDimensionPixelSize(R.dimen.dimen_40));
            sectionSpinner.setSelection(section_data_list.size() - 1);
        }
    }

    class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return section_data_list.size() - 1;
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
            LayoutInflater inflater = LayoutInflater.from(SendPostActivity.this);
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = inflater.inflate(R.layout.text_line, null);
            }
            SectionBean section = section_data_list.get(position);
            TextView textView = (TextView) view.findViewById(R.id.text_line_text);
            textView.setText(section.getSectionname());
            return view;
        }
    }

    /**
     * 权限
     */
    private void initPermissionSpinnerData() {
        permissonAdapter = new PermissonAdapter();
        permissionBeanList.add(new PermissionBean("public", "公开"));
        permissionBeanList.add(new PermissionBean("protected", "保护"));
        permissionBeanList.add(new PermissionBean("private", "私有"));
        permissionSpinner.setAdapter(permissonAdapter);
    }

    class PermissonAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return permissionBeanList.size();
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
            LayoutInflater inflater = LayoutInflater.from(SendPostActivity.this);
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = inflater.inflate(R.layout.text_line, null);
            }
            PermissionBean bean = permissionBeanList.get(position);
            TextView textView = (TextView) view.findViewById(R.id.text_line_text);
            textView.setText(bean.getPermissionname());
            return view;
        }
    }
}
