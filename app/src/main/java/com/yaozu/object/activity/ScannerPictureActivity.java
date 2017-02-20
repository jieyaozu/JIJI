package com.yaozu.object.activity;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
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

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by jieyz on 2016/4/28.
 */
public class ScannerPictureActivity extends BaseActivity {
    private ViewPager viewPager;
    private List<MyImages> images;
    //当前显示的位置
    private int currentItem = 0;
    private List<GestureImageView> imageViews = new ArrayList<>();
    private ScannerViewPagerAdapder viewPagerAdapder;
    private ActionBar mActionBar;

    private boolean isHide = false;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_scanner_pic);
        setSwipeBackEnable(false);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private GestureImageView createImageView() {
        GestureImageView imageView = new GestureImageView(ScannerPictureActivity.this);
        return imageView;
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        mActionBar = actionBar;
        //actionBar.hide();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle((currentItem + 1) + "/" + images.size());
    }

    @Override
    protected void initView() {
        images = getIntent().getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
        intiImageViews();
        viewPager = (ViewPager) findViewById(R.id.scanner_viewpager);
        viewPagerAdapder = new ScannerViewPagerAdapder();
        viewPager.setAdapter(viewPagerAdapder);

        currentItem = getIntent().getIntExtra(IntentKey.INTENT_ALBUM_IMAGES_INDEX, 0);
        viewPager.setCurrentItem(currentItem);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mActionBar.setTitle((position + 1) + "/" + images.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
                    finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
