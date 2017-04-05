package com.yaozu.object.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaozu.object.R;
import com.yaozu.object.activity.user.UserInfoActivity;
import com.yaozu.object.bean.MyImage;
import com.yaozu.object.bean.Post;
import com.yaozu.object.entity.DetailReplyPostListInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.NoScrollGridView;
import com.yaozu.object.widget.NoScrollListView;
import com.yaozu.object.widget.StickyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/25.
 */

public class ReplyFragment extends BaseFragment {
    public static String TAG = "ThemeFragment";
    public StickyListView listView;
    public ListReplyAdapter themeAdapter;
    private UserInfoActivity.StickyScrollCallBack scrollListener;
    private List<Post> postList = new ArrayList<>();
    private String userid;
    private int currentPage = 1;
    private int itemWidth;

    public static ReplyFragment newInstance(String userid) {
        ReplyFragment fragment = new ReplyFragment();
        fragment.setUserid(userid);
        return fragment;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        themeAdapter = new ListReplyAdapter();
        listView.setAdapter(themeAdapter);

        listView.setOnLoadListener(new StickyListView.OnLoadListener() {
            @Override
            public void onLoad() {
                currentPage++;
                requestData(userid, currentPage);
            }
        });
        int screenWidth = Utils.getScreenWidth(this.getActivity());
        itemWidth = (screenWidth - this.getActivity().getResources().getDimensionPixelSize(R.dimen.forum_item_margin) * 2) / 3;
        requestData(userid, currentPage);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme, container, false);
        listView = (StickyListView) view.findViewById(R.id.userinfo_stickylistview);
        listView.setScrollCallBack(scrollListener);
        View viewspace = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UserInfoActivity.STICKY_HEIGHT2);
        viewspace.setLayoutParams(params);
        listView.addHeaderView(viewspace);
        listView.invalidScroll();
        return view;
    }

    public int getStickyHeight() {
        int scrollHeight = listView.getFirstViewScrollTop();
        if (scrollHeight > UserInfoActivity.STICKY_HEIGHT1) {
            return UserInfoActivity.STICKY_HEIGHT1;
        }
        return scrollHeight;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setStickyH(int stickyH) {
        if (Math.abs(stickyH - getStickyHeight()) < 5) {
            return;
        }
        listView.setSelectionFromTop(0, -stickyH);
    }

    public void setScrollCallBack(UserInfoActivity.StickyScrollCallBack scrollListener) {
        this.scrollListener = scrollListener;
    }

    @Override
    protected void onIRefresh() {
        super.onIRefresh();
    }

    @Override
    protected void onILoad() {
        super.onILoad();
    }

    private void requestData(String userid, int page) {
        String url = DataInterface.FIND_USER_REPLYPOST_LIST + "userid=" + userid + "&pageindex=" + page;
        RequestManager.getInstance().getRequest(this.getActivity(), url, DetailReplyPostListInfo.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                listView.setLoading(false);
                if (object != null) {
                    DetailReplyPostListInfo homeForumDataInfo = (DetailReplyPostListInfo) object;
                    List<Post> listData = homeForumDataInfo.getBody().getPostlist();
                    if (listData != null) {
                        postList.addAll(listData);
                        themeAdapter.notifyDataSetChanged();
                        if (listData.size() < 15) {
                            listView.setCanLoad(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int code, String message) {
                listView.setLoading(false);
            }
        });
    }

    class ListReplyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return postList.size();
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
            View view = null;
            ReplyViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ReplyViewHolder();
                view = View.inflate(getActivity(), R.layout.item_listview_user_replypost, null);
                viewHolder.tvTime = (TextView) view.findViewById(R.id.item_listview_user_replypost_time);
                viewHolder.tvparentTitle = (TextView) view.findViewById(R.id.item_listview_user_replypost_parenttitle);
                viewHolder.titleLayout = (RelativeLayout) view.findViewById(R.id.item_listview_user_replypost_parenttitle_rl);
                viewHolder.tvContent = (TextView) view.findViewById(R.id.item_listview_user_replypost_content);
                viewHolder.imageListview = (NoScrollGridView) view.findViewById(R.id.item_listview_user_replypost_container);
                viewHolder.commentListview = (NoScrollListView) view.findViewById(R.id.item_listview_user_replypost_comments);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ReplyViewHolder) view.getTag();
            }

            final Post post = postList.get(position);
            viewHolder.tvTime.setText(DateUtil.getRelativeTime(post.getCreatetime()));
            viewHolder.tvparentTitle.setText(post.getParenttitle());
            viewHolder.tvContent.setText(post.getContent());
            NoScrollGridViewAdapter adapter = new NoScrollGridViewAdapter();
            adapter.setData(post.getImages());
            viewHolder.imageListview.setAdapter(adapter);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostReplyDetailActivity(ReplyFragment.this.getActivity(), post, "", 1);
                }
            });
            viewHolder.titleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostDetailActivity(ReplyFragment.this.getActivity(), post.getParentid());
                }
            });
            return view;
        }
    }

    class ReplyViewHolder {
        TextView tvTime;
        TextView tvparentTitle;
        TextView tvContent;
        NoScrollGridView imageListview;
        NoScrollListView commentListview;
        RelativeLayout titleLayout;
    }

    private class NoScrollGridViewAdapter extends BaseAdapter {
        private List<MyImage> imagesList = new ArrayList<MyImage>();

        public void setData(List<MyImage> images) {
            if (images == null) {
                this.imagesList.clear();
                notifyDataSetChanged();
                return;
            }
            if (this.imagesList != null) {
                this.imagesList.clear();
                this.imagesList.addAll(images);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            if (imagesList.size() > 3) {
                return 3;
            }
            return imagesList.size();
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
            View view = null;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(ReplyFragment.this.getActivity(), R.layout.item_nogridview, null);
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.item_nogridview_image);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemWidth;
            imageView.setLayoutParams(params);
            MyImage image = imagesList.get(position);
            ImageLoader.getInstance().displayImage(image.getImageurl_small(), imageView, Constant.IMAGE_OPTIONS_FOR_PARTNER);
            return view;
        }
    }
}
