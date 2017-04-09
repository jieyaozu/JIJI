package com.yaozu.object.activity.group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.activity.MyAlbumActivity;
import com.yaozu.object.activity.SendPostActivity;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.entity.CreateGroupReqData;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.ParamList;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.listener.UploadListener;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.EncodingConvert;
import com.yaozu.object.utils.FileUtil;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.NetUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/4/8.
 */

public class UploadGroupIconActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivGroupSurface;
    private Button btCreateFinish;
    private GroupBean groupBean;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_upload_groupicon);
    }

    @Override
    protected void initView() {
        ivGroupSurface = (ImageView) findViewById(R.id.upload_groupicon);
        btCreateFinish = (Button) findViewById(R.id.create_group_finish);
    }

    @Override
    protected void initData() {
        groupBean = (GroupBean) getIntent().getSerializableExtra(IntentKey.INTENT_GROUP);
    }

    @Override
    protected void setListener() {
        ivGroupSurface.setOnClickListener(this);
        btCreateFinish.setOnClickListener(this);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("上传群封面");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 将要发表的图片
     */
    private List<MyImage> mListData = new ArrayList<MyImage>();
    private final int REQUEST_RESULT_SELECT_ALBUM = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_RESULT_SELECT_ALBUM:
                if (data != null) {
                    mListData.clear();
                    List<MyImage> listData = data.getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
                    for (int i = 0; i < listData.size(); i++) {
                        MyImage image = listData.get(i);
                        String displayName = generateDisplayName(image.getDisplayName());
                        image.setDisplayName(displayName);
                    }
                    mListData.addAll(listData);
                    MyImage myImage = listData.get(0);
                    ImageLoader.getInstance().displayImage("file://" + myImage.getPath(), ivGroupSurface, Constant.IMAGE_OPTIONS_FOR_PARTNER);
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

    /**
     * 请求服务器创建群
     *
     * @param groupBean
     */
    private void requestCreateGroup(GroupBean groupBean) {
        showBaseProgressDialog("发送中...");
        String url = DataInterface.CREATE_MY_GROUP;
        ParamList parameters = new ParamList();
        try {
            parameters.add(new ParamList.Parameter("userid", LoginInfo.getInstance(this).getUserAccount()));
            parameters.add(new ParamList.Parameter("groupname", URLEncoder.encode(groupBean.getGroupname(), "utf-8")));
            parameters.add(new ParamList.Parameter("introduce", groupBean.getIntroduce()));
            parameters.add(new ParamList.Parameter("sectionid", groupBean.getSectionid()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestManager.getInstance().postRequest(this, url, parameters, CreateGroupReqData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    CreateGroupReqData requestData = (CreateGroupReqData) object;
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        //图片的处理
                        NetUtil.uploadGroupImagesToServer(UploadGroupIconActivity.this, mListData, requestData.getBody().getGroupid(), new UploadListener() {
                            @Override
                            public void uploadSuccess(String jsonstring) {
                                //数据回传
                                closeBaseProgressDialog();
                                Constant.IS_CREATEGROUP_SUCCESS = true;
                                finish();
                            }

                            @Override
                            public void uploadFailed() {
                                Toast.makeText(UploadGroupIconActivity.this, "图片发布失败，请重新发送", Toast.LENGTH_SHORT).show();
                                closeBaseProgressDialog();
                                finish();
                            }
                        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_groupicon:
                Intent intent = new Intent(this, MyAlbumActivity.class);
                intent.putExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, true);
                startActivityForResult(intent, REQUEST_RESULT_SELECT_ALBUM);
                break;
            case R.id.create_group_finish:
                if (mListData.size() <= 0) {
                    showToast("请选择一张封面图片");
                    return;
                }
                requestCreateGroup(groupBean);
                break;
        }
    }
}
