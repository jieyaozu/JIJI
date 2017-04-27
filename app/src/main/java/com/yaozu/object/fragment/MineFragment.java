package com.yaozu.object.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.activity.setting.SettingActivity;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;

/**
 * Created by jxj42 on 2017/2/5.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener {
    public static String TAG = "MineFragment";
    private ImageView ivUsericon;
    private LinearLayout tvTheme, tvReplyPost, tvComment, tvCollect;
    private TextView tvUsername, tvUserid;

    private CardView cvUserinfo, setting;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTheme.setOnClickListener(this);
        tvReplyPost.setOnClickListener(this);
        tvComment.setOnClickListener(this);
        tvCollect.setOnClickListener(this);
        setting.setOnClickListener(this);
        ivUsericon.setOnClickListener(this);
        cvUserinfo.setOnClickListener(this);

        Utils.setUserImg(LoginInfo.getInstance(this.getActivity()).getIconPath(), ivUsericon);
        tvUsername.setText(LoginInfo.getInstance(this.getActivity()).getUserName());
        tvUserid.setText("呱呱号: " + LoginInfo.getInstance(this.getActivity()).getUserAccount());
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
        tvTheme = (LinearLayout) view.findViewById(R.id.fragment_mine_theme);
        tvReplyPost = (LinearLayout) view.findViewById(R.id.fragment_mine_replypost);
        tvComment = (LinearLayout) view.findViewById(R.id.fragment_mine_comment);
        tvCollect = (LinearLayout) view.findViewById(R.id.fragment_mine_collect);
        tvUsername = (TextView) view.findViewById(R.id.fragment_username);
        tvUserid = (TextView) view.findViewById(R.id.fragment_userid);
        cvUserinfo = (CardView) view.findViewById(R.id.cardview_userinfo);
        setting = (CardView) view.findViewById(R.id.mine_setting);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_mine_theme:
                IntentUtil.toThemePostActivity(this.getActivity(), LoginInfo.getInstance(this.getActivity()).getUserAccount());
                break;
            case R.id.fragment_mine_replypost:
                break;
            case R.id.fragment_mine_comment:
                break;
            case R.id.fragment_mine_collect:
                IntentUtil.toCollectActivity(this.getActivity());
                break;
            case R.id.fragment_usericon:

                break;
            case R.id.cardview_userinfo:
                IntentUtil.toUserInfoActivity(this.getActivity(), "", LoginInfo.getInstance(this.getActivity()).getUserAccount());
                break;
            case R.id.mine_setting:
                Intent setting = new Intent(this.getActivity(), SettingActivity.class);
                startActivity(setting);
                break;
        }
    }
}
