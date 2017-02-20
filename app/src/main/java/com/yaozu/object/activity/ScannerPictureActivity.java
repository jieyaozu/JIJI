package com.yaozu.object.activity;

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

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by jieyz on 2016/4/28.
 */
public class ScannerPictureActivity extends BaseActivity {
    private ViewPager viewPager;
    private List<MyImages> images;
    //当前显示的位置
    private int currentItem = 0;
    private List<PhotoView> imageViews = new ArrayList<PhotoView>();
    private ScannerViewPagerAdapder viewPagerAdapder;

    private boolean isHide = false;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_scanner_pic);
        setSwipeBackEnable(false);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.hide();
        actionBar.setTitle("浏览");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initView() {
        images = getIntent().getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
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

    }

    class ScannerViewPagerAdapder extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            ImageLoader.getInstance().displayImage(images.get(position).getImageurl_big(), photoView, Constant.IMAGE_OPTIONS_FOR_PARTNER);

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            PhotoView imageView = imageViews.get(position);
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
