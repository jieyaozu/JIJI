package com.yaozu.object.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.bean.MyImages;
import com.yaozu.object.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyz on 2016/4/25.
 */
public class MyAlbumGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<MyImages> images;
    private int itemWidth = 0;
    private int margin = 0;
    private GridView gridView;
    private List<Integer> selectPosition = new ArrayList<Integer>();
    private CheckChangeListener checkChangeListener;
    private boolean isSingle = true;
    public int MAXPICTURE = 6;


    public MyAlbumGridAdapter(Context context, boolean isSingle, GridView gridView, List<MyImages> data) {
        this.mContext = context;
        this.gridView = gridView;
        images = data;
        DisplayMetrics disp = mContext.getResources().getDisplayMetrics();
        itemWidth = disp.widthPixels / 3;
        margin = mContext.getResources().getDimensionPixelSize(R.dimen.album_item_margin);
        this.isSingle = isSingle;
    }

    public List<Integer> getSelectPosition() {
        return selectPosition;
    }

    public void setOnCheckChangeListener(CheckChangeListener checkChangeListener) {
        this.checkChangeListener = checkChangeListener;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = View.inflate(mContext, R.layout.item_myalbum_gridview, null);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.album_item_image);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.album_item_checkbox);
        if (isSingle) {
            checkBox.setVisibility(View.GONE);
        } else {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        if (selectPosition.size() < MAXPICTURE) {
                            if (!selectPosition.contains(position)) {
                                selectPosition.add(position);
                            }
                        } else {
                            if (!selectPosition.contains(position)) {
                                Toast.makeText(mContext, "最多只能选择6张照片", Toast.LENGTH_SHORT).show();
                                checkBox.setChecked(false);
                            }
                        }
                    } else {
                        for (int i = 0; i < selectPosition.size(); i++) {
                            if (position == selectPosition.get(i)) {
                                int value = selectPosition.remove(i);
                                break;
                            }
                        }
                    }
                    if (checkChangeListener != null) {
                        checkChangeListener.onCheckedChanged(checked, selectPosition.size());
                    }
                }
            });

            if (selectPosition.contains(position)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
        params.setMargins(margin, margin, margin, margin);
        imageView.setLayoutParams(params);
        final MyImages myImages = images.get(position);

        imageView.setTag(myImages.getPath());
        ImageLoader.getInstance().displayImage("file://" + myImages.getPath(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);

        //点击或者选中后
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSingle) {
                    Intent intent = new Intent();
                    Uri uri = Uri.parse(myImages.getPath());
                    intent.setData(uri);
                    ((FragmentActivity) mContext).setResult(0, intent);
                    ((FragmentActivity) mContext).finish();
                } else {
                    //IntentUtil.toScannerActivity(mContext, images, position);
                }
            }
        });
        return view;
    }

    public interface CheckChangeListener {
        public void onCheckedChanged(boolean checked, int count);
    }
}
