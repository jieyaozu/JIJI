package com.yaozu.object.activity.group;

import android.annotation.TargetApi;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.entity.GroupBeanReqData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.listener.DownLoadIconListener;
import com.yaozu.object.utils.ACache;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.NetUtil;

/**
 * Created by jxj42 on 2017/4/9.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class GroupDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivGroupIcon;
    private ImageView ivReturn;
    private RelativeLayout rlHeaderBackground;
    private GroupBean mGroupbean;
    private TextView tvGroupName, tvGroupIntroduce;

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
        ivGroupIcon = (ImageView) findViewById(R.id.group_detail_icon);
        ivReturn = (ImageView) findViewById(R.id.actionbar_return);
        tvGroupName = (TextView) findViewById(R.id.group_detail_groupname);
        tvGroupIntroduce = (TextView) findViewById(R.id.group_detail_groupintroduce);
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

        requestGroupDetail(mGroupbean.getGroupid());
    }


    /**
     * 查找群的详情
     */
    private void requestGroupDetail(String groupid) {
        String url = DataInterface.FIND_GROUP_BY_ID + "groupid=" + groupid;
        RequestManager.getInstance().getRequest(this, url, GroupBeanReqData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    GroupBeanReqData groupBeanReqData = (GroupBeanReqData) object;
                    GroupBean groupBean = groupBeanReqData.getBody().getGroupbean();
                    ImageLoader.getInstance().displayImage(groupBeanReqData.getBody().getGroupbean().getGroupicon(), ivGroupIcon);
                    tvGroupName.setText(groupBean.getGroupname());
                    tvGroupIntroduce.setText(groupBean.getIntroduce());
                    downloadBackgroundImage(groupBean.getGroupicon());
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    private ACache aCache;

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
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_return:
                finish();
                break;
        }
    }
}
