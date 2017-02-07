package com.yaozu.object.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.entity.MyImages;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.widget.HorizontalListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/6.
 */

public class SendPostActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivPhotoButton;
    private LinearLayout llPicLayout;
    private LinearLayout rootView;
    //软件盘弹起后所占高度阀值  
    private int keyHeight = 0;

    private EditText etTitle, etContent;
    private ScrollView scrollView;

    private int selectedCount = 0;
    private final int REQUEST_RESULT_SELECT_ALBUM = 0;

    private HorizontalListView mHorizontalListView;
    private TextView tvIndicate;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_sendpost);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("发贴");
        // Enable the Up button
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sendpost_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_commit:
                showToast("完成");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initView() {
        rootView = (LinearLayout) findViewById(R.id.activity_sendpost_edit_root);
        ivPhotoButton = (ImageView) findViewById(R.id.activity_sendpost_edit_photo);
        llPicLayout = (LinearLayout) findViewById(R.id.activity_sendpost_edit_piclayout);
        etTitle = (EditText) findViewById(R.id.activity_sendpost_edit_title);
        etContent = (EditText) findViewById(R.id.activity_sendpost_edit_content);
        mHorizontalListView = (HorizontalListView) findViewById(R.id.activity_sendpost_edit_hlistview);
        scrollView = (ScrollView) findViewById(R.id.activity_sendpost_edit_scrollview);
        tvIndicate = (TextView) findViewById(R.id.activity_sendpost_edit_indicate);
    }

    @Override
    protected void initData() {
        //获取屏幕高度  
        int screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3  
        keyHeight = screenHeight / 3;

        horizontalListViewAdapter = new HorizontalListViewAdapter();
        mHorizontalListView.setAdapter(horizontalListViewAdapter);
    }

    @Override
    protected void setListener() {
        ivPhotoButton.setOnClickListener(this);
        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起  
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                    //showToast("监听到软键盘弹起...");
                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                    //showToast("监听到软件盘关闭...");
                    llPicLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        etTitle.setOnClickListener(this);
        etContent.setOnClickListener(this);
        etTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                llPicLayout.setVisibility(View.GONE);
            }
        });
        etContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                llPicLayout.setVisibility(View.GONE);
            }
        });
    }

    //隐藏键盘
    private void hideSoftInput() {
        InputMethodManager inManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_sendpost_edit_photo:
                hideSoftInput();
                if (mListData.size() == 0) {
                    Intent intent = new Intent(this, MyAlbumActivity.class);
                    intent.putExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, false);
                    intent.putExtra(IntentKey.HAVE_SELECTED_COUNT, selectedCount);
                    startActivityForResult(intent, REQUEST_RESULT_SELECT_ALBUM);
                }
                break;
            case R.id.activity_sendpost_edit_title:
            case R.id.activity_sendpost_edit_content:
                llPicLayout.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 将要发表的图片
     */
    private List<MyImages> mListData = new ArrayList<MyImages>();
    private HorizontalListViewAdapter horizontalListViewAdapter;

    public class HorizontalListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            tvIndicate.setText(mListData.size() + "/6");
            return mListData.size() + 1;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(SendPostActivity.this, R.layout.item_sendpost_selectpic, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.sendpic_item_image);
            ImageView delete = (ImageView) view.findViewById(R.id.sendpic_item_image_delete);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
//            imageView.setLayoutParams(params);
            if (position < mListData.size()) {
                final MyImages image = mListData.get(position);
                ImageLoader.getInstance().displayImage("file://" + image.getPath(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //IntentUtil.toScannerActivity(AddPlanUnitActivity.this, listData, position);
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListData.remove(image);
                        notifyDataSetChanged();
                    }
                });
            } else {
                imageView.setImageResource(R.drawable.addpic_selector);
                delete.setVisibility(View.GONE);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SendPostActivity.this, MyAlbumActivity.class);
                        intent.putExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, false);
                        intent.putExtra(IntentKey.HAVE_SELECTED_COUNT, mListData.size());
                        SendPostActivity.this.startActivityForResult(intent, REQUEST_RESULT_SELECT_ALBUM);
                    }
                });
            }
            return view;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_RESULT_SELECT_ALBUM:
                if (data != null) {
                    List<MyImages> listData = data.getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
                    mListData.addAll(listData);
                    horizontalListViewAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
