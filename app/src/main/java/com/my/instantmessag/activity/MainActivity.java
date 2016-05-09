package com.my.instantmessag.activity;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.NetUtils;
import com.my.instantmessag.R;
import com.my.instantmessag.base.BaseActivity;
import com.my.instantmessag.mydb.DBHelper;
import com.my.instantmessag.mydb.FriendCircleInfo;
import com.my.instantmessag.mydb.MyData;
import com.my.instantmessag.service.AppService;
import com.my.instantmessag.ui.chat.v.ChatDetailsActivity;
import com.my.instantmessag.ui.chat.v.ChatFragment;
import com.my.instantmessag.ui.contacts.ContactsFragment;
import com.my.instantmessag.ui.find.FindFragment;
import com.my.instantmessag.ui.setting.SettingFragment;
import com.my.instantmessag.utils.LogUtils;
import com.my.instantmessag.widget.ChangeColorIconWithTextView;
import com.parse.ParseObject;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, FriendCircleInfo, SettingFragment.OnSettingListener {

    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private List<ChangeColorIconWithTextView> mTabIndicator = new ArrayList<>();
    private Toolbar toolbar;
    private List<String> userNames;
    private DBHelper helper;
    private String user;
    private ParseObject parseObject;
    private NewMessageBroadcastReceiver msgReceiver;
    private ChatFragment chatFragment;
    private IGetNetMessage getNetMessage;
    //绑定service
    private boolean isBound = false;
    public AppService.AppBinder appBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            appBinder = (AppService.AppBinder) service;

            if (appBinder != null) {
                appBinder.getList(userNames, user);
            } else {
                Toast.makeText(context, "空了", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
    //获取到EMChatOptions对象
    private EMChatOptions options = EMChatManager.getInstance().getChatOptions();


    private int notificationNumber = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setOverflowShowingAlways();
        user = EMChatManager.getInstance().getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        initDatas();    //添加fragment数据


        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);

        helper = DBHelper.getInstance();


        //获取好友的username list，开发者需要根据username去自己服务器获取好友的详情

        try {
            EMChatManager.getInstance().getChatOptions().setUseRoster(true);
            userNames = EMContactManager.getInstance().getContactUserNames();//需异步执行
        } catch (EaseMobException e) {
            e.printStackTrace();
        }


        user = EMChatManager.getInstance().getCurrentUser();
        userNames.add(user);


        EMChat.getInstance().setAppInited();

        //注册一个监听连接状态的listener
        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());

        //只有注册了广播才能接收到新消息，目前离线消息，在线消息都是走接收消息的广播（离线消息目前无法监听，在登录以后，接收消息广播会执行一次拿到所有的离线消息）
        msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);

        registeredBroadcast();     //注册广播监听好友请求,同意好友请求等事件

        EMContactManager.getInstance().setContactListener(new MyContactListener());

        EMConversation conversation = EMChatManager.getInstance().getConversation(user);
