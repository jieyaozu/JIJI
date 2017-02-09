package com.yaozu.object.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.adapter.PostDetailAdapter;
import com.yaozu.object.entity.MyImages;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.HorizontalListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/8.
 */

public class PostDetailActivity extends BaseActivity implements View.OnClickListener {
    private EditText etEditContent;
    private ActionBar mActionBar;
    private ImageView ivMore;
    private LinearLayout llMoreLayout;
    private HorizontalListView mHorizontalListView;
    private TextView tvIndicate;
    private TextView tvSelectCount;

    private final int REQUEST_RESULT_SELECT_ALBUM = 0;

    private boolean isCollection = false;
    private Button btSend;

    private ListView mListView;
    private View headerView;
    private PostDetailAdapter postDetailAdapter;
    private ImageView ivSupport;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_postdetail);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle("详情");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setShowHideAnimationEnabled(true);
        mActionBar = actionBar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.postdetail_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_collection:
                if (!isCollection) {
                    isCollection = true;
                    showToast("收藏成功");
                    item.setIcon(R.drawable.navigationbar_collect_highlighted);
                } else {
                    isCollection = false;
                    showToast("取消收藏成功");
                    item.setIcon(R.drawable.navigationbar_collect);
                }
                return true;
            case R.id.action_share:
                showToast("分享");
                return true;
            case R.id.action_sort:
                showToast("排序");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initView() {
        etEditContent = (EditText) findViewById(R.id.activity_postdetail_edit);
        ivMore = (ImageView) findViewById(R.id.activity_postdetail_more);
        btSend = (Button) findViewById(R.id.activity_postdetail_send);
        llMoreLayout = (LinearLayout) findViewById(R.id.activity_postdetail_edit_piclayout);
        mHorizontalListView = (HorizontalListView) findViewById(R.id.activity_postdetail_edit_hlistview);
        tvIndicate = (TextView) findViewById(R.id.activity_postdetail_edit_indicate);
        tvSelectCount = (TextView) findViewById(R.id.activity_postdetail_selectpic_count);

        mListView = (ListView) findViewById(R.id.activity_postdetail_listview);
        headerView = View.inflate(this, R.layout.header_listview_postdetail, null);
        initHeaderView(headerView);
        mListView.addHeaderView(headerView);
    }

    private void initHeaderView(View headerView) {
        ImageView userIcon = (ImageView) headerView.findViewById(R.id.item_listview_forum_usericon);
        ivSupport = (ImageView) headerView.findViewById(R.id.header_postdetail_support);
        Utils.setUserImg("", userIcon);
    }

    @Override
    protected void initData() {
        horizontalListViewAdapter = new HorizontalListViewAdapter();
        mHorizontalListView.setAdapter(horizontalListViewAdapter);

        postDetailAdapter = new PostDetailAdapter(this);
        mListView.setAdapter(postDetailAdapter);
    }

    @Override
    protected void setListener() {
        ivMore.setOnClickListener(this);
        etEditContent.setOnClickListener(this);
        ivSupport.setOnClickListener(this);
        etEditContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                llMoreLayout.setVisibility(View.GONE);
            }
        });
        etEditContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !TextUtils.isEmpty(s.toString().trim())) {
                    if (btSend.getVisibility() != View.VISIBLE) {
                        btSend.setVisibility(View.VISIBLE);
                        Animator set = getEnterAnimtor(btSend);
                        set.start();
                    }
                } else {
                    btSend.setVisibility(View.GONE);
                }
            }
        });
    }

    private AnimatorSet getEnterAnimtor(final View target) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X,
                0.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y,
                0.2f, 1f);

        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(100);
        enter.setInterpolator(new LinearInterpolator());// 线性变化
        enter.playTogether(scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }

    //隐藏键盘
    private void hideSoftInput() {
        InputMethodManager inManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_postdetail_more:
                hideSoftInput();
                llMoreLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llMoreLayout.setVisibility(View.VISIBLE);
                    }
                }, 100);
                break;
            case R.id.activity_postdetail_edit:
                llMoreLayout.setVisibility(View.GONE);
                break;
            case R.id.header_postdetail_support:
                showToast("点赞");
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
            int size = mListData.size();
            tvIndicate.setText(size + "/6");
            if (size > 0) {
                tvSelectCount.setVisibility(View.VISIBLE);
                tvSelectCount.setText(size + "");
            } else {
                tvSelectCount.setVisibility(View.GONE);
                tvSelectCount.setText(size + "");
            }
            return size + 1;
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
            View view = View.inflate(PostDetailActivity.this, R.layout.item_sendpost_selectpic, null);
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
                        Intent intent = new Intent(PostDetailActivity.this, MyAlbumActivity.class);
                        intent.putExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, false);
                        intent.putExtra(IntentKey.HAVE_SELECTED_COUNT, mListData.size());
                        PostDetailActivity.this.startActivityForResult(intent, REQUEST_RESULT_SELECT_ALBUM);
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
