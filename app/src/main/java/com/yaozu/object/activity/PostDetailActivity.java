package com.yaozu.object.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.yaozu.object.R;

/**
 * Created by jxj42 on 2017/2/8.
 */

public class PostDetailActivity extends BaseActivity implements View.OnClickListener {
    private EditText etEditContent;
    private ActionBar mActionBar;
    private ImageView ivMore;

    private boolean isCollection = false;
    private Button btSend;

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
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setListener() {
        ivMore.setOnClickListener(this);
        etEditContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && !TextUtils.isEmpty(s.toString())) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_postdetail_more:

                break;
        }
    }
}
