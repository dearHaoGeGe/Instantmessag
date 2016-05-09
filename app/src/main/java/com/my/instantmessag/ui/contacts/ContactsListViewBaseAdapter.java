package com.my.instantmessag.ui.contacts;

import android.content.Context;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.my.instantmessag.R;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.utils.LogUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator onDetailClick 2016/3/2.
 */
public class ContactsListViewBaseAdapter extends BaseAdapter implements SectionIndexer {

    private List<ContactsBean> list = null;
    private Context context;

    public ContactsListViewBaseAdapter(List<ContactsBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setList(List<ContactsBean> list) {
        this.list = list;
    }

    public List<ContactsBean> getList() {
        return list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contacts_listview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView_item_firstletter = (TextView) convertView.findViewById(R.id.textView_item_firstletter);
            viewHolder.textView_item_username = (TextView) convertView.findViewById(R.id.textView_item_username);
            viewHolder.imageView_item_userface = (ImageView) convertView.findViewById(R.id.imageView_item_userface);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ContactsBean contactsBean = list.get(position);
        viewHolder.textView_item_username.setText(contactsBean.getNickName());

        int section = getSectionForPosition(position);
        if ((position) == getPositionForSection(section)) {
            //第一次出现该section
            viewHolder.textView_item_firstletter.setVisibility(View.VISIBLE);
            viewHolder.textView_item_firstletter.setText(contactsBean.getFirstLetter());
            viewHolder.textView_item_firstletter.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        } else {
            viewHolder.textView_item_firstletter.setVisibility(View.GONE);
        }
        if (list.get(position).getHeadImage() != null) {
            viewHolder.imageView_item_userface.setImageBitmap(list.get(position).getHeadImage());
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 做字母索引的时候常常会用到SectionIndexer这个接口
     * 1. getSectionForPosition() 通过该项的位置，获得所在分类组的索引号
     * 2. getPositionForSection() 根据分类列的索引号获得该序列的首个位置
     *
     * @param sectionIndex
     * @return
     */
    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String firstLetter = list.get(i).getFirstLetter();
            char firstChar = firstLetter.charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据position获取分类的首字母的Char ascii值
     *
     * @param position
     * @return
     */
    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getFirstLetter().charAt(0);
    }

    private class ViewHolder {
        private TextView textView_item_firstletter;
        private TextView textView_item_username;
        private ImageView imageView_item_userface;
    }

    /**
     * 刷新适配器
     *
     * @param userList
     */
    public void upData(List<ContactsBean> userList) {
        this.list = userList;
        notifyDataSetChanged();
    }

    /**
     * 移除指定位置的数据
     *
     * @param position
     */
    public void removeData(int position) {
        this.list.remove(position);
        //Log.e("position=",""+position);
        notifyDataSetChanged();
    }
}
