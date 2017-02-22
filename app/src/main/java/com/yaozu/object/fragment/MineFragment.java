package com.yaozu.object.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;

/**
 * Created by jxj42 on 2017/2/5.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener {
    public static String TAG = "MineFragment";
    private ImageView ivUsericon;
    private TextView tvTheme, tvReplyPost, tvComment, tvCollect;
    private TextView tvUsername;

    private CardView cardView2;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTheme.setOnClickListener(this);
        tvTheme.setTypeface(typeface);
        tvReplyPost.setOnClickListener(this);
        tvReplyPost.setTypeface(typeface);
        tvComment.setOnClickListener(this);
        tvComment.setTypeface(typeface);
        tvCollect.setOnClickListener(this);
        tvCollect.setTypeface(typeface);
        tvUsername.setTypeface(typeface);

        cardView2.setOnClickListener(this);
        ivUsericon.setOnClickListener(this);

        Utils.setUserImg(LoginInfo.getInstance(this.getActivity()).getIconPath(), ivUsericon);
        tvUsername.setText(LoginInfo.getInstance(this.getActivity()).getUserName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ivUsericon = (ImageView) view.findViewById(R.id.fragment_usericon);
        tvTheme = (TextView) view.findViewById(R.id.fragment_mine_theme);
        tvReplyPost = (TextView) view.findViewById(R.id.fragment_mine_replypost);
        tvComment = (TextView) view.findViewById(R.id.fragment_mine_comment);
        tvCollect = (TextView) view.findViewById(R.id.fragment_mine_collect);
        tvUsername = (TextView) view.findViewById(R.id.fragment_username);
        cardView2 = (CardView) view.findViewById(R.id.cardview2);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_mine_theme:
                break;
            case R.id.fragment_mine_replypost:
                break;
            case R.id.fragment_mine_comment:
                break;
            case R.id.fragment_mine_collect:
                break;
            case R.id.fragment_usericon:
                IntentUtil.toUserInfoActivity(this.getActivity(), LoginInfo.getInstance(this.getActivity()).getUserAccount());
                break;
        }
    }
}
