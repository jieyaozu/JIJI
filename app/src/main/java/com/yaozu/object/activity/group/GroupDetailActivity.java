package com.yaozu.object.activity.group;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.activity.BaseNoTitleActivity;
import com.yaozu.object.activity.CropImageActivity;
import com.yaozu.object.activity.MyAlbumActivity;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.db.dao.GroupDao;
import com.yaozu.object.entity.GroupBeanReqData;
import com.yaozu.object.entity.LoginInfo;
import com.yaozu.object.entity.RequestData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.listener.DownLoadIconListener;
import com.yaozu.object.listener.UploadListener;
import com.yaozu.object.utils.ACache;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.EncodingConvert;
import com.yaozu.object.utils.FileUtil;
import com.yaozu.object.utils.GroupPermission;
import com.yaozu.object.utils.IntentKey;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.NetUtil;
import com.yaozu.object.utils.ObjectBeanCache;
import com.yaozu.object.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/4/9.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class GroupDetailActivity extends BaseNoTitleActivity implements View.OnClickListener {
    private ImageView ivGroupIcon, ivEditIcon;
    private ImageView ivReturn;
    private RelativeLayout rlHeaderBackground;
    private ImageView ivGroupMenu;
    private GroupBean mGroupbean;
    private TextView tvGroupName, tvGroupIntroduce;
    private TextView tvGroupId, tvSection;
    private ACache aCache;
    private ObjectBeanCache objectBeanCache;
    private TextView tvPersonNum, tvPostNum, tvFansNum;//成员数，发贴数，粉丝数
    private TextView tvPersonCount;
    private TextView tvApplyEnter, tvAttention;
    private LinearLayout bottomLayout;
    private LinearLayout iconLayout;
    // 群名片Layout
    private RelativeLayout rlNicknameLayout;
    //群名片
    private TextView tvNickname;
    private PopupWindow popupwindow;
    private View rootView;

    private RelativeLayout rlLookMember;

    private static final int ACTIVITY_RESULT_GALRY = 0;
    private static final int ACTIVITY_RESULT_CROPIMAGE = 1;
    private FileUtil fileUtil = new FileUtil();
    public static String ICON_PATH = FileUtil.getSDPath() + File.separator + FileUtil.APP_FOLDER + File.separator + "icon.jpg";

    private GroupDao groupDao;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_group_detail);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void initView() {
        groupDao = new GroupDao(this);
        aCache = ACache.get(this);
        objectBeanCache = ObjectBeanCache.getInstance();
        rootView = findViewById(R.id.activity_group_detail);
        ivGroupIcon = (ImageView) findViewById(R.id.group_detail_icon);
        ivEditIcon = (ImageView) findViewById(R.id.group_detail_edit_icon);
        ivReturn = (ImageView) findViewById(R.id.actionbar_return);
        tvGroupName = (TextView) findViewById(R.id.group_detail_groupname);
        tvGroupId = (TextView) findViewById(R.id.group_detail_groupid);
        tvSection = (TextView) findViewById(R.id.group_detail_section);
        tvPersonNum = (TextView) findViewById(R.id.group_detail_personnum);
        tvPersonCount = (TextView) findViewById(R.id.group_detail_personcount);
        tvPostNum = (TextView) findViewById(R.id.group_detail_postnum);
        tvFansNum = (TextView) findViewById(R.id.group_detail_fansnum);
        tvGroupIntroduce = (TextView) findViewById(R.id.group_detail_groupintroduce);
        ivGroupMenu = (ImageView) findViewById(R.id.groupdetail_actionbar_menu);
        bottomLayout = (LinearLayout) findViewById(R.id.group_detail_bottom_layout);
        tvApplyEnter = (TextView) findViewById(R.id.group_detail_apply);
        tvAttention = (TextView) findViewById(R.id.group_detail_attention);
        rlLookMember = (RelativeLayout) findViewById(R.id.group_detail_lookmember);
        iconLayout = (LinearLayout) findViewById(R.id.group_detail_iconlayout);
        rlHeaderBackground = (RelativeLayout) findViewById(R.id.group_detail_header_background);
        rlNicknameLayout = (RelativeLayout) findViewById(R.id.group_detail_groupnickname_layout);
        tvNickname = (TextView) findViewById(R.id.group_detail_groupnickname);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xiaoma);
        rlHeaderBackground.setBackground(new BitmapDrawable(getResources(), Utils.blur(this, bitmap)));
    }

    @Override
    protected void initData() {
        mGroupbean = (GroupBean) getIntent().getSerializableExtra(IntentKey.INTENT_GROUP);
        tvGroupName.setText(mGroupbean.getGroupname());
        tvGroupId.setText(mGroupbean.getGroupid());
        if (!TextUtils.isEmpty(mGroupbean.getGroupicon())) {
            ImageLoader.getInstance().displayImage(mGroupbean.getGroupicon(), ivGroupIcon);
            downloadBackgroundImage(mGroupbean.getGroupicon());
        }

        Object object = objectBeanCache.getObject(mGroupbean.getGroupid());
        if (object == null) {
            requestGroupDetail(mGroupbean.getGroupid());
        } else {
            mGroupbean = (GroupBean) object;
            bindDataToView((GroupBean) object);
        }

        if (GroupPermission.isMyCreatGroupid(groupDao, mGroupbean.getGroupid())) {
            ivEditIcon.setVisibility(View.VISIBLE);
        } else {
            ivEditIcon.setVisibility(View.GONE);
        }
    }

    /**
     * 9
     * 查找群的详情
     */
    private void requestGroupDetail(final String groupid) {
        String url = DataInterface.FIND_GROUP_BY_ID + "groupid=" + groupid + "&userid=" + LoginInfo.getInstance(this).getUserAccount();
        RequestManager.getInstance().getRequest(this, url, GroupBeanReqData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    GroupBeanReqData groupBeanReqData = (GroupBeanReqData) object;
                    GroupBean groupBean = groupBeanReqData.getBody().getGroupbean();
                    mGroupbean = groupBean;
                    objectBeanCache.addCacheObject(groupid, groupBean);
                    bindDataToView(groupBean);
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    /**
     * 填充数据
     *
     * @param groupBean
     */
    private void bindDataToView(GroupBean groupBean) {
        ImageLoader.getInstance().displayImage(groupBean.getGroupicon(), ivGroupIcon);
        tvGroupName.setText(groupBean.getGroupname());
        tvGroupId.setText(groupBean.getGroupid());
        tvSection.setText(groupBean.getSectionname());
        tvGroupIntroduce.setText(groupBean.getIntroduce());
        tvPersonNum.setText(groupBean.getPnumber());
        tvPersonCount.setText(groupBean.getPnumber() + "名成员");
        tvPostNum.setText(groupBean.getMptnumber());
        tvNickname.setText(TextUtils.isEmpty(groupBean.getNickname()) ? "未设置" : groupBean.getNickname());

        //成员头像
        List<String> iconList = groupBean.getMembericonlist();
        if (iconList != null) {
            iconLayout.removeViews(1, iconLayout.getChildCount() - 1);
            for (String icon : iconList) {
                ImageView imageView = (ImageView) View.inflate(this, R.layout.item_member_usericon, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.dimen_45), getResources().getDimensionPixelSize(R.dimen.dimen_45));
                layoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.dimen_5);
                imageView.setLayoutParams(layoutParams);
                Utils.setUserImg(icon, imageView);
                iconLayout.addView(imageView);
            }
        }
        //是否是群成员
        if (GroupPermission.isGroupMember(groupDao, groupBean.getGroupid())) {
            bottomLayout.setVisibility(View.GONE);
            ivGroupMenu.setVisibility(View.VISIBLE);
        } else {
            bottomLayout.setVisibility(View.VISIBLE);
            ivGroupMenu.setVisibility(View.GONE);
        }

        downloadBackgroundImage(groupBean.getGroupicon());
    }

    private void downloadBackgroundImage(final String iconUrl) {
        Bitmap bitmap = aCache.getAsBitmap(iconUrl);
        if (bitmap != null) {
            rlHeaderBackground.setBackground(new BitmapDrawable(getResources(), Utils.blur(this, bitmap)));
            return;
        }
        NetUtil.downLoadBitmap(iconUrl, new DownLoadIconListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void downLoadSuccess(Bitmap bitmap) {
                aCache.put(iconUrl, bitmap);
                rlHeaderBackground.setBackground(new BitmapDrawable(getResources(), Utils.blur(GroupDetailActivity.this, bitmap)));
            }

            @Override
            public void downLoadFailed() {

            }
        });
    }

    @Override
    protected void setListener() {
        ivReturn.setOnClickListener(this);
        ivGroupMenu.setOnClickListener(this);
        ivEditIcon.setOnClickListener(this);
        tvSection.setOnClickListener(this);
        tvApplyEnter.setOnClickListener(this);
        tvAttention.setOnClickListener(this);
        rlLookMember.setOnClickListener(this);
        rlNicknameLayout.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.IS_EDITGROUP_SUCCESS) {
            Constant.IS_EDITGROUP_SUCCESS = false;
            objectBeanCache.cleanCache();
            requestGroupDetail(mGroupbean.getGroupid());
        }
    }

    /**
     * 打开相册选取图片
     */
    public void getimgefromegalry() {
        Intent intent = new Intent(this, MyAlbumActivity.class);
        intent.putExtra(IntentKey.INTENT_SELECT_ALBUM_SINGLE, true);
        this.startActivityForResult(intent, ACTIVITY_RESULT_GALRY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_RESULT_GALRY:
                if (data == null) {
                    return;
                }
                List<MyImage> listData = data.getParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES);
                if (listData == null || listData.size() <= 0) {
                    return;
                }
                String path = listData.get(0).getPath();
                final Bitmap bm = FileUtil.compressUserIcon(1200, path);
                Intent cropimage = new Intent(this, CropImageActivity.class);
                IntentKey.cropBitmap = bm;
                startActivityForResult(cropimage, ACTIVITY_RESULT_CROPIMAGE);
                break;
            case ACTIVITY_RESULT_CROPIMAGE:
                if (IntentKey.cropBitmap == null) {
                    return;
                }
                //保存到本地
                FileUtil.saveOutput(IntentKey.cropBitmap, ICON_PATH);
                //上传头像到服务器上
                //NetUtil.uploadUserIconFile(this, new File(ICON_PATH), new MyUploadListener());
                MyImage image = new MyImage();
                image.setPath(ICON_PATH);
                image.setDisplayName(generateDisplayName("icon"));
                List<MyImage> imageList = new ArrayList<>();
                imageList.add(image);
                NetUtil.uploadGroupImagesToServer(this, imageList, mGroupbean.getGroupid(), new UploadListener() {
                    @Override
                    public void uploadSuccess(String jsonstring) {
                        System.out.println();
                        Bitmap bitmap = BitmapFactory.decodeFile(ICON_PATH);
                        if (bitmap != null) {
                            objectBeanCache.cleanCache();
                            ivGroupIcon.setImageBitmap(bitmap);
                            rlHeaderBackground.setBackground(new BitmapDrawable(getResources(), Utils.blur(GroupDetailActivity.this, bitmap)));
                        }
                    }

                    @Override
                    public void uploadFailed() {
                        Toast.makeText(GroupDetailActivity.this, "图片发布失败，请重新发送", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    /**
     * 重新生成图片的文件名
     *
     * @param originName
     * @return
     */
    private String generateDisplayName(String originName) {
        String displayName = null;
        originName = (System.currentTimeMillis() % 1000) + "_" + originName;//保证唯一
        if (EncodingConvert.isContainsChinese(originName)) {
            originName = originName.hashCode() + ".jpg";
        } else {
            int index = originName.lastIndexOf(".");
            String suffix = "";
            if (index > 0) {
                suffix = originName.substring(index, originName.length());
            }
            originName = "guagua_" + EncodingConvert.getRandomString(4) + "_" + System.currentTimeMillis() + suffix;
        }
        displayName = originName;
        return displayName;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_return:
                finish();
                break;
            case R.id.groupdetail_actionbar_menu:
                showPopupMenuView();
                break;
            case R.id.group_detail_edit_icon:
                getimgefromegalry();
                break;
            case R.id.group_detail_section:
                break;
            case R.id.group_detail_apply:
                IntentUtil.toApplyEnterGroupActivity(this, mGroupbean.getGroupid());
                break;
            case R.id.group_detail_attention:
                break;
            case R.id.group_detail_lookmember:
                IntentUtil.toGroupMembersActivity(this, mGroupbean.getGroupid());
                break;
            case R.id.group_detail_groupnickname_layout:
                IntentUtil.toEditNicknameActivity(this, mGroupbean.getGroupid(), mGroupbean.getNickname());
                break;
        }
    }

    /**
     * @一级菜单
     */
    private void showPopupMenuView() {
        View contentview = View.inflate(this, R.layout.popup_groupdetail_menu, null);
        //编辑
        TextView tvEdit = (TextView) contentview.findViewById(R.id.popupwindow_groupdetail_menu_edit);
        //退出群
        TextView tvExit = (TextView) contentview.findViewById(R.id.popupwindow_groupdetail_menu_exit);
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toEditGroupActivity(GroupDetailActivity.this, mGroupbean);
                popupwindow.dismiss();
            }
        });

        if (GroupPermission.isMyCreatGroupid(groupDao, mGroupbean.getGroupid())) {
            tvEdit.setVisibility(View.VISIBLE);
            tvExit.setVisibility(View.GONE);
        } else {
            tvEdit.setVisibility(View.GONE);
            tvExit.setVisibility(View.VISIBLE);
        }

        tvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupwindow.dismiss();
                new MaterialDialog.Builder(GroupDetailActivity.this)
                        .backgroundColorRes(R.color.colorWhite)
                        .content("确定要退出此群吗?")
                        .contentColorRes(R.color.nomralblack)
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                requestExitGroup(LoginInfo.getInstance(GroupDetailActivity.this).getUserAccount(), mGroupbean.getGroupid());
                            }
                        }).show();
            }
        });

        popupwindow = new PopupWindow(contentview, this.getResources().getDimensionPixelSize(R.dimen.detail_menu_width), LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置消失动画
        popupwindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupwindow.setFocusable(true);
        popupwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int[] location = new int[2];
        rootView.getLocationInWindow(location);
        popupwindow.showAtLocation(rootView, Gravity.TOP | Gravity.RIGHT, 10, 0);

        AlphaAnimation scaleAt = new AlphaAnimation(0, 1);
        scaleAt.setDuration(200);
        scaleAt.setFillEnabled(true);
        scaleAt.setInterpolator(new DecelerateInterpolator());
        contentview.startAnimation(scaleAt);
    }

    /**
     * 退出群
     *
     * @param userid
     * @param groupid
     */
    private void requestExitGroup(String userid, final String groupid) {
        showBaseProgressDialog("退出中...");
        String url = DataInterface.EXIT_GROUP + "userid=" + userid + "&groupid=" + groupid;
        RequestManager.getInstance().getRequest(this, url, RequestData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                closeBaseProgressDialog();
                if (object != null) {
                    RequestData requestData = (RequestData) object;
                    if (Constant.SUCCESS.equals(requestData.getBody().getCode())) {
                        groupDao.deleteGroupById(groupid);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {
                closeBaseProgressDialog();
            }
        });
    }
}
