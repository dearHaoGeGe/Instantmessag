package com.my.instantmessag.utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.my.instantmessag.base.BaseApplication;

/**
 * Created by dllo onDetailClick 16/3/1.
 */
public class ToastUtils {
    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Toast toast = null;
    private static Object synObj = new Object();
    private static Context context = BaseApplication.getContext();


    /**
     * Toast发送消息，默认Toast.LENGTH_SHORT
     *
     * @param msg
     * @author WikerYong   Email:<a href="#">yw_312@foxmail.com</a>
     * @version 2012-5-22 上午11:13:35
     */
    public static void showMessage(final int msg) {
        showMessage(msg, Toast.LENGTH_SHORT);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_LONG
     *
     * @param msg
     * @author WikerYong   Email:<a href="#">yw_312@foxmail.com</a>
     * @version 2012-5-22 上午11:13:35
     */
    public static void showMessageLong(final int msg) {
        showMessage(msg, Toast.LENGTH_LONG);
    }

    /**
     * Toast发送消息
     *
     * @param msg
     * @param len
     * @author WikerYong   Email:<a href="#">yw_312@foxmail.com</a>
     * @version 2012-5-22 上午11:14:09
     */
    public static void showMessage(final int msg,
                                   final int len) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.cancel();
                                toast.setText(msg);
                                toast.setDuration(len);
                            } else {
                                toast = Toast.makeText(context, msg, len);
                            }
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * Toast发送消息
     *
     * @param msg
     * @param len
     * @author WikerYong   Email:<a href="#">yw_312@foxmail.com</a>
     * @version 2012-5-22 上午11:14:27
     */
    public static void showMessage(final String msg,
                                   final int len) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
                                toast.cancel();
                                toast.setText(msg);
                                toast.setDuration(len);
                            } else {
                                toast = Toast.makeText(context, msg, len);
                            }
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

    /**
     * 关闭当前Toast
     *
     * @author WikerYong   Email:<a href="#">yw_312@foxmail.com</a>
     * @version 2012-5-22 上午11:14:45
     */
    public static void cancelCurrentToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}