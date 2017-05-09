package com.yaozu.object.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.yaozu.object.R;
import com.yaozu.object.activity.CollectActivity;
import com.yaozu.object.activity.CommentToMeActivity;
import com.yaozu.object.activity.CreateGroupActivity;
import com.yaozu.object.activity.PostDetailActivity;
import com.yaozu.object.activity.PostReplyDetailActivity;
import com.yaozu.object.activity.ReplyToMeActivity;
import com.yaozu.object.activity.ScannerPictureActivity;
import com.yaozu.object.activity.SendPostActivity;
import com.yaozu.object.activity.ThemePostActivity;
import com.yaozu.object.activity.UserReplyPostActivity;
import com.yaozu.object.activity.UserThemePostActivity;
import com.yaozu.object.activity.WebViewActivity;
import com.yaozu.object.activity.group.ApplyEnterGroupActivity;
import com.yaozu.object.activity.group.EditGroupActivity;
import com.yaozu.object.activity.group.EditNicknameActivity;
import com.yaozu.object.activity.group.GroupDetailActivity;
import com.yaozu.object.activity.group.GroupMembersActivity;
import com.yaozu.object.activity.group.GroupMessageActivity;
import com.yaozu.object.activity.group.GroupOfPostActivity;
import com.yaozu.object.activity.group.GroupSearchActivity;
import com.yaozu.object.activity.group.SectionGroupActivity;
import com.yaozu.object.activity.setting.UserSettingActivity;
import com.yaozu.object.activity.user.LoginActivity;
import com.yaozu.object.activity.user.RegisterActivity;
import com.yaozu.object.activity.user.UserIconDetail;
import com.yaozu.object.activity.user.UserInfoActivity;
import com.yaozu.object.bean.GroupBean;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.bean.Post;

import java.util.ArrayList;

/**
 * Created by jxj42 on 2016/12/22.
 */

public class IntentUtil {

    private static void overridePendingTransition(Context context) {
        ((FragmentActivity) context).overridePendingTransition(R.anim.right_enter_page, R.anim.right_quit_page);
    }

