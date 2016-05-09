package com.my.instantmessag.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.mydb.DBHelper;
import com.my.instantmessag.mydb.PersonInfoCallBack;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务
 * <p/>
 * Created by YJH onDetailClick 16/3/7.
 */
public class AppService extends Service {
    public static Map<String, ContactsBean> beans = new HashMap<>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AppBinder();
    }

    public AppService() {

    }

    public class AppBinder extends Binder {

        public AppService getAppService() {

            return AppService.this;
        }

        /**
         * 在Service里获得好友和自己的个人信息
         *
         * @param friends
         * @param myName
         */

        public void getList(List<String> friends, String myName) {
            DBHelper.getPersonInfoList(friends, new PersonInfoCallBack() {
                @Override
                public void getBean(ContactsBean bean) {

                }

                @Override
                public void getBeans(List<ContactsBean> beans) {
                    for (int i = 0; i < beans.size(); i++) {
                        AppService.beans.put(beans.get(i).getUserName(), beans.get(i));
                    }
                    EventBus.getDefault().post(AppService.beans);
                }
            }, myName);
        }


    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(AppService.this, "随意聊开机服务创建成功", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
