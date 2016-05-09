package com.my.instantmessag.ui.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.ViewUtils;
import com.my.instantmessag.R;
import com.my.instantmessag.activity.FriendDetailsActivity;
import com.my.instantmessag.activity.MainActivity;
import com.my.instantmessag.base.BaseFragment;
import com.my.instantmessag.mydb.DBHelper;
import com.my.instantmessag.mydb.PersonInfoCallBack;
import com.my.instantmessag.utils.LogUtils;
import com.my.instantmessag.widget.SidebarView;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.utils.ChineseToPinyinHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 这是通讯录的fragment
 * <p/>
 * Created by YJH onDetailClick 16/2/29.
 */
public class ContactsFragment extends BaseFragment {

    private ListView listView_contacts;
    private TextView textView_dialog, textView_emptyinfo;
    private List<ContactsBean> contactsBean;
    private SidebarView sidebarView_contacts;
    private ContactsListViewBaseAdapter adapter;
    private MainActivity mainActivity;
    private List<String> friendNames;
    private String myName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        /**EventBus如果注册之后必须取消注册，如果不取消再次注册会报错*/
        EventBus.getDefault().register(this); //注册EventBus
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView_contacts = (ListView) mainActivity.findViewById(R.id.listView_contacts);
        textView_dialog = (TextView) mainActivity.findViewById(R.id.textView_dialog);
        //textView_emptyinfo = (TextView) mainActivity.findViewById(R.id.textView_emptyinfo);
        sidebarView_contacts = (SidebarView) mainActivity.findViewById(R.id.sidebarView_contacts);

        myName = EMChatManager.getInstance().getCurrentUser();
        friendNames = new ArrayList<>();
        try {
            friendNames = EMContactManager.getInstance().getContactUserNames();//需异步执行
        } catch (EaseMobException e) {
            e.printStackTrace();
        }

        DBHelper.getPersonInfoList(friendNames, new PersonInfoCallBack() {
            @Override
            public void getBean(ContactsBean bean) {

            }

            @Override
            public void getBeans(List<ContactsBean> beans) {
                contactsBean = beans;
                initView();
            }
        }, myName);


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);     //取消注册EventBus
    }

    private void initView() {
        ViewUtils.inject(mainActivity);
        sidebarView_contacts.setTextView(textView_dialog);
        //this.contactsBean = new ArrayList<>();
//        this.contactsBean = getUserList();
        contactsBean = getFriendList();
        Collections.sort(this.contactsBean, new Comparator<ContactsBean>() {
            @Override
            public int compare(ContactsBean lhs, ContactsBean rhs) {
                if (lhs.getFirstLetter().equals("#")) {
                    return 1;
                } else if (rhs.getFirstLetter().equals("#")) {
                    return -1;
                } else {
                    return lhs.getFirstLetter().compareTo(rhs.getFirstLetter());
                }
            }
        });

        View view1 = LayoutInflater.from(mainActivity).inflate(R.layout.item_contacts_head_apply, null);
        View view2 = LayoutInflater.from(mainActivity).inflate(R.layout.item_contacts_head_groupchat, null);
        View view3 = LayoutInflater.from(mainActivity).inflate(R.layout.item_contacts_head_chatroom, null);
        View view4 = LayoutInflater.from(mainActivity).inflate(R.layout.item_contacts_head_helper, null);
        listView_contacts.addHeaderView(view1);
        listView_contacts.addHeaderView(view2);
        listView_contacts.addHeaderView(view3);
        listView_contacts.addHeaderView(view4);

        adapter = new ContactsListViewBaseAdapter(this.contactsBean, mainActivity);
        listView_contacts.setAdapter(adapter);
        //listView_contacts.setEmptyView(textView_emptyinfo);

        listView_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                position = position - 4;
                if (position < 0) {
                    switch (position) {
                        case -4:
                            Toast.makeText(mainActivity, "申请与通知", Toast.LENGTH_SHORT).show();
                            break;
                        case -3:
                            Toast.makeText(mainActivity, "群聊", Toast.LENGTH_SHORT).show();
                            break;

                        case -2:
                            Toast.makeText(mainActivity, "聊天室", Toast.LENGTH_SHORT).show();
                            break;

                        case -1:
                            Toast.makeText(mainActivity, "小助手", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            break;
                    }
                } else {
                    String username = adapter.getList().get(position).getUserName();
                    Intent intent = new Intent(getActivity(), FriendDetailsActivity.class);
                    intent.putExtra("friendName", username);
                    intent.putExtra("position", position);
                    startActivity(intent);
                    mainActivity.jumpAnimationActivity();
                }
            }
        });

