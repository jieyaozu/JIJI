package com.yaozu.object.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.IntentUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/16.
 */

public class ImageListAdapter extends BaseAdapter {
    private Context mContext;

    public ImageListAdapter(Context context) {
        mContext = context;
    }

    private List<MyImage> imagesList = new ArrayList<>();

    public void setData(List<MyImage> images) {
        if (images != null) {
            imagesList.clear();
            imagesList.addAll(images);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.item_noscroll_listview_reply, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.item_noscroll_listview_reply_image);
        MyImage image = imagesList.get(position);
        ImageLoader.getInstance().displayImage(image.getImageurl_big(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toScannerPictureActivity(mContext, (ArrayList<MyImage>) imagesList, position);
            }
        });
        return view;
    }
}
