package com.yaozu.object.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.bean.MyImages;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.widget.polites.GestureImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyz on 2016/4/28.
 */
public class ScannerPictureActivity extends BaseActivity {
    private ViewPager viewPager;
    private List<MyImages> images;
    //当前显示的位置
    private int currentItem = 0;
    private List<GestureImageView> imageViews = new ArrayList<GestureImageView>();
    private ScannerViewPagerAdapder viewPagerAdapder;

    private boolean isHide = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_pic);
        images = getIntent().getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
        intiImageViews();
        viewPager = (ViewPager) findViewById(R.id.scanner_viewpager);
        viewPagerAdapder = new ScannerViewPagerAdapder();
        viewPager.setAdapter(viewPagerAdapder);

        currentItem = getIntent().getIntExtra(IntentKey.INTENT_ALBUM_IMAGES_INDEX, 0);
        viewPager.setCurrentItem(currentItem);
    }

    @Override
    protected void setContentView() {

    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    /**
     * 初始化ImageView
     */
    private void intiImageViews() {
        for (int i = 0; i < images.size(); i++) {
            GestureImageView imageView = createImageView();
            imageViews.add(imageView);
        }
    }

    class ScannerViewPagerAdapder extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GestureImageView imageView = imageViews.get(position);
            ImageLoader.getInstance().displayImage(images.get(position).getImageurl_big(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
            container.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            GestureImageView imageView = imageViews.get(position);
            container.removeView(imageView);
            imageView.setImageBitmap(null);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private GestureImageView createImageView() {
        GestureImageView imageView = new GestureImageView(ScannerPictureActivity.this);
        return imageView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
