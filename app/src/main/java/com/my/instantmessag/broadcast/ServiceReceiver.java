package com.my.instantmessag.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.my.instantmessag.service.AppService;

/**
 * 开机启动服务的广播接收者
 *
 * Created by dllo on 16/3/15.
 */
public class ServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, AppService.class);
            context.startService(i);
        }
    }
}
