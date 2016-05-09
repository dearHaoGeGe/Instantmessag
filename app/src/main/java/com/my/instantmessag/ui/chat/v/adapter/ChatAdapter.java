package com.my.instantmessag.ui.chat.v.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.my.instantmessag.R;
import com.my.instantmessag.activity.IGetNetMessage;
import com.my.instantmessag.mydb.MyData;

import java.util.ArrayList;

/**
 * Created by dllo onDetailClick 16/3/1.
 */
public class ChatAdapter extends BaseAdapter{

    private ArrayList<MyData> data;
    private ArrayList<Bitmap> headImageData;
    private Context context;

    public ChatAdapter(ArrayList<MyData> data, ArrayList<Bitmap> headImage, Context context){

        this.data = data;
        this.headImageData = headImage;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
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

        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //viewHolder.headImgae.setImageBitmap(headImageData.get(position));
        viewHolder.messgae.setText(data.get(position).getBody());
        viewHolder.nikName.setText(data.get(position).getName());
        viewHolder.time.setText(data.get(position).getTime());

        return convertView;
    }


    private class ViewHolder{
        TextView nikName,messgae,time;
        ImageView headImgae;
        public ViewHolder(View view){
            nikName = (TextView) view.findViewById(R.id.nik_name_chat_item);
            messgae = (TextView) view.findViewById(R.id.message_chat_item);
            time = (TextView) view.findViewById(R.id.time_chat_item);

            headImgae = (ImageView) view.findViewById(R.id.head_image_chat_fragment);
        }
    }
}
