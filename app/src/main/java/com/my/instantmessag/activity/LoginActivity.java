package com.my.instantmessag.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.my.instantmessag.R;
import com.my.instantmessag.base.BaseActivity;
import com.my.instantmessag.utils.LogUtils;


/**
 * Created by jz onDetailClick 16/2/29.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private Button login, register;
    private EditText userName, password;
    private String userN, passW;
    private ImageView remember;
    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        remember = (ImageView) findViewById(R.id.remember);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        remember.setOnClickListener(this);
        userN = readWord("userName");
        passW = readWord("passWord");
        if ("T".equals(readWord("status"))) {
            status = true;
            remember.setImageResource(R.mipmap.checkt);
        } else {
            status = false;
            remember.setImageResource(R.mipmap.checkf);
        }
        userName.setText(userN);
        password.setText(passW);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                userN = String.valueOf(userName.getText());
                passW = String.valueOf(password.getText());
                final Dialog dialog = new ProgressDialog(context);
                dialog.setTitle("正在登陆");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                if (null != passW && !"".equals(passW) && null != userN && !"".equals(userN)) {
                    EMChatManager.getInstance().login(userN, passW, new EMCallBack() {//回调
                        @Override
                        public void onSuccess() {
                            dialog.dismiss();
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    EMGroupManager.getInstance().loadAllGroups();
                                    EMChatManager.getInstance().loadAllConversations();
                                    LogUtils.d("main", "登陆聊天服务器成功！");
                                    Toast.makeText(context, "登陆成功", Toast.LENGTH_SHORT).show();
                                    if (status) {
                                        saveWord("userName", userN);
                                        saveWord("passWord", passW);
                                    } else {
                                        saveWord("userName", userN);
                                        removeWord("passWord");
                                    }
                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                    jumpAnimationActivity();
                                    finish();
                                }

                            });
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }

                        @Override
                        public void onError(int code, String message) {
                            dialog.dismiss();

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    EMGroupManager.getInstance().loadAllGroups();
                                    EMChatManager.getInstance().loadAllConversations();
                                    LogUtils.d("main", "登陆聊天服务器失败！");
                                    Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(context, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                jumpAnimationActivity();
                break;
            case R.id.remember:
                if (status) {
                    remember.setImageResource(R.mipmap.checkf);
                    status = false;
                    saveWord("status", "F");
                } else {
                    status = true;
                    remember.setImageResource(R.mipmap.checkt);
                    saveWord("status", "T");
                }
                break;
        }
    }

    //保存SharedPreferences
    private void saveWord(String key, String value) {
        if (key.length() > 0 && key != null && value.length() > 0 && value.length() > 0) {
            SharedPreferences sp = getSharedPreferences("Info", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    //从SharedPreferences读取key
    private String readWord(String key) {
        SharedPreferences sp = getSharedPreferences("Info", MODE_PRIVATE);
        String s = sp.getString(key, "");
        return s;
    }

    //从SharedPreferences移除key
    private void removeWord(String key) {
        SharedPreferences sp = getSharedPreferences("Info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
}
