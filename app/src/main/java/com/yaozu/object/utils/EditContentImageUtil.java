package com.yaozu.object.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.widget.UrlImageSpan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyaozu on 2017/3/30.
 */

public class EditContentImageUtil {
    public static SpannableString getBitmapMime(Context context, Bitmap pic, String imagename) {
        String path = "<img>" + imagename + "</img>";
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
        editText.append("\r\n");
        editText.setSelection(start + ss.length());// 设置Edittext中光标在最后面显示
    }

    /**
     * 向editText中插入
     *
     * @param context
     * @param editText  被插入的输入框
     * @param pic       插入的图片
     * @param imagename 图片的唯一标识
     */
    public static void insertIntoEditText(Context context, EditText editText, Bitmap pic, String imagename) {
        SpannableString spannableString = getBitmapMime(context, pic, imagename);
        insertIntoEditText(editText, spannableString);
    }

    private static Bitmap getDefaultbgBitmap(Context context, MyImage images) {
        Bitmap defaultbmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading_bg);
        // 获得图片的宽高
        int width = defaultbmp.getWidth();
        int height = defaultbmp.getHeight();
        //目标宽高
        int targetWidth = (int) (context.getResources().getDisplayMetrics().widthPixels);
        int targetHeight = (int) ((Float.parseFloat(images.getHeight()) / Float.parseFloat(images.getWidth())) * targetWidth);
        // 计算缩放比例
        Matrix matrix = new Matrix();
        float scalew = ((float) targetWidth) / width;
        float scaleh = ((float) targetHeight) / height;
        matrix.setScale(scalew, scaleh);
        Bitmap newbm = Bitmap.createBitmap(defaultbmp, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * @param context
     * @param content
     * @param images
     * @param tag
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void showImageInEditTextView(Context context, EditText content, List<MyImage> images, String tag) {
        String contentString = content.getText().toString();
        String[] arrayString = contentString.split("<img>");
        Log.d("length:", "" + arrayString.length);
        content.setText("");
        //TODO
        for (int i = 0; i < arrayString.length; i++) {
            String p = arrayString[i];
            //有图片
            if (p.contains("</img>")) {
                String displayName = p.substring(0, p.indexOf("</img>"));
                p = p.substring(p.indexOf("</img>") + "</img>".length(), p.length());
                //插入图片
                if (images != null && images.size() > 0) {
                    MyImage image = getDesImage(images, displayName, null);//images.get(pos);
                    if (image != null) {
                        Bitmap defaultbitmap = getDefaultbgBitmap(context, image);
                        UrlImageSpan imageSpan = new UrlImageSpan(context, defaultbitmap, image.getImageurl_big(), content);
                        String path = "<img>" + image.getDisplayName() + "</img>";
                        SpannableString spannableString = new SpannableString(path);
                        spannableString.setSpan(imageSpan, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        //content.append("\r\n");
                        content.append(spannableString);
                    }
                } else {
                    //Toast.makeText(context, "图片丢失了", Toast.LENGTH_SHORT).show();
                    Log.e("EditContentImageUtil:", "图片丢失了");
                }
            }
            content.append(p);
        }
    }

    public static void addTextImageToLayout(final Context context, LinearLayout layout, String contentString, final List<MyImage> imagesList) {
        //重新排好序的
        List<MyImage> orderList = new ArrayList<>();

        String[] arrayString = contentString.split("<img>");
        for (int i = 0; i < arrayString.length; i++) {
            TextView textView = (TextView) View.inflate(context, R.layout.item_textview, null);
            String p = arrayString[i];
            Log.d("======p=====>", i + ": " + p);
            //有图片
            if (p.contains("</img>")) {
                String displayName = p.substring(0, p.indexOf("</img>"));
                p = p.substring(p.indexOf("</img>") + "</img>".length(), p.length());
                //插入图片
                if (imagesList != null && imagesList.size() > 0) {
                    MyImage image = getDesImage(imagesList, displayName, orderList);//imagesList.get(pos);
                    if (image != null) {
                        ImageView imageView = (ImageView) View.inflate(context, R.layout.item_imageview, null);
                        int imageWidth = Utils.getScreenWidth(context) - context.getResources().getDimensionPixelSize(R.dimen.forum_item_margin) * 2;
                        int imageHeight = (int) (imageWidth * (Float.parseFloat(image.getHeight()) / Float.parseFloat(image.getWidth())));
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageWidth, imageHeight);
                        layoutParams.topMargin = context.getResources().getDimensionPixelSize(R.dimen.dimen_5);
                        ImageLoader.getInstance().displayImage(image.getImageurl_big(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
                        imageView.setLayoutParams(layoutParams);
                        layout.addView(imageView);

                        final int finalI = i - 1;
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                IntentUtil.toScannerPictureActivity(context, (ArrayList<MyImage>) imagesList, finalI);
                            }
                        });
                    }
                } else {
                    Log.e("EditContentImageUtil:", "图片丢失了");
                }
            }
            layout.addView(textView);
            textView.setText(p);
        }
        if (imagesList != null) {
            if (orderList.size() > 0) imagesList.clear();
            imagesList.addAll(orderList);
        }
    }

    /**
     * @param imageList
     * @param desDisplayname 目标图片
     * @return
     */
    private static MyImage getDesImage(List<MyImage> imageList, String desDisplayname, List<MyImage> orderList) {
        if (imageList != null) {
            for (int i = 0; i < imageList.size(); i++) {
                MyImage image = imageList.get(i);
                if (desDisplayname.equals(image.getDisplayName())) {
                    imageList.remove(image);
                    if (orderList != null) {
                        orderList.add(image);
                    }
                    return image;
                }
            }
        }
        return null;
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
