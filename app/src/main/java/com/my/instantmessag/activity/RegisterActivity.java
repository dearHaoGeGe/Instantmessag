package com.my.instantmessag.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.my.instantmessag.R;
import com.my.instantmessag.base.BaseActivity;
import com.my.instantmessag.mydb.PersonInfo;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by dllo onDetailClick 16/3/1.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener,PersonInfo {
    private EditText userName, password, confirm;
    private Button submit;
    private String userN, passW, cfm;
    private ParseObject parseObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userName = (EditText) findViewById(R.id.register_userName);
        password = (EditText) findViewById(R.id.register_passWord);
        confirm = (EditText) findViewById(R.id.register_confirm);
        submit = (Button) findViewById(R.id.register_submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.arg1) {
                    case 1:
                        Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "用户已存在！", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "注册失败，无权限！", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(getApplicationContext(), "注册失败:用户名或密码为空 ", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        registerUser(userN,passW);
                        break;
                    case 6:
                        Toast.makeText(getApplicationContext(), "请确认密码", Toast.LENGTH_SHORT).show();
                        break;

                }
                return false;
            }
        });

        userN = String.valueOf(userName.getText());
        passW = String.valueOf(password.getText());
        cfm = String.valueOf(confirm.getText());
        new Thread(new Runnable() {

            public void run() {
                int errorCode = 0;
                Message msg = new Message();

                try {

                    if (null == passW || null == cfm) {
                        errorCode = 1;
                        msg.arg1 = 6;
                        handler.sendMessage(msg);
                    } else if (passW .equals(cfm)) {
                        EMChatManager.getInstance().createAccountOnServer(userN, passW);
                    } else {
                        errorCode = 1;
                        msg.arg1 = 6;
                        handler.sendMessage(msg);
                    }
                    // 调用sdk注册方法

                } catch (final EaseMobException e) {
                    //注册失败
                    errorCode = e.getErrorCode();
                    if (errorCode == EMError.NONETWORK_ERROR) {
                        msg.arg1 = 1;
                        handler.sendMessage(msg);
                    } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                        msg.arg1 = 2;
                        handler.sendMessage(msg);
                    } else if (errorCode == EMError.UNAUTHORIZED) {
                        msg.arg1 = 3;
                        handler.sendMessage(msg);
                    } else {
                        msg.arg1 = 4;
                        handler.sendMessage(msg);
                    }
                } finally {
                    //成功
                    if (errorCode == 0) {
                        msg.arg1 = 5;
                        handler.sendMessage(msg);
                    }
                }
            }

        }).start();
    }
    public void registerUser(String userN,String passW){
        parseObject = new ParseObject(PERSON_TABLE_NAME);
        parseObject.put(USER, userN);
        parseObject.put(NICK_NAME,userN);
        parseObject.put(SEX,"未知");
        parseObject.put("pass_word",passW);
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(getApplicationContext(), "注册成功 ", Toast.LENGTH_SHORT).show();
                    finish();
                    jumpAnimationActivity();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        jumpAnimationActivity();
        return super.onKeyDown(keyCode, event);
    }
}
