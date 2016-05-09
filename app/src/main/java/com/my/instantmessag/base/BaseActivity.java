package com.my.instantmessag.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.my.instantmessag.R;
import com.my.instantmessag.service.AppService;
import com.my.instantmessag.utils.LogUtils;

/**
 * Created by dllo onDetailClick 16/2/29.
 */
public class BaseActivity extends AppCompatActivity {

    public Context context;
//    public AppService appService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }



    /**
     * 跳转动画(Activity之间)
     */
    public void jumpAnimationActivity() {
        overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
    }
}
