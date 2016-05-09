package com.my.instantmessag.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.my.instantmessag.R;
import com.my.instantmessag.base.BaseActivity;
import com.my.instantmessag.entity.ContactsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置页面的联系人黑名单Activity
 * <p/>
 * Created by YJH onDetailClick 16/3/9.
 */
public class ContactsBlackListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    private ImageView iv_back_contacts_black_list;
    private ListView listView_contacts_black_list;
    private List<ContactsBean> blackName;
    private ContactsBlackListBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_black_list);

        iv_back_contacts_black_list = (ImageView) findViewById(R.id.iv_back_contacts_black_list);
        listView_contacts_black_list = (ListView) findViewById(R.id.listView_contacts_black_list);
        iv_back_contacts_black_list.setOnClickListener(this);
        listView_contacts_black_list.setOnItemLongClickListener(this);

//        getBlackList();     //从网络获取黑名单,添加到blackName中
//
//        listView_contacts_black_list.setEmptyView(findViewById(R.id.tv_empty)); //设置listview没有数据的时候有提示内容
//
//        adapter = new ContactsBlackListBaseAdapter(blackName, this);
//        listView_contacts_black_list.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBlackList();     //从网络获取黑名单,添加到blackName中

        listView_contacts_black_list.setEmptyView(findViewById(R.id.tv_empty)); //设置listview没有数据的时候有提示内容

        adapter = new ContactsBlackListBaseAdapter(blackName, this);
        listView_contacts_black_list.setAdapter(adapter);
    }

    /**
     * 从网络获取黑名单,添加到blackName中
     */
    private void getBlackList() {
        blackName = new ArrayList<>();
        List<String> username;
        username = EMContactManager.getInstance().getBlackListUsernames();
        for (int i = 0; i < username.size(); i++) {
            blackName.add(new ContactsBean(username.get(i)));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_contacts_black_list:
                finish();
                jumpAnimationActivity();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        jumpAnimationActivity();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showMyDialog(position);
        return false;
    }

    /**
     * 弹出从黑名单移除的Dialog
     *
     * @param position
     */
    public void showMyDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("移出黑名单");
        builder.setIcon(R.drawable.weixin);
        builder.setMessage("您确定要将此好友移出黑名单吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    EMContactManager.getInstance().deleteUserFromBlackList(blackName.get(position).getUserName());
                    adapter.removeData(position);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
//        View view = LayoutInflater.from(this).inflate(R.layout.item_dialog, null);
//        LinearLayout linearLayout_dialog= (LinearLayout) view.findViewById(R.id.linearLayout_dialog);
//        linearLayout_dialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    EMContactManager.getInstance().deleteUserFromBlackList(blackName.get(position).getUserName());
//                } catch (EaseMobException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        builder.setView(view);  //设置自定义布局
//        EMContactManager.getInstance().deleteUserFromBlackList(blackName.get(position).getUserName());
        builder.show();
    }

    /**
     * 适配器
     */
    private class ContactsBlackListBaseAdapter extends BaseAdapter {

        private List<ContactsBean> list = null;
        private Context context;

        public ContactsBlackListBaseAdapter(List<ContactsBean> list, Context context) {
            this.list = list;
            this.context = context;
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_contacts_head_apply, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView_item_head_apply = (ImageView) convertView.findViewById(R.id.imageView_item_head_apply);
                viewHolder.textView_item_head_apply = (TextView) convertView.findViewById(R.id.textView_item_head_apply);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView_item_head_apply.setText(list.get(position).getUserName());
            viewHolder.imageView_item_head_apply.setImageResource(R.mipmap.ease_default_avatar);

            return convertView;
        }

        private class ViewHolder {
            private ImageView imageView_item_head_apply;
            private TextView textView_item_head_apply;
        }

        public void upData(List<ContactsBean> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        /**
         * 移除指定位置的数据
         *
         * @param position
         */
        public void removeData(int position){
            this.list.remove(position);
            //Log.e("position=",""+position);
            notifyDataSetChanged();
        }
    }
}
