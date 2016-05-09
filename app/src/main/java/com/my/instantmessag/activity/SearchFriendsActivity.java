package com.my.instantmessag.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
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
 * 搜索好友activity
 * <p/>
 * Created by YJH onDetailClick 16/3/3.
 */
public class SearchFriendsActivity extends BaseActivity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private SearchView searchView_search_friends;
    private ImageView back_search;
    private TextView textView_find;
    private ListView listView_searchView_friend;
    private SearchFriendsBaseAdapter adapter;
    private List<ContactsBean> beans;
    private String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        searchView_search_friends = (SearchView) findViewById(R.id.searchView_search_friends);
        back_search = (ImageView) findViewById(R.id.back_search);
        textView_find = (TextView) findViewById(R.id.textView_find);
        listView_searchView_friend = (ListView) findViewById(R.id.listView_searchView_friend);

        // 设置该SearchView默认是否自动缩小为图标     (设置搜索图标在编辑框外，ture时在框内)
        searchView_search_friends.setIconifiedByDefault(false);
        searchView_search_friends.setAlpha(0.6f);
        back_search.setOnClickListener(this);
        textView_find.setOnClickListener(this);
        // 为该SearchView组件设置事件监听器
        searchView_search_friends.setOnQueryTextListener(this);
        // 设置该SearchView显示搜索按钮
        searchView_search_friends.setSubmitButtonEnabled(false);

        /*更改系统搜索图标*/
//        int searchView_icon_id = searchView_search_friends.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
//        ImageView searchView_icon = (ImageView) searchView_search_friends.findViewById(searchView_icon_id);     // 获取搜索图标
//        searchView_icon.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_search:
                finish();
                jumpAnimationActivity();
                break;

            case R.id.textView_find:    //点击查找好友
                if (searchText.equals("")) {
                    Toast.makeText(SearchFriendsActivity.this, "请输入内容!", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(SearchFriendsActivity.this, "好友名字:" + searchText, Toast.LENGTH_SHORT).show();
                    adapter = new SearchFriendsBaseAdapter(this, addSearchFriendsData(new ContactsBean(searchText)));
                    listView_searchView_friend.setAdapter(adapter);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        jumpAnimationActivity();
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 用户输入字符时激发该方法,
     * 点击搜索后执行的方法
     *
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
//        Toast.makeText(SearchFriendsActivity.this, ""+query, Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 单击搜索按钮时激发该方法,
     * 在你一边输入一边监听框里面的内容
     * 例如搜索的时候可以用这个方法
     *
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
//        Toast.makeText(SearchFriendsActivity.this, ""+newText, Toast.LENGTH_SHORT).show();
        searchText = newText;
        return true;
    }

    /**
     * 添加搜索好友的数据
     *
     * @param contactsBean
     */
    private List<ContactsBean> addSearchFriendsData(ContactsBean contactsBean) {
        beans = new ArrayList<>();
        beans.add(contactsBean);
        return beans;
    }

    /**
     * 点击查找后下面有一个listview,这是适配器
     */
    private class SearchFriendsBaseAdapter extends BaseAdapter {

        private Context context;
        private List<ContactsBean> bean;

        public SearchFriendsBaseAdapter(Context context, List<ContactsBean> bean) {
            this.context = context;
            this.bean = bean;
        }

        @Override
        public int getCount() {
            return bean.size();
        }

        @Override
        public Object getItem(int position) {
            return bean.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (null == convertView) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_search_friends_activity, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView_friend_head = (ImageView) convertView.findViewById(R.id.imageView_friend_head);
                viewHolder.textView_friend_name = (TextView) convertView.findViewById(R.id.textView_friend_name);
                viewHolder.button_add_friends = (Button) convertView.findViewById(R.id.button_add_friends);

                viewHolder.button_add_friends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        try {
                            EMContactManager.getInstance().addContact(bean.get(position).getUserName(), "aaa");
                            Toast.makeText(context, bean.get(position).getUserName(), Toast.LENGTH_SHORT).show();

                        } catch (EaseMobException e) {
                            e.printStackTrace();
                        }




                    }
                });

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.textView_friend_name.setText(bean.get(position).getUserName());

            return convertView;
        }

        private class ViewHolder {
            private ImageView imageView_friend_head;
            private TextView textView_friend_name;
            private Button button_add_friends;
        }
    }
}
