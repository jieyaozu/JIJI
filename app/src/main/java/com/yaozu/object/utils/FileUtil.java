package com.yaozu.object.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 耀祖 on 2015/12/26.
 */
public class FileUtil {
    private static String TAG = FileUtil.class.getSimpleName();
    private String SDPATH;
    private String DWONLOADPATH;
    public static String APP_FOLDER = "mayimen";

    /**
     * "/superplan/file/"
     */
    public String getFileSDPATH() {
        return SDPATH;
    }

    /**
     * "/superplan/download/"
     */
    public String getDownLoadPath() {
        return DWONLOADPATH;
    }

    public FileUtil() {
        //得到当前外部存储设备的目录
        // /SDCARD
        SDPATH = getSDPath() + File.separator + APP_FOLDER + File.separator + "file" + File.separator;
        DWONLOADPATH = getSDPath() + File.separator + APP_FOLDER + File.separator + "download" + File.separator;
        File dir = new File(SDPATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File downdir = new File(DWONLOADPATH);
        if (!downdir.exists()) {
            downdir.mkdirs();
        }
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); //
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//
        }
        if (sdDir == null) {
            return null;
        }
        return sdDir.toString();

    }

    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    public File creatSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        if (!file.exists()) {
            file.createNewFile();
            Log.d(TAG, "=======创建文件:SDPATH + fileName======>" + SDPATH + fileName);
        } else {
            Log.d(TAG, "=======文件已存在:SDPATH + fileName======>" + SDPATH + fileName);
        }
        return file;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public File creatSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        if (!dir.exists()) {
            dir.mkdirs();
            Log.d(TAG, "=======创建文件夹:SDPATH + dirName======>" + SDPATH + dirName);
        } else {
            Log.d(TAG, "=======文件夹已存在:SDPATH + dirName======>" + SDPATH + dirName);
        }
        return dir;
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    public static long getAppCacheSize(Context context) {
        //File file = context.getDir("cache", Context.MODE_PRIVATE);
        String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
        File file = new File(cacheDirPath);
        return getSize(file);
    }

    public static void cleanAppCache(Context context) {
        //File file = context.getDir("cache", Context.MODE_PRIVATE);
        String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
        File file = new File(cacheDirPath);
        delFolder(file.getPath());
    }

    /**
     * 计算文件或者文件夹的大小 ，单位 字节
     *
     * @param file 要计算的文件或者文件夹 ， 类型：java.io.File
     * @return 大小，单位：字节
     */
    public static long getSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小，如果是文件则直接返回其大小
            if (!file.isFile()) {
                //获取文件大小
                File[] fl = file.listFiles();
                long ss = 0;
                for (File f : fl)
                    ss += getSize(f);
                return ss;
            } else {
                long ss = file.length();
                return ss;
            }
        } else {
            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
            return 0;
        }
    }

    /**
     * 删除文件夹
     * param folderPath 文件夹完整绝对路径
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除指定文件夹下所有文件
    //param path 文件夹完整绝对路径
    private static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public void write2SDFromInput(File file, InputStream input) {
        OutputStream output = null;
        try {
/*            creatSDDir(path);
            file = creatSDFile(path + File.separator + fileName);*/
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            while ((input.read(buffer)) != -1) {
                output.write(buffer);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 压缩图片
     */
    public static Bitmap compressUserIcon(int maxWidth, String srcpath) {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(srcpath, localOptions);
        if (localOptions.outWidth > maxWidth) {
            int j = localOptions.outWidth / (maxWidth / 2);
            localOptions.inSampleSize = j;
        }
        localOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(srcpath, localOptions);
    }

    /**
     * 压缩图片
     */
    public static Bitmap compressUserIcon(int maxWidth, int rawWidth, String srcpath) {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        if (rawWidth > maxWidth) {
            int j = rawWidth / (maxWidth / 2);
            localOptions.inSampleSize = j;
        }
        localOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(srcpath, localOptions);
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

    private static BitmapFactory.Options localOptions;

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
