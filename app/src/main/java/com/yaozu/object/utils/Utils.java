package com.yaozu.object.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.PermissionChecker;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangfang on 2016/7/26.
 */
public class Utils {
    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 得到设备的密度
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * dip转换px
     */
    public static int dip2px(Context context, float px) {
        final float scale = getScreenDensity(context);
        return (int) (px * scale + 0.5f);
    }

    /**
     * px转换dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = getScreenDensity(context);
        return (int) (pxValue / scale + 0.5f);
    }


    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 检测网络连接
     */
    public static boolean checkConnection(Context context) {
        if (context == null)
            return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    // 验证手机格式
    public static boolean isMobileNO(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
	    联通：130、131、132、152、155、156、185、186
	    电信：133、153、180、189、（1349卫通）
	    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
	    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (mobiles.matches(telRegex)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPassword(String pwd) {

        String pwdRegex = "[a-zA-Z0-9\\!\\@\\#\\$\\%\\^\\&\\*\\_\\-\\+\\=\\:\\?]{6,20}";
        if (pwd.matches(pwdRegex)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取设备号
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getDeviceId() != null) {
            return tm.getDeviceId();
        } else {
            return "";
        }
    }

    public static String getProductVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            if (info.versionName != null) {
                return info.versionName;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 计算16:9的图片高度
     *
     * @param context
     * @return
     */
    public static int computeImageHeight(Context context) {
        return computeImageHeight(context, 0, 1);
    }

    /**
     * 计算16:9的图片高度
     *
     * @param context
     * @param padding
     * @return
     */
    public static int computeImageHeight(Context context, int padding) {
        return computeImageHeight(context, padding, 1);
    }

    /**
     * 计算16:9的图片高度
     *
     * @param context
     * @param padding
     * @param count
     * @return
     */
    public static int computeImageHeight(Context context, int padding, int count) {
        int mWidth = Utils.getScreenWidth(context);
        BigDecimal b = new BigDecimal(((mWidth - padding) / count) * 9 / 16);
        int height = (int) b.setScale(0, BigDecimal.ROUND_HALF_UP).floatValue();
        return height;
    }


    public static String getShowTime(long milliseconds) {
        // 获取日历函数
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = null;
        // 判断是否大于60分钟，如果大于就显示小时。设置日期格式
        if (milliseconds / 60000 > 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");
        } else {
            dateFormat = new SimpleDateFormat("00:mm:ss");
        }
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 启动帧动画
     *
     * @param Img
     * @param anim
     */
    public static void startImageAnim(ImageView Img, int anim) {
        Img.setVisibility(View.VISIBLE);
        try {
            Img.setImageResource(anim);
            AnimationDrawable animationDrawable = (AnimationDrawable) Img.getDrawable();
            animationDrawable.start();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止帧动画
     *
     * @param Img
     */
    public static void stopImageAnim(ImageView Img) {
        try {
            AnimationDrawable animationDrawable = (AnimationDrawable) Img.getDrawable();
            animationDrawable.stop();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        Img.setVisibility(View.GONE);
    }

    /**
     * 资源转Bitmap
     *
     * @param context
     * @param id
     * @return
     */
    public static Bitmap resourceIdToBitmap(Context context, int id) {
        InputStream is = context.getResources().openRawResource(id);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 图片转换成圆形
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        return toRoundBitmap(bitmap, 0);
    }

    /**
     * 转换成圆角图片
     *
     * @param bitmap
     * @param radius
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap, float radius) {
        if (bitmap == null)
            return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);// 设置画笔无锯齿
        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
        // 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        if (radius != 0) {
            canvas.drawRoundRect(rectF,radius, radius, paint);
        }else{
            canvas.drawCircle(roundPx, roundPx, roundPx, paint);
        }
        paint.setXfermode(new PorterDuffXfermode(
                android.graphics.PorterDuff.Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }

    /**
     * 设置用户头像
     *
     * @param urlImg
     * @param userImg
     */
    public static void setUserImg(final String urlImg, final ImageView userImg) {
        if (TextUtils.isEmpty(urlImg)) {
            userImg.setImageResource(R.mipmap.icon_user_image_default);
        } else {
            ImageLoader.getInstance().displayImage(
                    urlImg, userImg,
                    Constant.IMAGE_OPTIONS_FOR_USER,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view,
                                    loadedImage);
                            ImageView iv = (ImageView) view;
                            if (loadedImage != null) {
                                iv.setImageBitmap(Utils
                                        .toRoundBitmap(loadedImage));
                            }
                        }
                    });
        }
    }

    /**
     * 设置圆图
     *
     * @param urlImg
     * @param Img
     */
    public static void setRoundImg(final String urlImg, final ImageView Img) {
        if (!TextUtils.isEmpty(urlImg)) {
            ImageLoader.getInstance().displayImage(
                    urlImg, Img,
                    Constant.IMAGE_OPTIONS_FOR_PARTNER,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view,
                                    loadedImage);
                            ImageView iv = (ImageView) view;
                            if (loadedImage != null) {
                                iv.setImageBitmap(Utils
                                        .toRoundBitmap(loadedImage,10));
                            }
                        }
                    });
        }
    }

    /**
     * 设置隐藏键盘
     *
     * @param context
     */
    public static void setHideSoftInput(Context context) {
        InputMethodManager input = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(((BaseActivity) context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * @param urlpath
     * @return Bitmap
     * 根据图片url获取图片对象
     */
    public static Bitmap getUrlToBitmap(String urlpath) {
        Bitmap map = null;

        try {
            URL url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            map = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        return map;
    }

    /**
     * 计算
     *
     * @param lenght 文件长度K
     * @return
     */
    public static String computeByteToM(long lenght) {
        BigDecimal filesize = new BigDecimal(lenght);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "M");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "K");
    }


    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @return
     */
    public static String timeStampToDate(String seconds) {
        if (TextUtils.isEmpty(seconds)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(new Date(Long.valueOf(seconds) * 1000));
    }

    public static String timeStampToDateTime(String seconds) {
        if (TextUtils.isEmpty(seconds)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(new Date(Long.valueOf(seconds) * 1000));
    }


    /**
     * 固定格式化日期 "yyyy-MM-dd HH:mm:ss"
     *
     * @param timestamp
     * @return
     */
    public static String getTimeStampToFormatString(String timestamp) {
        if (!TextUtils.isEmpty(timestamp)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.setTimeInMillis(Long.parseLong(timestamp));
                return format.format(calendar.getTime());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 格式化日期
     *
     * @param timestamp
     * @return
     */
    public static String getTimeStampToFormatString(String timestamp, String pattern) {
        if (!TextUtils.isEmpty(timestamp)) {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.clear();
                calendar.setTimeInMillis(Long.parseLong(timestamp));
                return format.format(calendar.getTime());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return "";
    }


    /**
     * 比较字符串的日期格式的大小
     */

    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1);
        if (between_days < 0) {
            return -1;
        } else if (between_days > 0) {
            return 1;
        }
        return 0;
    }

    /**
     * 获取渠道号
     *
     * @param context
     * @return
     */
    public static String getChanelId(Context context) {
        String channelId = "000000";
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            Object value = info.metaData.get("UMENG_CHANNEL");
            if (value != null) {
                channelId = value.toString();
            }
        } catch (Exception e) {
            Log.e("getChanelId", "get chanel failed: " + e.getMessage());
        }
        return channelId;
    }

    /**
     * 读取缓存文件
     *
     * @param context
     * @param strFilePath
     * @return
     */
    public static String ReadCacheFile(Context context, String strFilePath) {
        if (context == null) {
            return "";
        }
        String cachePath = context.getCacheDir().getAbsolutePath();
        String path = cachePath + File.separator + strFilePath;
        String content = "";
        StringBuffer sb = new StringBuffer("");
        // 打开文件
        File file = new File(path);
        // 如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Logger.d("TestFile", "The File doesn't not exist.");
        } else {
            if (!file.exists())
                return "";
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(
                            instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    // 分行读取
                    while ((line = buffreader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\n");
                    }
                    content = sb.toString();
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Logger.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Logger.d("TestFile", e.getMessage());
            }
        }
        return content;
    }

    /**
     * 缓冲json文件
     *
     * @param context
     * @param filename 文件名称
     * @param json     json数据
     */
    public static void saveCacheFile(Context context, String filename, String json) {
        if (context == null) {
            return;
        }
        String cachePath = context.getCacheDir().getAbsolutePath();
        String fullFilePath = cachePath + File.separator + filename;
        File file = new File(fullFilePath);
        try {
            FileOutputStream outs = new FileOutputStream(file, false);
            byte[] bytes = json.getBytes();
            outs.write(bytes);
            outs.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getRealFilePath(Context mContext, final Uri mUri) {
        if (null == mUri) {
            return null;
        }
        final String scheme = mUri.getScheme();
        String data = null;
        if (scheme == null)
            data = mUri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = mUri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = mContext.getContentResolver().query(mUri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public static String millisToString(long millis) {
        millis /= 1000;
        int minute = (int) (millis / 60);
        int hour = minute / 60;
        int second = (int) (millis % 60);
        minute %= 60;
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }
    }

    /**
     * 保存到本地
     *
     * @param croppedImage
     */

    public static void saveOutput(Bitmap croppedImage, String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream outStream;
        try {
            outStream = new FileOutputStream(file);
            croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            Log.i("CropImage", "bitmap saved tosd,path:" + file.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean selfPermissionGranted(Context context, String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (getTargetSdkVersion(context) >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        return result;
    }

    private static int getTargetSdkVersion(Context context) {
        int version = 0;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            version = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return version;
    }

    /***
     * encode by Base64
     */
    public static String encodeBase64(byte[] input) throws Exception {
        Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
        Method mainMethod = clazz.getMethod("encode", byte[].class);
        mainMethod.setAccessible(true);
        Object retObj = mainMethod.invoke(null, new Object[]{input});
        return (String) retObj;
    }

    /***
     * decode by Base64
     */
    public static byte[] decodeBase64(String input) throws Exception {
        Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
        Method mainMethod = clazz.getMethod("decode", String.class);
        mainMethod.setAccessible(true);
        Object retObj = mainMethod.invoke(null, input);
        return (byte[]) retObj;
    }
}
