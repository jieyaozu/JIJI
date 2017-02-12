package com.yaozu.object.listener;

/**
 * Created by 耀祖 on 2016/1/31.
 */
public interface UploadListener {
    public void uploadSuccess(String jsonstring);

    public void uploadFailed();
}
