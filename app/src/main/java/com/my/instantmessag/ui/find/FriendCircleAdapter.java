package com.my.instantmessag.ui.find;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.my.instantmessag.R;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.entity.FriendCircleBean;
import com.my.instantmessag.mydb.DBHelper;
import com.my.instantmessag.mydb.PersonInfoCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dllo onDetailClick 16/3/8.
 */
public class FriendCircleAdapter extends BaseAdapter {
    private Context context;
    private List<FriendCircleBean> data;
    private onCommentListener mListener;
    private Map<String, ContactsBean> beans;

    public void setListener(onCommentListener mListener) {
        this.mListener = mListener;
    }

    public FriendCircleAdapter(Context context, List<FriendCircleBean> data, Map<String,ContactsBean> beans) {
        this.context = context;
        this.data = data;
        this.beans=beans;
    }

    @Override
    public int getCount() {
        return null == data ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String userName = data.get(position).getUsername();
        CommentViewHolder commentViewHolder;
//        convertView = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend_circle, parent, false);
            commentViewHolder = new CommentViewHolder(convertView);
            convertView.setTag(commentViewHolder);
        } else {
            commentViewHolder = (CommentViewHolder) convertView.getTag();
        }


        commentViewHolder.content.setText(data.get(position).getContent());

        if ( beans.size()!=0) {
            commentViewHolder.name.setText(beans.get(userName).getNickName());
            Bitmap head=beans.get(userName).getHeadImage();
            if (head!=null) {
                commentViewHolder.head.setImageBitmap(head);
            }
        } else {
            commentViewHolder.name.setText(userName);
        }
            commentViewHolder.img.setVisibility(View.VISIBLE);
        if (null != data.get(position).getImg()) {
            commentViewHolder.img.setImageBitmap(data.get(position).getImg());
        } else {
            commentViewHolder.img.setVisibility(View.GONE);
        }
        List<TextView> textViews = commentViewHolder.getTextViews();
        //评论
        int end = 0;
        String comment = "";
//        if (textViews.size() < data.get(position).getComment().size()) {
//            for (int i = textViews.size(); i < data.get(position).getComment().size(); i++) {
//                TextView textView = new TextView(context);
//                end = data.get(position).getComment().get(i).get(0).length() + 1;
//                comment = data.get(position).getComment().get(i).get(0) + ":" + data.get(position).getComment().get(i).get(1);
//                SpannableStringBuilder style = new SpannableStringBuilder(comment);
//                style.setSpan(new ForegroundColorSpan(0xFF4A708B), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                textView.setText(style);
//                textViews.add(textView);
//                commentViewHolder.linear.addView(textView);
//            }
//        }
        commentViewHolder.linear.removeAllViews();
        for (int i = 0; i < data.get(position).getComment().size(); i++) {
            TextView textView = new TextView(context);
            end = data.get(position).getComment().get(i).get(0).length() + 1;
            comment = data.get(position).getComment().get(i).get(0) + ":" + data.get(position).getComment().get(i).get(1);
            SpannableStringBuilder style = new SpannableStringBuilder(comment);
            style.setSpan(new ForegroundColorSpan(0xFF4A708B), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(style);
            commentViewHolder.linear.addView(textView);
        }
        //好评人
        String friends = "❤️";
        for (int i = 0; i < data.get(position).getFriendName().size(); i++) {
            friends += data.get(position).getFriendName().get(i) + ",";
        }
        if (data.get(position).getFriendName().size() != 0) {
            friends = friends.substring(0, friends.lastIndexOf(","));
            commentViewHolder.textView.setVisibility(View.VISIBLE);
            commentViewHolder.textView.setText(friends);
            commentViewHolder.textView.setTextColor(0xFF4A708B);
        } else {
            commentViewHolder.textView.setVisibility(View.GONE);
        }

        //点击事件
        commentViewHolder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCommentClick(data.get(position).getUsername(), data.get(position).getTime(), position);
            }
        });
        commentViewHolder.good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onGoodClick(data.get(position).getUsername(), data.get(position).getTime(), position);
            }
        });
        commentViewHolder.setTextViews(textViews);


        return convertView;
    }

    public void pullRefresh(List<FriendCircleBean> beans) {
        this.data = beans;
        notifyDataSetChanged();
    }

    public void addComment(List<String> comment, int pos) {
        this.data.get(pos).getComment().add(comment);
        notifyDataSetChanged();
    }

    public void addGood(int pos, String myName) {
        data.get(pos).getFriendName().add(myName);
        notifyDataSetChanged();
    }

    public void setHeadImg(Map<String, ContactsBean> beans) {
        this.beans = beans;
        notifyDataSetChanged();
    }


    class CommentViewHolder {
        LinearLayout linear;
        ImageView head, img, good, send;
        TextView name, content;
        List<TextView> textViews;
        TextView textView;

        public List<TextView> getTextViews() {
            return textViews;
        }

        public void setTextViews(List<TextView> textViews) {
            this.textViews = textViews;
        }

        public CommentViewHolder(View v) {
            textView = (TextView) v.findViewById(R.id.item_friend_good_tv);
            linear = (LinearLayout) v.findViewById(R.id.item_friend_comment_liner);
            head = (ImageView) v.findViewById(R.id.item_friend_head_iv);
            img = (ImageView) v.findViewById(R.id.item_friend_content_iv);
            name = (TextView) v.findViewById(R.id.item_friend_name_tv);
            content = (TextView) v.findViewById(R.id.item_friend_content_tv);
            textViews = new ArrayList<>();
            good = (ImageView) v.findViewById(R.id.item_friend_good_iv);
            send = (ImageView) v.findViewById(R.id.item_friend_send_iv);
        }
    }

    interface onCommentListener {
        void onGoodClick(String username, long time, int pos);

        void onCommentClick(String username, long time, int pos);

        void onDetailClick(String username);
    }

}
