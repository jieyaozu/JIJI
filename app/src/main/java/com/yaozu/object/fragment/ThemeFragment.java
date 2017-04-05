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
import com.yaozu.object.entity.HomeForumDataInfo;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.NoScrollGridView;
import com.yaozu.object.widget.StickyListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/22.
 */

public class ThemeFragment extends BaseFragment {
    public static String TAG = "ThemeFragment";
    public StickyListView listView;
    public ListThemeAdapter themeAdapter;
    private UserInfoActivity.StickyScrollCallBack scrollListener;
    private List<Post> postList = new ArrayList<>();
    private String userid;
    private int currentPage = 1;
    private int itemWidth;

    public static ThemeFragment newInstance(String userid) {
        ThemeFragment fragment = new ThemeFragment();
        fragment.setUserid(userid);
        return fragment;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        themeAdapter = new ListThemeAdapter();
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
        String url = DataInterface.FIND_USER_POST_LIST + "userid=" + userid + "&pageindex=" + page;
        RequestManager.getInstance().getRequest(this.getActivity(), url, HomeForumDataInfo.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                listView.setLoading(false);
                if (object != null) {
                    HomeForumDataInfo homeForumDataInfo = (HomeForumDataInfo) object;
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

    class ListThemeAdapter extends BaseAdapter {

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
            ThemeViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ThemeViewHolder();
                view = View.inflate(getActivity(), R.layout.item_listview_theme, null);
                viewHolder.tvTime = (TextView) view.findViewById(R.id.item_listview_theme_time);
                viewHolder.tvSupporu = (TextView) view.findViewById(R.id.item_listview_theme_support);
                viewHolder.tvReply = (TextView) view.findViewById(R.id.item_listview_theme_reply);
                viewHolder.tvTitle = (TextView) view.findViewById(R.id.item_listview_theme_title);
                viewHolder.tvContent = (TextView) view.findViewById(R.id.item_listview_theme_content);
                viewHolder.noScrollGridView = (NoScrollGridView) view.findViewById(R.id.item_listview_theme_container);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ThemeViewHolder) view.getTag();
            }

            final Post post = postList.get(position);
            viewHolder.tvTime.setText(DateUtil.getRelativeTime(post.getCreatetime()));
            viewHolder.tvSupporu.setText(post.getSupportNum() + "赞");
            viewHolder.tvReply.setText(post.getReplyNum() + "回复");
            viewHolder.tvTitle.setText(post.getTitle());
            viewHolder.tvContent.setText(post.getContent());
            NoScrollGridViewAdapter adapter = new NoScrollGridViewAdapter();
            adapter.setData(post.getImages());
            viewHolder.noScrollGridView.setAdapter(adapter);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostDetailActivity(ThemeFragment.this.getActivity(), post);
                }
            });
            return view;
        }
    }

    class ThemeViewHolder {
        TextView tvTime;
        TextView tvSupporu;
        TextView tvReply;
        TextView tvTitle;
        TextView tvContent;
        NoScrollGridView noScrollGridView;
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
                view = View.inflate(ThemeFragment.this.getActivity(), R.layout.item_nogridview, null);
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
