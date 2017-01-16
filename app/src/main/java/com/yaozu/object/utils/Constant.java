package com.yaozu.object.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yaozu.object.R;

/**
 * Created by jxj42 on 2016/12/21.
 */

public class Constant {
    public static final DisplayImageOptions IMAGE_OPTIONS_FOR_USER = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.icon_user_image_default)
            .showImageForEmptyUri(R.mipmap.icon_user_image_default)
            .showImageOnFail(R.mipmap.icon_user_image_default)
            .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY)
            .cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();

    public static final DisplayImageOptions IMAGE_OPTIONS_FOR_PARTNER = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY)
            .cacheInMemory(true).cacheOnDisc(true).resetViewBeforeLoading(true).build();
}
