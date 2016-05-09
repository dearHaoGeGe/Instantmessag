package com.my.instantmessag.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.my.instantmessag.R;
import com.my.instantmessag.base.BaseActivity;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.service.AppService;
import com.my.instantmessag.ui.chat.v.ChatDetailsActivity;
import com.my.instantmessag.ui.contacts.ContactsListViewBaseAdapter;
import com.my.instantmessag.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 好友详情页面
 * <p/>
 * Created by YJH onDetailClick 16/3/7.
 */
public class FriendDetailsActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_black_list, ll_del_friend;
    private Button btn_send_messages;
    private TextView tv_user_account, tv_friend_nickname, tv_friends_name;
    private ImageView iv_back_contacts, iv_head;
    private String friendName;
    private ContactsListViewBaseAdapter adapter;
    private List<ContactsBean> contactsBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);

        EventBus.getDefault().register(this); //注册EventBus

        EventBus.getDefault().post("isFriendDetailsCreate");    //创建的时候发送一个eventbus告诉ContactsFragment已经创建了

        initFindViewById();

        friendName = getIntent().getStringExtra("friendName");
//        LogUtils.i("传过来的位置是:", "" + getIntent().getIntExtra("position", 0));    //从ContactsFragment过来的点击的位置
        if (null != contactsBean.get(getIntent().getIntExtra("position", 0)).getHeadImage()) {
            iv_head.setImageBitmap(contactsBean.get(getIntent().getIntExtra("position", 0)).getHeadImage());
        }
        tv_friend_nickname.setText(contactsBean.get(getIntent().getIntExtra("position", 0)).getNickName());
        tv_friends_name.setText(friendName);
        tv_user_account.setText("账号:" + friendName);
        tv_friend_nickname.setCompoundDrawables(null, null, null, getResources().getDrawable(R.mipmap.ic_camera));
    }

    private void initFindViewById() {
        ll_black_list = (LinearLayout) findViewById(R.id.ll_black_list);
        ll_del_friend = (LinearLayout) findViewById(R.id.ll_del_friend);
        btn_send_messages = (Button) findViewById(R.id.btn_send_messages);
        tv_user_account = (TextView) findViewById(R.id.tv_user_account);
        tv_friend_nickname = (TextView) findViewById(R.id.tv_friend_nickname);
        tv_friends_name = (TextView) findViewById(R.id.tv_friends_name);
        iv_back_contacts = (ImageView) findViewById(R.id.iv_back_contacts);
        iv_head = (ImageView) findViewById(R.id.iv_head);

        ll_black_list.setOnClickListener(this);
        ll_del_friend.setOnClickListener(this);
        btn_send_messages.setOnClickListener(this);
        iv_back_contacts.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_black_list:    //添加黑名单
                showDialogBlackList();
                break;

            case R.id.ll_del_friend:        //删除好友
                showDialogDeleteFriend();
                break;

            case R.id.btn_send_messages:    //发消息
                Intent intent = new Intent(this, ChatDetailsActivity.class);
                intent.putExtra("friendName", friendName);
                startActivity(intent);
                jumpAnimationActivity();
                finish();
                break;

            case R.id.iv_back_contacts:
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

    /**
     * 删除好友Dialog
     */
    private void showDialogDeleteFriend() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除好友");
        builder.setIcon(R.drawable.weixin);
        builder.setMessage("您确定要删除吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFriend();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    /**
     * 添加好友到黑名单Dialog
     */
    private void showDialogBlackList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加黑名单");
        builder.setIcon(R.drawable.weixin);
        builder.setMessage("您确定要将此好友添加到黑名单吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addUserToBlackList();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    /**
     * 删除好友的方法
     */
    private void deleteFriend() {
        try {
            EMContactManager.getInstance().deleteContact(friendName);   //删除好友
            LogUtils.i("传过来的位置是:", "" + getIntent().getIntExtra("position", 0));    //从ContactsFragment过来的点击的位置
            adapter.removeData(getIntent().getIntExtra("position", 0));
            Toast.makeText(FriendDetailsActivity.this, friendName + "已经删除", Toast.LENGTH_SHORT).show();
            finish();
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加好友到黑名单方法
     */
    private void addUserToBlackList() {
        try {
            //第二个参数如果为true，则把用户加入到黑名单后双方发消息时对方都收不到；false,则
            //我能给黑名单的中用户发消息，但是对方发给我时我是收不到的
            EMContactManager.getInstance().addUserToBlackList(friendName, false);
            adapter.removeData(getIntent().getIntExtra("position", 0));
            Toast.makeText(FriendDetailsActivity.this, friendName + "已经添加到黑名单", Toast.LENGTH_SHORT).show();
            finish();
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 从ContactsFragment接到adapter对象,
     * 然后在本类中使用
     *
     * @param adapter
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(ContactsListViewBaseAdapter adapter) {
        this.adapter = adapter;
//        if (null != this.adapter) {
//            LogUtils.i("88888", "已经收到adapter");
//        }
    }

    /**
     * 从ContactsFragment接到contactsBean对象,
     * 然后在本类中使用
     *
     * @param contactsBean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(List<ContactsBean> contactsBean) {
        this.contactsBean = contactsBean;
    }
}
