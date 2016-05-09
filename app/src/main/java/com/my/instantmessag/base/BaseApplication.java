package com.my.instantmessag.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.easemob.chat.EMChat;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.my.instantmessag.activity.MainActivity;
import com.my.instantmessag.entity.ContactsBean;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.my.instantmessag.utils.LogUtils;
import com.parse.Parse;


/**
 * Created by JZ onDetailClick 16/2/29.
 */
public class BaseApplication extends Application {
    public static Context context;
    private DbUtils dbUtils;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        EMChat.getInstance().init(context);


        dbUtils = DbUtils.create(getApplicationContext(), "MyDB");

        try {

            dbUtils.createTableIfNotExist(ContactsBean.class);

        } catch (DbException e) {
            e.printStackTrace();
        }

        /**
         * debugMode == true 时为打开，sdk 会在log里输入调试信息
         * @param debugMode
         * 在做代码混淆的时候需要设置成false
         */

        /**
         * debugMode == true 时为打开，sdk 会在log里输入调试信息
         * @param debugMode
         * 在做代码混淆的时候需要设置成false
         */

        EMChat.getInstance().setDebugMode(true);//在做打包混淆时，要关闭debug模式，避免消耗不必要的资源

        //获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();

        //默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(true);

        //设置收到消息是否有新消息通知，默认为true
        options.setNotificationEnable(true);
        //设置收到消息是否有声音提示，默认为true
        options.setNoticeBySound(true);
        //设置收到消息是否震动 默认为true
        options.setNoticedByVibrate(true);
        //设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(true);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this);
    }
}