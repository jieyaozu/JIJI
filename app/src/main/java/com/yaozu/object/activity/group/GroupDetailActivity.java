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
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;

/**
 * Created by jxj42 on 2017/4/9.
 */

public class GroupDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivGroupIcon;
    private ImageView ivReturn;
    private RelativeLayout rlHeaderBackground;

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
        ivGroupIcon = (ImageView) findViewById(R.id.group_detail_icon);
        ivReturn = (ImageView) findViewById(R.id.actionbar_return);
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
        matrix.postScale(4f, 4f); //长和宽放大缩小的比例
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
