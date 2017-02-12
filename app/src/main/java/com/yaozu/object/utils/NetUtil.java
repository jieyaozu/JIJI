package com.yaozu.object.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
    public static void uploadImageFile(final Context context, String postid, String createTime, final File file, final UploadListener uploadListener) {
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

    public static void uploadImageFile(final Context context, String postid, String createTime, final String path, final UploadListener uploadListener) {
        File file = new File(path);
        uploadImageFile(context, postid, createTime, file, uploadListener);
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


    public static void downLoadUserIcon(final String iconUrl, final DownLoadIconListener downLoadListener) {
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

    public static Bitmap getLocalOtherUserIcon(String userid) {
        final String filePath = USERS_ICON_PATH + File.separator + userid + "_icon";
        Bitmap localbitmap = BitmapFactory.decodeFile(filePath);
        return localbitmap;
    }

    /**
     * 原画质
     *
     * @param userid
     * @return
     */
    public static Bitmap getLocalOriginOtherUserIcon(String userid) {
        final String filePath = USERS_ICON_PATH + File.separator + userid + "_icon_og";
        Bitmap localbitmap = BitmapFactory.decodeFile(filePath);
        return localbitmap;
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
