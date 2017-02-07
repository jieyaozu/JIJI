package com.yaozu.object.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.adapter.MyAlbumGridAdapter;
import com.yaozu.object.entity.MyImages;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.IntentKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jieyz on 2016/4/25.
 */
public class MyAlbumActivity extends BaseActivity implements View.OnClickListener {
    private GridView gridView;
    private MyAlbumGridAdapter albumGridAdapter;
    List<MyImages> listData;
    public static boolean isInit = false;
    public int selectedcount = 0;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_myalbum);
        gridView = (GridView) findViewById(R.id.myalbum_gridview);
        listData = scannerMedia();
        boolean isSingle = getIntent().getBooleanExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, true);
        selectedcount = getIntent().getIntExtra(IntentKey.HAVE_SELECTED_COUNT, 0);
        albumGridAdapter = new MyAlbumGridAdapter(this, isSingle, gridView, listData);
        albumGridAdapter.MAXPICTURE = 6 - selectedcount;
        gridView.setAdapter(albumGridAdapter);


        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int start_index = 0;
            int end_index = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                isInit = true;
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 滑动停止
                        for (; start_index < end_index; start_index++) {
                            MyImages myImages = listData.get(start_index);
                            ImageView imageView = (ImageView) gridView.findViewWithTag(myImages.getPath());
                            ImageLoader.getInstance().displayImage("file://" + myImages.getPath(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
                        }
                        break;

                    default:
                        break;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                start_index = firstVisibleItem;
                end_index = firstVisibleItem + visibleItemCount;
            }
        });
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("选择图片");
        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.myalbum_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.myalbum_action_commit:
                List<Integer> selectpos = albumGridAdapter.getSelectPosition();
                List<MyImages> resultData = new ArrayList<MyImages>();
                for (int i = 0; i < selectpos.size(); i++) {
                    int pos = selectpos.get(i);
                    resultData.add(listData.get(pos));
                }
                Intent result = new Intent();
                result.putParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES, (ArrayList<? extends Parcelable>) resultData);
                this.setResult(0, result);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void onDestroy() {
        isInit = false;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 从系统提供的MediaStore中获取媒体文件的信息
     */
    public List<MyImages> scannerMedia() {
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Images.Media.DATE_ADDED + " desc");
        List<MyImages> images = new ArrayList<MyImages>();
        //Uri mUri = Uri.parse("content://media/external/images/media");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                String width = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
                String height = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
/*                int ringtoneID = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri mImageUri = Uri.withAppendedPath(mUri, "" + ringtoneID);*/
                try {
                    if (TextUtils.isEmpty(width) || Integer.parseInt(width) < 200) {
                        continue;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue;
                }

                MyImages myImage = new MyImages();
                myImage.setDisplayName(displayName);
                myImage.setPath(path);
                myImage.setWidth(width);
                myImage.setHeight(height);
                //myImage.setImageUri(mImageUri);

                images.add(myImage);
            }
            cursor.close();
        }
        return images;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
