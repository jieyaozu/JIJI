package com.yaozu.object;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yaozu.object.bean.Post;

/**
 * Created by jxj42 on 2017/2/5.
 */

public class ObjectApplication extends Application {
    public static Post tempPost = null;
    public static String clientid = null;

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }
}
