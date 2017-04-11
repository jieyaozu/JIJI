package com.yaozu.object.utils;

import android.support.v4.util.LruCache;

public class ObjectBeanCache<T> {
    private static ObjectBeanCache cache;

    private LruCache<String, T> mMemoryCache;

    public ObjectBeanCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); //16M
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory;
        mMemoryCache = new LruCache<String, T>(cacheSize) {
            @Override
            protected int sizeOf(String key, T type) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, T oldValue, T newValue) {
                if (evicted && oldValue != null) {
                    //oldValue.recycle();
                    oldValue = null;
                }
            }
        };
    }

    public void cleanCache() {
        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                mMemoryCache.evictAll();
            }
        }
    }

    /**
     * 取得缓存器实例
     */
    public static ObjectBeanCache getInstance() {
        if (cache == null) {
            cache = new ObjectBeanCache<Object>();
        }
        return cache;
    }

    /**
     * 以软引用的方式对一个Bitmap对象的实例进行引用并保存该引用
     */
    public void addCacheObject(String key, T type) {
        if (getObjectFromMemCache(key) == null && type != null) {
            if (mMemoryCache != null) {
                mMemoryCache.put(key, type);
                Runtime.getRuntime().gc();
            }
        }
    }

    private T getObjectFromMemCache(String key) {
        // 缓存中是否有该Bitmap实例的软引用，如果有，从软引用中取得。
        if (mMemoryCache != null) {
            return mMemoryCache.get(key);
        }
        return null;
    }

    /**
     * 依据所指定的文件名获取图片
     */
    public T getObject(String key) {
        return getObjectFromMemCache(key);
    }
}