//        List<EMMessage> messages = conversation.loadMoreMsgFromDB(startMsgId, pagesize);

        EMChat.getInstance().setAppInited();


        readSettingInfo(user);  //读取上一次的设置
    }

    /**
     * 绑定服务
     */
    public void bindAppService() {
        if (!isBound) {
            Intent intent = new Intent(this, AppService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
            LogUtils.i("BaseActivity", "AppService服务已经绑定");
            isBound = true;
        }
    }

    /**
     * 解除绑定服务
     */
    public void unbindAppService() {
        if (isBound) {
            unbindService(connection);
            LogUtils.i("BaseActivity", "AppService服务已经解除绑定");
            isBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindAppService();   /**绑定服务*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindAppService(); /**解除绑定服务*/
        notificationNumber = 0;
    }

    /**
     * 创建一条发送消息
     *
     * @param to
     * @return
     */
    private EMMessage createSentTextMsg(String to, String body) {
        EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
        String currentUser = EMChatManager.getInstance().getCurrentUser();
        TextMessageBody textMessageBody = new TextMessageBody(body);
        msg.addBody(textMessageBody);
        msg.setTo(to);
        msg.setFrom(currentUser);
        msg.setMsgTime(System.currentTimeMillis());
        return msg;
    }

    @Override
    public void onSetting(int a) {
        LogUtils.d("----->", "" + a);
    }

    private class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {
            // 保存增加的联系人
            Toast.makeText(context, "保存增加的联系人", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除

        }

        @Override
        public void onContactInvited(final String username, String reason) {
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒
            Toast.makeText(MainActivity.this, "发过来了" + username + "" + reason, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder a = new AlertDialog.Builder(context);
            a.setTitle("添加好友请求");
            a.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        EMChatManager.getInstance().acceptInvitation(username);

                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }
                }
            });
            a.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        EMChatManager.getInstance().refuseInvitation(username);
                    } catch (EaseMobException e) {
                        e.printStackTrace();
                    }
                }
            });
            a.show();
        }

        @Override
        public void onContactAgreed(String username) {
            //同意好友请求
        }

        @Override
        public void onContactRefused(String username) {
            // 拒绝好友请求

        }

    }

    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
            //已连接到服务器
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        Toast.makeText(context, "  帐号已经被移除 ", Toast.LENGTH_SHORT).show();
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        Toast.makeText(context, "帐号在其他设备登陆", Toast.LENGTH_SHORT).show();
                    } else if (NetUtils.hasNetwork(MainActivity.this)) {
                        Toast.makeText(context, "  连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "  当前网络不可用，请检查网络设置 ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    /**
     * 添加fragment数据
     */
    private void initDatas() {

        chatFragment = new ChatFragment();


        mTabs.add(chatFragment);
        mTabs.add(new ContactsFragment());
        mTabs.add(new FindFragment());
        mTabs.add(new SettingFragment());

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mTabs.get(arg0);
            }
        };

        initTabIndicator();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Toolbar菜单点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_search:        //搜索
                Toast.makeText(MainActivity.this, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_group_chat:    //发起群聊
                Toast.makeText(MainActivity.this, "发起群聊", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_add_friend:    //添加朋友
                Toast.makeText(MainActivity.this, "添加朋友", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, SearchFriendsActivity.class));    //跳转到搜索好友页面
                jumpAnimationActivity();
                break;
            case R.id.action_scan:          //扫一扫
                startActivity(new Intent(this, ScanActivity.class)); //进入扫码activity
                break;

            case R.id.action_feed:          //意见反馈
                startActivity(new Intent(this, CreateQRCodeAty.class));
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    /**
     * 初始化最下面的图标
     */
    private void initTabIndicator() {
        ChangeColorIconWithTextView one = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_one);
        ChangeColorIconWithTextView two = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_two);
        ChangeColorIconWithTextView three = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_three);
        ChangeColorIconWithTextView four = (ChangeColorIconWithTextView) findViewById(R.id.id_indicator_four);

        mTabIndicator.add(one);
        mTabIndicator.add(two);
        mTabIndicator.add(three);
        mTabIndicator.add(four);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);


        one.setIconAlpha(1.0f);
    }

    @Override
    public void onPageSelected(int arg0) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            ChangeColorIconWithTextView left = mTabIndicator.get(position);
            ChangeColorIconWithTextView right = mTabIndicator.get(position + 1);

            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

        resetOtherTabs();

        switch (v.getId()) {
            case R.id.id_indicator_one:
                mTabIndicator.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_two:
                mTabIndicator.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_three:
                mTabIndicator.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.id_indicator_four:
                mTabIndicator.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                break;
        }
    }

    /**
     * 重置其他的Tab
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicator.size(); i++) {
            mTabIndicator.get(i).setIconAlpha(0);
        }
    }


    private void setOverflowShowingAlways() {
        try {
            // true if a permanent menu key is present, false otherwise.
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 接收其他用户发消息的广播接收者
     */
    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 注销广播

            abortBroadcast();

            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");

            //发送方
            String from = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);


            EMConversation conversation = EMChatManager.getInstance().getConversation(from);

            if (null != message.getBody()) {
                String body = message.getBody().toString().replace("txt:\"", "").replace("\"", "");
                showIntentActivityNotify(from, body); //弹出Notification
                helper.saveMessage(user, from, body, "receive", DBHelper.formatTime(message.getMsgTime()));


                LogUtils.e("mainActivity", message + "");

                /** EventBus 发送端 */
                MyData myData = new MyData();
                myData.setName(from);
                myData.setBody(body);
                myData.setType("receive");
                EventBus.getDefault().post(myData);

            }


            // 如果是群聊消息，获取到group id
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                from = message.getTo();
            }
            if (!from.equals(from)) {
                // 消息不是发给当前会话，return
                return;
            }
        }
    }

    /**
     * 监听好友请求,同意好友请求等事件(广播接收者)
     */
    private BroadcastReceiver contactInviteReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //请求理由
            final String reason = intent.getStringExtra("reason");
            final boolean isResponse = intent.getBooleanExtra("isResponse", false);
            //消息发送方username
            final String from = intent.getStringExtra("username");
            //sdk暂时只提供同意好友请求方法，不同意选项可以参考微信增加一个忽略按钮。
            if (!isResponse) {
                Log.d("TAG", from + "请求加你为好友,reason: " + reason);
            } else {
                Log.d("TAG", from + "同意了你的好友请求");
            }
        }
    };

    /**
     * 注册广播contactInviteReceiver
     */

    private void registeredBroadcast(){
        IntentFilter inviteIntentFilter = new IntentFilter(EMChatManager.getInstance().getContactInviteEventBroadcastAction());
        registerReceiver(contactInviteReceiver, inviteIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(msgReceiver);
        unregisterReceiver(contactInviteReceiver);
    }

    /**
     * 显示跳转Activity的Notification
     *
     * @param fromUser 发送用户的名字
     * @param body     发送的消息内容
     */
    private void showIntentActivityNotify(String fromUser, String body) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);

        builder.setSmallIcon(R.drawable.weixin);
        builder.setContentTitle(fromUser);
        builder.setContentText(body);
        builder.setTicker(body);    //设置上升动画效果
        builder.setNumber(++notificationNumber);
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_VIBRATE);

        Intent jumpIntent = new Intent(this, ChatDetailsActivity.class);
        jumpIntent.putExtra("friendName", fromUser);     //为ChatDetailsActivity传用户名
        PendingIntent intent = PendingIntent.getActivity(this, 0, jumpIntent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(intent);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        manager.notify(0, notification);
    }

    /**
     * 读取设置的存储
     *
     * @param User 用户名,用这个作为key值
     */
    public void readSettingInfo(String User) {
        SharedPreferences sharedPreferences = getSharedPreferences("SettingInfoMain", Context.MODE_PRIVATE);
        options.setNotificationEnable(sharedPreferences.getBoolean(User + "_接收新消息通知", true));
        options.setNoticeBySound(sharedPreferences.getBoolean(User + "_声音", true));
        options.setNoticedByVibrate(sharedPreferences.getBoolean(User + "_震动", true));
        options.setUseSpeaker(sharedPreferences.getBoolean(User + "_使用扬声器播放语音", true));
    }

    /**
     * 保存设置存储
     *
     * @param User       用户名
     * @param tixing     是否有提醒
     * @param sound      是否有声音
     * @param shock      是否有震动
     * @param useSpeaker 是否使用扬声器播放语音
     */
    public void saveSettingInfo(String User, boolean tixing, boolean sound, boolean shock, boolean useSpeaker) {
        SharedPreferences sharedPreferences = getSharedPreferences("SettingInfoMain", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(User + "_接收新消息通知", tixing);
        editor.putBoolean(User + "_声音", sound);
        editor.putBoolean(User + "_震动", shock);
        editor.putBoolean(User + "_使用扬声器播放语音", useSpeaker);

        //设置收到消息是否有新消息通知，默认为true
        options.setNotificationEnable(tixing);
        //设置收到消息是否有声音提示，默认为true
        options.setNoticeBySound(sound);
        //设置收到消息是否震动 默认为true
        options.setNoticedByVibrate(shock);
        //设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(useSpeaker);

        editor.commit();
    }
}
