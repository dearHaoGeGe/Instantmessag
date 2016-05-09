package com.my.instantmessag.ui.chat.v.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.my.instantmessag.R;
import com.my.instantmessag.entity.ChatItemEntity;
import com.my.instantmessag.mydb.MyData;
import com.my.instantmessag.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by dllo onDetailClick 16/3/3.
 */
public class ChatDetailsAdapter extends BaseAdapter{

    private Context context;

    private ArrayList<MyData> data;


    public ChatDetailsAdapter(Context context,ArrayList<MyData> data){

        this.context = context;
        this.data = data;


    }

    @Override
    public int getCount() {

        if (null != data){

            return data.size();
        }else {

            return 0;
        }

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

        MyViewHolder myViewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.itme_chat_details,parent,false);
            myViewHolder = new MyViewHolder(convertView);
            convertView.setTag(myViewHolder);
        }else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }

        if (!data.get(position).getType().equals("send")){

            myViewHolder.getLayout.setVisibility(View.VISIBLE);
            myViewHolder.senLayout.setVisibility(View.GONE);
            myViewHolder.getMessage.setText(data.get(position).getBody());

        }
        else {

            myViewHolder.senLayout.setVisibility(View.VISIBLE);
            myViewHolder.getLayout.setVisibility(View.GONE);
            myViewHolder.sendMessage.setText(data.get(position).getBody());

        }


        return convertView;
    }


    private class MyViewHolder{

        private TextView sendMessage,getMessage;
        private ImageView sendHeadImg,getHeadImg;
        private LinearLayout senLayout,getLayout;


        public MyViewHolder(View view){

            sendMessage = (TextView) view.findViewById(R.id.message_send_chat_details_item);
            getMessage = (TextView) view.findViewById(R.id.message_get_chat_details_item);

            senLayout = (LinearLayout) view.findViewById(R.id.layout_send_chat_details);
            getLayout = (LinearLayout) view.findViewById(R.id.layout_get_chat_details);
        }
    }
}
