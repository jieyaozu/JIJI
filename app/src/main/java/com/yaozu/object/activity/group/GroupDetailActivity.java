package com.yaozu.object.activity.group;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.activity.CropImageActivity;
import com.yaozu.object.activity.MyAlbumActivity;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.entity.GroupBeanReqData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.listener.DownLoadIconListener;
import com.yaozu.object.listener.UploadListener;
import com.yaozu.object.utils.ACache;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.EncodingConvert;
import com.yaozu.object.utils.FileUtil;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.NetUtil;
import com.yaozu.object.utils.ObjectBeanCache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/4/9.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class GroupDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivGroupIcon, ivEditIcon;
    private ImageView ivReturn;
    private RelativeLayout rlHeaderBackground;
    private ImageView ivGroupMenu;
    private GroupBean mGroupbean;
    private TextView tvGroupName, tvGroupIntroduce;
    private ACache aCache;
    private ObjectBeanCache objectBeanCache;

    private static final int ACTIVITY_RESULT_GALRY = 0;
    private static final int ACTIVITY_RESULT_CROPIMAGE = 1;
    private FileUtil fileUtil = new FileUtil();
    public static String ICON_PATH = FileUtil.getSDPath() + File.separator + FileUtil.APP_FOLDER + File.separator + "icon.jpg";

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_group_detail);
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_LAYOUT_FLAGS | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().getDecorView().setFitsSystemWindows(true);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void initView() {
        aCache = ACache.get(this);
        objectBeanCache = ObjectBeanCache.getInstance();
        ivGroupIcon = (ImageView) findViewById(R.id.group_detail_icon);
        ivEditIcon = (ImageView) findViewById(R.id.group_detail_edit_icon);
        ivReturn = (ImageView) findViewById(R.id.actionbar_return);
        tvGroupName = (TextView) findViewById(R.id.group_detail_groupname);
        tvGroupIntroduce = (TextView) findViewById(R.id.group_detail_groupintroduce);
        ivGroupMenu = (ImageView) findViewById(R.id.groupdetail_actionbar_menu);
        rlHeaderBackground = (RelativeLayout) findViewById(R.id.group_detail_header_background);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xiaoma);
        rlHeaderBackground.setBackground(new BitmapDrawable(getResources(), blur(bitmap)));
    }

    /**
     * 模糊
     *
     * @param bkg
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Bitmap blur(Bitmap bkg) {
        long startMs = System.currentTimeMillis();
        float radius = 25;

        bkg = small(bkg);
        Bitmap bitmap = bkg.copy(bkg.getConfig(), true);

        final RenderScript rs = RenderScript.create(this);
        final Allocation input = Allocation.createFromBitmap(rs, bkg, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        bitmap = big(bitmap);
        //setBackground(new BitmapDrawable(getResources(), bitmap));
        rs.destroy();
        return bitmap;
    }

    private Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(20f, 20f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    private Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.05f, 0.05f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    @Override
    protected void initData() {
        mGroupbean = (GroupBean) getIntent().getSerializableExtra(IntentKey.INTENT_GROUP);
        tvGroupName.setText(mGroupbean.getGroupname());
        if (!TextUtils.isEmpty(mGroupbean.getGroupicon())) {
            ImageLoader.getInstance().displayImage(mGroupbean.getGroupicon(), ivGroupIcon);
            downloadBackgroundImage(mGroupbean.getGroupicon());
        }

        Object object = objectBeanCache.getObject(mGroupbean.getGroupid());
        if (object == null) {
            requestGroupDetail(mGroupbean.getGroupid());
        } else {
            mGroupbean = (GroupBean) object;
            bindDataToView((GroupBean) object);
        }
    }


    /**
     * 查找群的详情
     */
    private void requestGroupDetail(final String groupid) {
        String url = DataInterface.FIND_GROUP_BY_ID + "groupid=" + groupid;
        RequestManager.getInstance().getRequest(this, url, GroupBeanReqData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    GroupBeanReqData groupBeanReqData = (GroupBeanReqData) object;
                    GroupBean groupBean = groupBeanReqData.getBody().getGroupbean();
                    mGroupbean = groupBean;
                    objectBeanCache.addCacheObject(groupid, groupBean);
                    bindDataToView(groupBean);
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    /**
     * 填充数据
     *
     * @param groupBean
     */
    private void bindDataToView(GroupBean groupBean) {
        ImageLoader.getInstance().displayImage(groupBean.getGroupicon(), ivGroupIcon);
        tvGroupName.setText(groupBean.getGroupname());
        tvGroupIntroduce.setText(groupBean.getIntroduce());
        downloadBackgroundImage(groupBean.getGroupicon());
    }

    private void downloadBackgroundImage(final String iconUrl) {
        Bitmap bitmap = aCache.getAsBitmap(iconUrl);
        if (bitmap != null) {
            rlHeaderBackground.setBackground(new BitmapDrawable(getResources(), blur(bitmap)));
            return;
        }
        NetUtil.downLoadBitmap(iconUrl, new DownLoadIconListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void downLoadSuccess(Bitmap bitmap) {
                aCache.put(iconUrl, bitmap);
                rlHeaderBackground.setBackground(new BitmapDrawable(getResources(), blur(bitmap)));
            }

            @Override
            public void downLoadFailed() {

            }
        });
    }

    @Override
    protected void setListener() {
        ivReturn.setOnClickListener(this);
        ivGroupMenu.setOnClickListener(this);
        ivEditIcon.setOnClickListener(this);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.IS_EDITGROUP_SUCCESS) {
            Constant.IS_EDITGROUP_SUCCESS = false;
            objectBeanCache.cleanCache();
            requestGroupDetail(mGroupbean.getGroupid());
        }
    }

    /**
     * 打开相册选取图片
     */
    public void getimgefromegalry() {
        Intent intent = new Intent(this, MyAlbumActivity.class);
        intent.putExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, true);
        this.startActivityForResult(intent, ACTIVITY_RESULT_GALRY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_RESULT_GALRY:
                if (data == null) {
                    return;
                }
                List<MyImage> listData = data.getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
                String path = listData.get(0).getPath();
                final Bitmap bm = FileUtil.compressUserIcon(1200, path);
                Intent cropimage = new Intent(this, CropImageActivity.class);
                IntentKey.cropBitmap = bm;
                startActivityForResult(cropimage, ACTIVITY_RESULT_CROPIMAGE);
                break;
            case ACTIVITY_RESULT_CROPIMAGE:
                if (IntentKey.cropBitmap == null) {
                    return;
                }
                //保存到本地
                FileUtil.saveOutput(IntentKey.cropBitmap, ICON_PATH);
                //上传头像到服务器上
                //NetUtil.uploadUserIconFile(this, new File(ICON_PATH), new MyUploadListener());
                MyImage image = new MyImage();
                image.setPath(ICON_PATH);
                image.setDisplayName(generateDisplayName("icon"));
                List<MyImage> imageList = new ArrayList<>();
                imageList.add(image);
                NetUtil.uploadGroupImagesToServer(this, imageList, mGroupbean.getGroupid(), new UploadListener() {
                    @Override
                    public void uploadSuccess(String jsonstring) {
                        System.out.println();
                        Bitmap bitmap = BitmapFactory.decodeFile(ICON_PATH);
                        if (bitmap != null) {
                            objectBeanCache.cleanCache();
                            ivGroupIcon.setImageBitmap(bitmap);
                            rlHeaderBackground.setBackground(new BitmapDrawable(getResources(), blur(bitmap)));
                        }
                    }

                    @Override
                    public void uploadFailed() {
                        Toast.makeText(GroupDetailActivity.this, "图片发布失败，请重新发送", Toast.LENGTH_SHORT).show();
                    }
                });
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
        } else {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_return:
                finish();
                break;
            case R.id.groupdetail_actionbar_menu:
                IntentUtil.toEditGroupActivity(this, mGroupbean);
                break;
            case R.id.group_detail_edit_icon:
                getimgefromegalry();
                break;
        }
    }
}