    /**
     * 跳到查看用户头像页面
     *
     * @param context
     */
    public static void toUserIconActivity(Context context, String userid, String iconpath) {
        Intent intent = new Intent(context, UserIconDetail.class);
        intent.putExtra(IntentKey.INTENT_USERID, userid);
        intent.putExtra(IntentKey.USER_ICON_PATH, iconpath);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.usericon_scale_enter, R.anim.usericon_enter_quit_page);
    }

    public static void toScannerPictureActivity(Context context, ArrayList<MyImage> imagesList, int position) {
        Intent intent = new Intent(context, ScannerPictureActivity.class);
        intent.putParcelableArrayListExtra(IntentKey.INTENT_ALBUM_IMAGES, imagesList);
        intent.putExtra(IntentKey.INTENT_ALBUM_IMAGES_INDEX, position);
        context.startActivity(intent);
    }

    public static void toSendPostActivity(Context context, String groupid) {
        Intent sendPost = new Intent(context, SendPostActivity.class);
        sendPost.putExtra(IntentKey.INTENT_IS_EDIT_POST, false);
        sendPost.putExtra(IntentKey.INTENT_GROUP_ID, groupid);
        context.startActivity(sendPost);
        overridePendingTransition(context);
    }

    public static void toEditSendPostActivity(Context context, Post post) {
        Intent sendPost = new Intent(context, SendPostActivity.class);
        sendPost.putExtra(IntentKey.INTENT_POST, post);
        sendPost.putExtra(IntentKey.INTENT_IS_EDIT_POST, true);
        context.startActivity(sendPost);
        overridePendingTransition(context);
    }

    public static void toPostDetailActivity(Context context, Post post) {
        Intent postDetail = new Intent(context, PostDetailActivity.class);
        postDetail.putExtra(IntentKey.INTENT_POST, post);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }

    public static void toPostDetailActivity(Context context, String postid) {
        Intent postDetail = new Intent(context, PostDetailActivity.class);
        postDetail.putExtra(IntentKey.INTENT_POST_ID, postid);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }

    /**
     * @param context
     * @param post
     * @param mainuserid 楼主的用户id
     * @param index
     */
    public static void toPostReplyDetailActivity(Context context, Post post, String mainuserid, int index) {
        Intent postDetail = new Intent(context, PostReplyDetailActivity.class);
        postDetail.putExtra(IntentKey.INTENT_POST, post);
        postDetail.putExtra(IntentKey.INTENT_POST_POSITION, index);
        postDetail.putExtra(IntentKey.INTENT_USERID, mainuserid);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }

    /**
     * @param context
     * @param postid          贴子的ID
     * @param comefromcomment 是否是从评论页面跳转过来的
     */
    public static void toPostReplyDetailActivity(Context context, String postid, boolean comefromcomment) {
        Intent postDetail = new Intent(context, PostReplyDetailActivity.class);
        postDetail.putExtra(IntentKey.INTENT_POST_ID, postid);
        postDetail.putExtra(IntentKey.INTENT_IS_COMEFROM_COMMENT, comefromcomment);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }

    public static void toLoginActivity(Context context) {
        Intent postDetail = new Intent(context, LoginActivity.class);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }

    public static void toRegisterActivity(Context context) {
        Intent postDetail = new Intent(context, RegisterActivity.class);
        context.startActivity(postDetail);
        overridePendingTransition(context);
    }

    public static void toUserInfoActivity(Context context, String groupid, String userid) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        intent.putExtra(IntentKey.INTENT_GROUP_ID, groupid);
        intent.putExtra(IntentKey.INTENT_USERID, userid);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    public static void toCollectActivity(Context context) {
        Intent intent = new Intent(context, CollectActivity.class);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    public static void toThemePostActivity(Context context, String userid) {
        Intent intent = new Intent(context, ThemePostActivity.class);
        intent.putExtra(IntentKey.INTENT_USERID, userid);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    public static void toWebViewActivity(Context context, String postid) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(IntentKey.INTENT_POST_ID, postid);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    /**
     * 创建群
     *
     * @param context
     */
    public static void toCreatGroupActivity(Context context) {
        Intent intent = new Intent(context, CreateGroupActivity.class);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    /**
     * 群内帖子页面
     *
     * @param context
     * @param groupBean
     */
    public static void toGroupOfPostActivity(Context context, GroupBean groupBean) {
        Intent intent = new Intent(context, GroupOfPostActivity.class);
        intent.putExtra(IntentKey.INTENT_GROUP, groupBean);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    /**
     * 群详情页面
     *
     * @param context
     * @param groupBean
     */
    public static void toGroupDetailActivity(Context context, GroupBean groupBean) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra(IntentKey.INTENT_GROUP, groupBean);
        context.startActivity(intent);
    }

    /**
     * 群详情页面
     *
     * @param context
     * @param groupBean
     */
    public static void toEditGroupActivity(Context context, GroupBean groupBean) {
        Intent intent = new Intent(context, EditGroupActivity.class);
        intent.putExtra(IntentKey.INTENT_GROUP, groupBean);
        context.startActivity(intent);
    }

    /**
     * 查找版块下面的群
     *
     * @param context
     * @param sectionid
     */
    public static void toSectionGroupActivity(Context context, String sectionid, String sectionname) {
        Intent intent = new Intent(context, SectionGroupActivity.class);
        intent.putExtra(IntentKey.INTENT_SECTION_ID, sectionid);
        intent.putExtra(IntentKey.INTENT_SECTION_NAME, sectionname);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    /**
     * 申请加入群
     *
     * @param context
     * @param groupid
     */
    public static void toApplyEnterGroupActivity(Context context, String groupid) {
        Intent intent = new Intent(context, ApplyEnterGroupActivity.class);
        intent.putExtra(IntentKey.INTENT_GROUP_ID, groupid);
        context.startActivity(intent);
    }

    /**
     * 群搜索页面
     *
     * @param context
     */
    public static void toGroupSearchActivity(Context context) {
        Intent intent = new Intent(context, GroupSearchActivity.class);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    /**
     * 群消息页面
     *
     * @param context
     */
    public static void toGroupMessageActivity(Context context) {
        Intent intent = new Intent(context, GroupMessageActivity.class);
        context.startActivity(intent);
        overridePendingTransition(context);
    }

    /**
     * 群成员列表页面
     *
     * @param context
     * @param groupid
     */
    public static void toGroupMembersActivity(Context context, String groupid) {
        Intent intent = new Intent(context, GroupMembersActivity.class);
        intent.putExtra(IntentKey.INTENT_GROUP_ID, groupid);
        context.startActivity(intent);
    }

    /**
     * 编辑群名片
     *
     * @param context
     * @param nickname
     */
    public static void toEditNicknameActivity(Context context, String groupid, String nickname) {
        Intent intent = new Intent(context, EditNicknameActivity.class);
        intent.putExtra(IntentKey.INTENT_GROUP_ID, groupid);
        intent.putExtra(IntentKey.INTENT_NICKNAME, nickname);
        context.startActivity(intent);
    }

    /**
     * 去用户贴子列表页面
     *
     * @param context
     * @param userid
     * @param username
     */
    public static void toUserThemePostActivity(Context context, String userid, String username) {
        Intent intent = new Intent(context, UserThemePostActivity.class);
        intent.putExtra(IntentKey.INTENT_USERID, userid);
        intent.putExtra(IntentKey.INTENT_USERNAME, username);
        context.startActivity(intent);
    }

    /**
     * 去用户回复的帖子列表页面
     *
     * @param context
     * @param userid
     * @param username
     */
    public static void toUserReplyPostActivity(Context context, String userid, String username) {
        Intent intent = new Intent(context, UserReplyPostActivity.class);
        intent.putExtra(IntentKey.INTENT_USERID, userid);
        intent.putExtra(IntentKey.INTENT_USERNAME, username);
        context.startActivity(intent);
    }

    /**
     * 用户个人设置页面
     *
     * @param context
     */
    public static void toUserSettingActivity(Context context) {
        Intent intent = new Intent(context, UserSettingActivity.class);
        context.startActivity(intent);
    }

    /**
     * 去回复我的页面
     *
     * @param context activity对象
     */
    public static void toReplyToMeActivity(Context context) {
        Intent intent = new Intent(context, ReplyToMeActivity.class);
        context.startActivity(intent);
    }

    /**
     * 去评论我的页面
     *
     * @param context activity对象
     */
    public static void toCommentToMeActivity(Context context) {
        Intent intent = new Intent(context, CommentToMeActivity.class);
        context.startActivity(intent);
    }
}
