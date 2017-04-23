package com.yaozu.object.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.adapter.ImageListAdapter;
import com.yaozu.object.bean.Comment;
import com.yaozu.object.bean.Post;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.ParamList;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/14.
 */

public class PostReplyDetailActivity extends BaseActivity implements View.OnClickListener {
    private Post mPost;
    private int index;
    private String mainUserid;//楼主的userid
    private ImageView ivUserIcon;
    private TextView tvUserName;
    private TextView tvIsMain;
    private TextView tvLayerIndex;
    private TextView tvTime;
    private TextView tvContent;

    private NoScrollListView nlCommentListview;
    private CommentAdapter commentAdapter;
    private NoScrollListView nlImageViews;
    private ImageListAdapter imageListAdapter;

    private EditText etEditContent;
    private Button btSend;
    private String replyuserid = "";
    private ScrollView scrollView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_postreply_detail);
    }

    @Override
    protected void settingActionBar(ActionBar actionBar) {
        actionBar.setTitle(index + "楼");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initView() {
        mPost = (Post) getIntent().getSerializableExtra(IntentKey.INTENT_POST);
        index = getIntent().getIntExtra(IntentKey.INTENT_POST_POSITION, 0);
        mainUserid = getIntent().getStringExtra(IntentKey.INTENT_USERID);

        scrollView = (ScrollView) findViewById(R.id.activity_postreply_scrollview);
        ivUserIcon = (ImageView) findViewById(R.id.item_listview_replypost_usericon);
        tvUserName = (TextView) findViewById(R.id.item_listview_replypost_username);
        tvIsMain = (TextView) findViewById(R.id.item_listview_replypost_ismain);
        tvLayerIndex = (TextView) findViewById(R.id.item_listview_replypost_layerindex);
        tvTime = (TextView) findViewById(R.id.item_listview_replypost_time);
        tvContent = (TextView) findViewById(R.id.item_listview_replypost_content);
        etEditContent = (EditText) findViewById(R.id.activity_postdetail_edit);
        btSend = (Button) findViewById(R.id.activity_postdetail_send);
        nlCommentListview = (NoScrollListView) findViewById(R.id.item_listview_replypost_comments);
        nlImageViews = (NoScrollListView) findViewById(R.id.item_listview_replypost_container);
        imageListAdapter = new ImageListAdapter(this);
        nlImageViews.setAdapter(imageListAdapter);

        if (mPost.getUserid().equals(mainUserid)) {
            tvIsMain.setVisibility(View.VISIBLE);
        } else {
            tvIsMain.setVisibility(View.GONE);
        }

        commentAdapter = new CommentAdapter();
        commentAdapter.setDataList(mPost.getComments());
        nlCommentListview.setAdapter(commentAdapter);
    }

    @Override
    protected void initData() {
        Utils.setUserImg(mPost.getUserIcon(), ivUserIcon);
        tvUserName.setText(mPost.getUserName());
        tvLayerIndex.setText(index + "楼");
        tvTime.setText(DateUtil.getRelativeTime(mPost.getCreatetime()));
        tvContent.setText(mPost.getContent());

        imageListAdapter.setData(mPost.getImages());
        etEditContent.setHint("回复 " + mPost.getUserName());

        //把软键盘打开
        //TODO
    }

    @Override
    protected void setListener() {
        btSend.setOnClickListener(this);
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
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        if (etEditContent.hasFocus()) {
                            etEditContent.clearFocus();
                            hideSoftInput();
                        }
                        break;
                }
                return false;
            }
        });
        ivUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toUserInfoActivity(PostReplyDetailActivity.this, "", mPost.getUserid());
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
            case R.id.activity_postdetail_send:
                if (!LoginInfo.getInstance(this).isLogining()) {
                    IntentUtil.toLoginActivity(this);
                    return;
                }
                String content = etEditContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                replyPostRequest(content, mPost.getPostid(), replyuserid);
                break;
        }
    }

    private void replyPostRequest(String content, final String postid, String replyUserid) {
        showBaseProgressDialog("发送中...");
        String url = DataInterface.ADD_POSTREPLY_COMMENT;
        ParamList parameters = new ParamList();
        String commentid = (System.currentTimeMillis() + LoginInfo.getInstance(this).getUserAccount()).hashCode() + "";
        parameters.add(new ParamList.Parameter("postid", postid));
        parameters.add(new ParamList.Parameter("commentid", commentid));
        parameters.add(new ParamList.Parameter("userid", LoginInfo.getInstance(this).getUserAccount()));
        parameters.add(new ParamList.Parameter("replyuserid", replyUserid));
        parameters.add(new ParamList.Parameter("publictime", DateUtil.generateDateOfTime(System.currentTimeMillis())));
        parameters.add(new ParamList.Parameter("content", content));
        parameters.add(new ParamList.Parameter("unread", "1"));

        RequestManager.getInstance().postRequest(this, url, parameters, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        closeBaseProgressDialog();
                        etEditContent.setText("");
                        hideSoftInput();
                        //发表完以后刷新一下
                        findReplypostRequest(postid);
                    } else {
                        showToast(requestData.getBody().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {
                closeBaseProgressDialog();
            }
        });
    }

    private void findReplypostRequest(String postid) {
        String url = DataInterface.FIND_REPLY_POST + "postid=" + postid;
        RequestManager.getInstance().getRequest(this, url, Post.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    Post post = (Post) object;
                    commentAdapter.setDataList(post.getComments());
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    //隐藏键盘
    private void hideSoftInput() {
        replyuserid = "";
        etEditContent.setHint("回复 " + mPost.getUserName());
        InputMethodManager inManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    public class CommentAdapter extends BaseAdapter {
        private List<Comment> commentList = new ArrayList<>();

        public void setDataList(List<Comment> comments) {
            if (comments != null) {
                commentList.clear();
                commentList.addAll(comments);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return commentList.size();
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
            View view = View.inflate(PostReplyDetailActivity.this, R.layout.item_comment_listview, null);
            TextView tvContent = (TextView) view.findViewById(R.id.item_comment_content);
            final Comment comment = commentList.get(position);
            Spannable spannable = getSpanned(comment, mainUserid);
            tvContent.setText(spannable);
            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
            tvContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNameClick) {
                        isNameClick = false;
                        return;
                    }
                    etEditContent.requestFocus();
                    if (!LoginInfo.getInstance(PostReplyDetailActivity.this).getUserAccount().equals(comment.getUserid())) {
                        replyuserid = comment.getUserid();
                        etEditContent.setHint("回复 " + comment.getUserName());
                    } else {
                        replyuserid = "";
                        etEditContent.setHint("回复 " + mPost.getUserName());
                    }
                    InputMethodManager imm = (InputMethodManager) etEditContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(etEditContent, InputMethodManager.SHOW_FORCED);
                }
            });
            return view;
        }
    }

    private Spannable getSpanned(Comment comment, String mainUserid) {
        String contentstr = null;//userId + ":" + content;
        String userName = comment.getUserName();
        String replyName = comment.getReplyUserName();
        String content = comment.getContent();
        boolean isreply = false;
        boolean hasMain = false;
        if (TextUtils.isEmpty(replyName)) {
            isreply = false;
            contentstr = userName + " : " + content;
            if (comment.getUserid().equals(mainUserid)) {
                hasMain = true;
                contentstr = userName + "楼主" + " : " + content;
            }
        } else {
            isreply = true;
            contentstr = userName + " 回复 " + replyName + " : " + content;
            if (comment.getUserid().equals(mainUserid)) {
                hasMain = true;
                contentstr = userName + "楼主" + " 回复 " + replyName + " : " + content;
            } else if (comment.getReplyUserid().equals(mainUserid)) {
                hasMain = true;
                contentstr = userName + " 回复 " + replyName + "楼主" + " : " + content;
            }
        }
        Spannable spannable = new SpannableString(contentstr);
        if (isreply) {
            ForegroundColorSpan huifucolor = new ForegroundColorSpan(getResources().getColor(R.color.top_blue));
            spannable.setSpan(huifucolor, contentstr.indexOf("回复"), contentstr.indexOf("回复") + "回复".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        View.OnClickListener nameclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = (String) v.getTag();
                IntentUtil.toUserInfoActivity(PostReplyDetailActivity.this, "", userid);
            }
        };
        spannable.setSpan(new Clickable(nameclick, comment.getUserid()), 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new Clickable(nameclick, comment.getReplyUserid()), contentstr.indexOf(replyName), contentstr.indexOf(replyName) + replyName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //图片
        if (hasMain) {
            Drawable drawable = getResources().getDrawable(R.drawable.author);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            spannable.setSpan(new ImageSpan(drawable), contentstr.indexOf("楼主"), contentstr.indexOf("楼主") + "楼主".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    private boolean isNameClick = false;

    /**
     * 内部类，用于截获点击富文本后的事件
     */
    class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;
        private String userId;

        public Clickable(View.OnClickListener mListener, String userid) {
            this.mListener = mListener;
            this.userId = userid;
        }

        @Override
        public void onClick(View v) {
            isNameClick = true;
            v.setTag(userId);
            mListener.onClick(v);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.gray_white));
            ds.setUnderlineText(false);    //去除超链接的下划线
        }
    }
}
