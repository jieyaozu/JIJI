package com.yaozu.object.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaozu.object.R;
import com.yaozu.object.bean.Comment;
import com.yaozu.object.bean.Post;
import com.yaozu.object.utils.Constant;
import com.yaozu.object.utils.DateUtil;
import com.yaozu.object.utils.IntentUtil;
import com.yaozu.object.utils.Utils;
import com.yaozu.object.widget.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jxj42 on 2017/2/9.
 */

public class PostDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Post> mListData = new ArrayList<>();
    //楼主的userid
    public String userid;
    private View headerView;

    public PostDetailAdapter(Context context, String userid) {
        mContext = context;
        this.userid = userid;
    }

    public void setAddData(List<Post> datas) {
        if (datas != null) {
            mListData.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void addHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void clearData() {
        mListData.clear();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Constant.VIEWTYPE_HEADER;
        } else {
            return Constant.VIEWTYPE_LIST;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Constant.VIEWTYPE_HEADER) {
            HeaderViewHolder viewHolder = new HeaderViewHolder(headerView);
            return viewHolder;
        } else {
            MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_listview_replypost, parent, false));
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            ((PostDetailAdapter.HeaderViewHolder) holder).setData(position);
        } else {
            ((PostDetailAdapter.MyViewHolder) holder).setData(position - 1);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mListData.size() + 1;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {


        public HeaderViewHolder(View itemView) {
            super(itemView);
        }

        public void setData(int position) {

        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView usericon;
        TextView userName;
        TextView time;
        TextView content;
        TextView isMain;
        TextView layerIndex;
        TextView commentBt;
        NoScrollListView imageListView;
        NoScrollListView commentListView;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            usericon = (ImageView) itemView.findViewById(R.id.item_listview_replypost_usericon);
            userName = (TextView) itemView.findViewById(R.id.item_listview_replypost_username);
            time = (TextView) itemView.findViewById(R.id.item_listview_replypost_time);
            content = (TextView) itemView.findViewById(R.id.item_listview_replypost_content);
            isMain = (TextView) itemView.findViewById(R.id.item_listview_replypost_ismain);
            layerIndex = (TextView) itemView.findViewById(R.id.item_listview_replypost_layerindex);
            commentBt = (TextView) itemView.findViewById(R.id.item_listview_replypost_comment_bt);
            imageListView = (NoScrollListView) itemView.findViewById(R.id.item_listview_replypost_container);
            commentListView = (NoScrollListView) itemView.findViewById(R.id.item_listview_replypost_comments);
        }

        public void setData(final int position) {
            ImageListAdapter adapter = new ImageListAdapter(mContext);
            imageListView.setAdapter(adapter);

            CommentListAdapter commentListAdapter = new CommentListAdapter();
            commentListView.setAdapter(commentListAdapter);

            final Post post = mListData.get(position);
            Utils.setUserImg(post.getUserIcon(), usericon);
            userName.setText(post.getUserName());
            time.setText(DateUtil.getRelativeTime(post.getCreatetime()));
            content.setText(post.getContent());
            layerIndex.setText((position + 2) + "楼");
            adapter.setData(post.getImages());
            commentListAdapter.setDataList(post.getComments(), post, position + 2);
            if (post.getUserid().equals(userid)) {
                isMain.setVisibility(View.VISIBLE);
            } else {
                isMain.setVisibility(View.GONE);
            }
            commentBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostReplyDetailActivity(mContext, post, userid, position + 2);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostReplyDetailActivity(mContext, post, userid, position + 2);
                }
            });
            usericon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toUserInfoActivity(mContext, post.getGroupid(), post.getUserid());
                }
            });
        }
    }

    /**
     * 评论
     */
    public class CommentListAdapter extends BaseAdapter {
        private List<Comment> commentList = new ArrayList<>();
        private int indexofmain = 0;
        private Post post;

        public void setDataList(List<Comment> comments, Post post, int position) {
            if (comments != null) {
                commentList.addAll(comments);
            }
            this.post = post;
            this.indexofmain = position;
        }

        @Override
        public int getCount() {
            int size = commentList.size();
            if (size > 3) {
                return 4;
            }
            return size;
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
            if (position == 3) {
                view = View.inflate(mContext, R.layout.more_comment, null);
                TextView textView = (TextView) view.findViewById(R.id.more_comment_text);
                textView.setText("更多" + (commentList.size() - 3) + "条评论");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentUtil.toPostReplyDetailActivity(mContext, post, userid, indexofmain);
                    }
                });
            } else {
                view = View.inflate(mContext, R.layout.item_comment_listview, null);
                TextView tvUsername = (TextView) view.findViewById(R.id.item_comment_content);
                Comment comment = commentList.get(position);
                Spannable spannable = getSpanned(comment, userid);
                tvUsername.setText(spannable);
                tvUsername.setMovementMethod(LinkMovementMethod.getInstance());
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.toPostReplyDetailActivity(mContext, post, userid, indexofmain);
                }
            });
            return view;
        }
    }

    private Spannable getSpanned(Comment comment, String mainUserid) {
        String contentstr = null;//userName + ":" + content;
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
            ForegroundColorSpan huifucolor = new ForegroundColorSpan(mContext.getResources().getColor(R.color.top_blue));
            spannable.setSpan(huifucolor, contentstr.indexOf("回复"), contentstr.indexOf("回复") + "回复".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        View.OnClickListener nameclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
        spannable.setSpan(new Clickable(nameclick, userName), 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new Clickable(nameclick, replyName), contentstr.indexOf(replyName), contentstr.indexOf(replyName) + replyName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //图片
        if (hasMain) {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.author);
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
        private String userName;

        public Clickable(View.OnClickListener mListener, String userName) {
            this.mListener = mListener;
            this.userName = userName;
        }

        @Override
        public void onClick(View v) {
            isNameClick = true;
            v.setTag(userName);
            mListener.onClick(v);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(mContext.getResources().getColor(R.color.gray_white));
            ds.setUnderlineText(false);    //去除超链接的下划线
        }
    }
}
