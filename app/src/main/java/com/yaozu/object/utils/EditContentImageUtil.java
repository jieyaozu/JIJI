package com.yaozu.object.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.widget.UrlImageSpan;

/**
 * Created by jieyaozu on 2017/3/30.
 */

public class EditContentImageUtil {
    public static SpannableString getBitmapMime(Context context, Bitmap pic, String tag) {
        String path = "<img>" + tag + "</img>";
        SpannableString ss = new SpannableString(path);
        ImageSpan span = new ImageSpan(context, pic);
        ss.setSpan(span, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static void insertIntoEditText(EditText editText, SpannableString ss) {
        Editable et = editText.getText();// 先获取Edittext中的内容
        int start = editText.getSelectionStart();
        et.insert(start, ss);// 设置ss要添加的位置
        editText.setText(et);// 把et添加到Edittext中
        editText.setSelection(start + ss.length());// 设置Edittext中光标在最后面显示
    }

    /**
     * 向editText中插入
     *
     * @param context
     * @param editText 被插入的输入框
     * @param pic      插入的图片
     * @param tag      图片的唯一标识
     */
    public static void insertIntoEditText(Context context, EditText editText, Bitmap pic, String tag) {
        SpannableString spannableString = getBitmapMime(context, pic, tag);
        insertIntoEditText(editText, spannableString);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void conbineEditText(Context context, TextView content, String imageUrl, String tag) {
        Uri uri = Uri.parse(imageUrl);
        Bitmap defaultbmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.navigationbar_collect);
        int targetWidth = (int) (context.getResources().getDisplayMetrics().widthPixels);
        Matrix matrix = new Matrix();
        // 获得图片的宽高
        int width = defaultbmp.getWidth();
        int height = defaultbmp.getHeight();
        // 计算缩放比例
        float scalew = ((float) targetWidth) / width;
        float scaleh = ((float) targetWidth / 2) / height;
        matrix.setScale(scalew, scaleh);

        Bitmap newbm = Bitmap.createBitmap(defaultbmp, 0, 0, targetWidth, targetWidth / 2);
        UrlImageSpan imageSpan = new UrlImageSpan(context, newbm, imageUrl, content);
        SpannableString spannableString = new SpannableString(imageUrl);
        spannableString.setSpan(imageSpan, 0, imageUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        content.append("\r\n");
        content.append(spannableString);
    }

    //第二种方法
    private Html.ImageGetter imageGetter = new Html.ImageGetter() {
        @Override
        public Drawable getDrawable(String source) {
            Drawable d = Drawable.createFromPath(source);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        }
    };
}
