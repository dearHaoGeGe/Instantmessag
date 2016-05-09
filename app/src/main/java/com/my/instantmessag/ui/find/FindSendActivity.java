package com.my.instantmessag.ui.find;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.my.instantmessag.R;
import com.my.instantmessag.base.BaseActivity;
import com.my.instantmessag.mydb.FriendCircleInfo;
import com.my.instantmessag.mydb.PersonInfo;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dllo onDetailClick 16/3/9.
 */
public class FindSendActivity extends BaseActivity implements View.OnClickListener, FriendCircleInfo, PersonInfo {
    private ImageView iv, send, back;
    private ArrayList<String> path;
    private EditText ed;
    private ParseObject parseObject;
    private String myName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_send);
        Intent intent = getIntent();
        path = intent.getStringArrayListExtra("data");
        myName = intent.getStringExtra("name");
        iv = (ImageView) findViewById(R.id.find_send_iv);
        send = (ImageView) findViewById(R.id.find_send_finish);
        back = (ImageView) findViewById(R.id.go_back_friendSend);
        ed = (EditText) findViewById(R.id.find_send_ed);
        send.setOnClickListener(this);
        back.setOnClickListener(this);
        iv.setImageBitmap(getDiskBitmap(path.get(0)));

    }

    private Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back_friendSend:
                finish();
                break;
            case R.id.find_send_finish:
                sendFirstData(myName, path.get(0), ed.getText().toString());
                break;
        }
    }

    public void sendFirstData(String user, String path, String content) {
        parseObject = new ParseObject(FRIEND_TABLE_NAME);
        parseObject.put(USER_NAME, user);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        long time = System.currentTimeMillis();
        ParseFile pf = new ParseFile("IMG_" + time + ".png", data);
        parseObject.put(CONTENT_IMG, pf);
        List<List<String>> arrayList = new ArrayList<>();
        List<String> friends = new ArrayList<>();
        parseObject.put(FRIEND_NAME, friends);
        parseObject.put(GOOD, 0);
        parseObject.put(COMMENT, arrayList);
        parseObject.put(CONTENT, content);
        parseObject.put(SUBMIT_TIME, time);
        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(context, "e:" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}