//        /**
//         * 长按事件
//         */
//        listView_contacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                position = position - 4;
//                if (position < 0) {
//                    switch (position) {
//                        case -4:
//                            Toast.makeText(mainActivity, "长按~申请与通知", Toast.LENGTH_SHORT).show();
//                            break;
//
//                        case -3:
//                            Toast.makeText(mainActivity, "长按~群聊", Toast.LENGTH_SHORT).show();
//                            break;
//
//                        case -2:
//                            Toast.makeText(mainActivity, "长按~聊天室", Toast.LENGTH_SHORT).show();
//                            break;
//
//                        case -1:
//                            Toast.makeText(mainActivity, "长按~小助手", Toast.LENGTH_SHORT).show();
//                            break;
//
//                        default:
//                            break;
//                    }
//                } else {
//                    String username = contactsBean.get(position).getUserName();
//                    Toast.makeText(mainActivity, "长按~" + username, Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            }
//        });

        /**
         * A~B的索引
         */
        sidebarView_contacts.setOnLetterClickedListener(new SidebarView.OnLetterClickedListener() {
            @Override
            public void onLetterClicked(String str) {
                int position = adapter.getPositionForSection(str.charAt(0));
                if (position == -1) {

                } else {
                    listView_contacts.setSelection(position + 4);
                }
            }
        });

        /**
         * 头布局的索引
         */
        sidebarView_contacts.setOnHeadClickedListener(new SidebarView.OnHeadClickedListener() {
            @Override
            public void onHeadClickedClicked() {
                listView_contacts.setSelection(0);
            }
        });
    }

    /**
     * 获取联系人的首字母
     *
     * @return
     */
    private List<ContactsBean> getUserList() {
        List<ContactsBean> list = new ArrayList<>();

        /*从环信上拉取好友列表*/
        List<String> name = new ArrayList<>();
//        EMChatManager.getInstance().getChatOptions().setUseRoster(true);
        try {
            name = EMContactManager.getInstance().getContactUserNames();//需异步执行
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
        /* ********* */

        for (int i = 0; i < name.size(); i++) {
            ContactsBean bean = new ContactsBean();
            String username = name.get(i);
            String pinyin = ChineseToPinyinHelper.getInstance().getPinyin(username);
            String firstLetter = pinyin.substring(0, 1).toUpperCase();
            if (firstLetter.matches("[A-Z]")) {
                bean.setFirstLetter(firstLetter);
            } else {
                bean.setFirstLetter("#");
            }
            bean.setUserName(username);
            list.add(bean);
        }
        return list;
    }

    private List<ContactsBean> getFriendList() {
        if (null != contactsBean) {
            for (int i = 0; i < contactsBean.size(); i++) {
                String pinyin = ChineseToPinyinHelper.getInstance().getPinyin(contactsBean.get(i).getNickName());
                String firstLetter = pinyin.substring(0, 1).toUpperCase();
                contactsBean.get(i).setFirstLetter(firstLetter);
            }
            return contactsBean;
        } else {
            return null;
        }
    }

    /**
     * 当FriendDetailsActivity创建的时候回发送一个eventbus，
     * 在这里接收String isFriendDetailsCreate是来区别eventbus的，
     * 当接到这个eventbus时把adapter这个对象发到FriendDetailsActivity中
     *
     * @param isFriendDetailsCreate
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(String isFriendDetailsCreate) {
        if (isFriendDetailsCreate.equals("isFriendDetailsCreate")) {
            //LogUtils.i("88888", "isFriendDetailsCreate");
            EventBus.getDefault().post(adapter);
            EventBus.getDefault().post(contactsBean);
        }
    }
}