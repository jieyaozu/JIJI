package com.yaozu.object.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yaozu.object.ObjectApplication;
import com.yaozu.object.activity.SendPostActivity;
import com.yaozu.object.activity.group.UploadGroupIconActivity;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.bean.Post;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.listener.DownLoadIconListener;
import com.yaozu.object.listener.UploadListener;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by 耀祖 on 2015/12/22.
 */
public class NetUtil {
    public static String USERS_ICON_PATH = FileUtil.getSDPath() + File.separator + "superplan" + File.separator + "usericons";

    /**
     * 上传头像到服务器
     *
     * @param file
     */
    public static void uploadUserIconFile(final Context context, final File file, final UploadListener uploadListener) {
        LoginInfo user = new LoginInfo(context);
        // 组拼上传的数据
        Part[] parts = new Part[0];
        try {
            parts = new Part[]{new StringPart("source", "695132533"),
                    new StringPart("userid", user.getUserAccount()),
                    new FilePart("file", file)};
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        uploadIconFile(DataInterface.UPLOAD_USERICON, parts, uploadListener);
    }

    /**
     * 上传计划动态图片
     *
     * @param context
     * @param file
     * @param uploadListener
     */
    private static void uploadImageFile(final Context context, String postid, String createTime, final File file, final UploadListener uploadListener) {
        LoginInfo user = new LoginInfo(context);
        // 组拼上传的数据
        Part[] parts = new Part[0];
        try {
            parts = new Part[]{new StringPart("source", "695132533"),
                    new StringPart("userid", user.getUserAccount()),
                    new StringPart("postid", postid),
                    new StringPart("createtime", createTime),
                    new FilePart("file", file)};
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        uploadIconFile(DataInterface.UPLOAD_IMAGES, parts, uploadListener);
    }

    /**
     * 上传群封面图
     *
     * @param context
     * @param groupid
     * @param createTime
     * @param file
     * @param uploadListener
     */
    private static void uploadGroupImageFile(final Context context, String groupid, String createTime, final File file, final UploadListener uploadListener) {
        LoginInfo user = new LoginInfo(context);
        // 组拼上传的数据
        Part[] parts = new Part[0];
        try {
            parts = new Part[]{new StringPart("source", "695132533"),
                    new StringPart("userid", user.getUserAccount()),
                    new StringPart("groupid", groupid),
                    new StringPart("createtime", createTime),
                    new FilePart("file", file)};
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        uploadIconFile(DataInterface.UPLOAD_GROUP_IMAGES, parts, uploadListener);
    }

    public static void uploadImageFile(final Context context, String postid, String createTime, final String path, final UploadListener uploadListener) {
        File file = new File(path);
        uploadImageFile(context, postid, createTime, file, uploadListener);
    }

    public static void uploadGroupImageFile(final Context context, String groupid, String createTime, final String path, final UploadListener uploadListener) {
        File file = new File(path);
        uploadGroupImageFile(context, groupid, createTime, file, uploadListener);
    }

    /**
     * 上传文件
     *
     * @param parts
     */
    public static void uploadIconFile(final String url, final Part[] parts, final UploadListener uploadListener) {
        final int SUCCESS = 1;
        final int FAILED = 0;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SUCCESS:
                        if (uploadListener != null) {
                            uploadListener.uploadSuccess((String) msg.obj);
                        }
                        break;
                    case FAILED:
                        if (uploadListener != null) {
                            uploadListener.uploadFailed();
                        }
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 创建一个httppost的请求
                PostMethod filePost = new PostMethod(url);
                try {
                    filePost.setRequestEntity(new MultipartRequestEntity(parts,
                            filePost.getParams()));
                    HttpClient client = new HttpClient();
                    client.getHttpConnectionManager().getParams()
                            .setConnectionTimeout(5000);
                    int status = client.executeMethod(filePost);
                    if (status == 200) {
                        Log.d("NetUtil", "上传文件成功");
                        String jsonstring = filePost.getResponseBodyAsString();
                        Message msg = handler.obtainMessage();
                        msg.what = SUCCESS;
                        msg.obj = jsonstring;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = FAILED;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Log.e("NetUtil", e.getLocalizedMessage(), e);
                    Message msg = handler.obtainMessage();
                    msg.what = FAILED;
                    handler.sendMessage(msg);
                } finally {
                    filePost.releaseConnection();
                }
            }
        }).start();
    }

    /**
     * 上传群封面图片
     * 保存本地并上传图片
     * 把发布的图片另存一份到指定的本地位置
     * 然后在把图片上传到服务器上
     */
    public static void uploadGroupImagesToServer(Context context, List<MyImage> mListData, String groupid, UploadListener listener) {
        for (int i = 0; i < mListData.size(); i++) {
            final MyImage image = mListData.get(i);
            //保存一份到本地
            Bitmap bitmap = FileUtil.compressUserIcon(1200, image.getPath());
            String savePath = context.getDir("images", Context.MODE_PRIVATE).getPath();
            String displayName = image.getDisplayName();

            savePath = createSavePath(savePath, displayName);
            FileUtil.saveOutput(bitmap, savePath);
            //插入数据库
            image.setPath(savePath);
            image.setCreatetime((System.currentTimeMillis() + (i * 1000)) + "");
            //上传到服务器
            NetUtil.uploadGroupImageFile(context, groupid, image.getCreatetime(), image.getPath(), listener);
        }
    }

    private static int count = 0;

    /**
     * 发送主题贴子图片
     *
     * @param context
     * @param mListData
     * @param postid
     */
    public static void uploadPostImagesToServer(final Context context, final Post post, final List<MyImage> mListData, String postid) {
        post.setUploadstatus("uploading");
        count = 0;
        for (int i = 0; i < mListData.size(); i++) {
            final MyImage image = mListData.get(i);
            //保存一份到本地
            Bitmap bitmap = FileUtil.compressUserIcon(1200, image.getPath());
            String savePath = context.getDir("images", Context.MODE_PRIVATE).getPath();
            String displayName = image.getDisplayName();

            savePath = createSavePath(savePath, displayName);
            FileUtil.saveOutput(bitmap, savePath);
            image.setPath(savePath);
            image.setCreatetime((System.currentTimeMillis() + (i * 1000)) + "");
            //上传到服务器
            NetUtil.uploadImageFile(context, postid, image.getCreatetime(), image.getPath(), new UploadListener() {
                @Override
                public void uploadSuccess(String jsonstring) {
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(jsonstring);
                    int code = jsonObject.getIntValue("code");
                    if (code == 1) {
                        count++;
                        image.setSendSuccess(1);
                    } else {
                        image.setSendSuccess(0);
                        post.setUploadstatus("failed");

                        Intent playingintent = new Intent(IntentKey.NOTIFY_UPLOAD_IMAGE_FAILED);
                        LocalBroadcastManager playinglocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                        playinglocalBroadcastManager.sendBroadcast(playingintent);
                        Log.i("", "图片发布失败:" + image.getDisplayName());
                    }
                    if (count == mListData.size()) {
                        post.setUploadstatus("success");
                        Intent playingintent = new Intent(IntentKey.NOTIFY_UPLOAD_IMAGE_SUCCESS);
                        LocalBroadcastManager playinglocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                        playinglocalBroadcastManager.sendBroadcast(playingintent);
                        ObjectApplication.tempPost = null;
                        Log.i("", "图片发布成功:" + image.getDisplayName());
                    }
                }

                @Override
                public void uploadFailed() {
                    post.setUploadstatus("failed");
                    Intent playingintent = new Intent(IntentKey.NOTIFY_UPLOAD_IMAGE_FAILED);
                    LocalBroadcastManager playinglocalBroadcastManager = LocalBroadcastManager.getInstance(context);
                    playinglocalBroadcastManager.sendBroadcast(playingintent);
                    Toast.makeText(context, "网络错误，图片发布失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private static String createSavePath(String savePath, String displayName) {
        return savePath + File.separator + displayName;
    }

    public static void downLoadBitmap(final String iconUrl, final DownLoadIconListener downLoadListener) {
        final int SUCCESS = 1;
        final int FAILED = 0;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SUCCESS:
                        if (downLoadListener != null) {
                            Bitmap bmp = (Bitmap) msg.obj;
                            downLoadListener.downLoadSuccess(bmp);
                        }
                        break;
                    case FAILED:
                        if (downLoadListener != null) {
                            downLoadListener.downLoadFailed();
                        }
                        break;
                }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                URL url;
                try {
                    url = new URL(iconUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(30000);
                    InputStream is = conn.getInputStream();
                    if (is == null) {
                        Message msg = handler.obtainMessage();
                        msg.what = FAILED;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = SUCCESS;
                        msg.obj = BitmapFactory.decodeStream(is);
                        handler.sendMessage(msg);
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = handler.obtainMessage();
                    msg.what = FAILED;
                    handler.sendMessage(msg);
                }
            }
        };
        new Thread(runnable).start();
    }

    /**
     * 获得网络连接是否可用
     *
     * @param context
     * @return
     * @author 揭耀祖
     */
    public static boolean hasNetwork(Context context) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo workinfo = con.getActiveNetworkInfo();
        if (workinfo == null || !workinfo.isAvailable()) {
            //Toast.makeText(context, R.string.net_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
