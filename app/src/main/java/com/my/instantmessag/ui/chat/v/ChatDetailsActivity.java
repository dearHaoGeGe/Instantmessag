package com.my.instantmessag.ui.chat.v;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.my.instantmessag.R;
import com.my.instantmessag.base.BaseActivity;
import com.my.instantmessag.base.BaseApplication;

import com.my.instantmessag.mydb.DBHelper;
import com.my.instantmessag.mydb.MyData;
import com.my.instantmessag.ui.chat.m.VoiceRecorder;
import com.my.instantmessag.ui.chat.p.ChatDetailsPresenter;
import com.my.instantmessag.ui.chat.p.IChatDetailsPresenter;
import com.my.instantmessag.ui.chat.v.adapter.ChatDetailsAdapter;
import com.my.instantmessag.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 聊天详情页面
 * <p/>
 * Created by WN onDetailClick 16/3/3.
 */
public class ChatDetailsActivity extends BaseActivity implements View.OnClickListener,
        TextView.OnEditorActionListener, AdapterView.OnItemClickListener, IChatDetailsActivity, View.OnTouchListener {

    private ImageView goBackImage, face;
    private ListView listView;
    private ChatDetailsAdapter adapter;
    private EditText inputEt;
    private ImageView voiceChatImg, textChatImg;
    private Button voiceChatButton;

    private BaseApplication application;

    private IChatDetailsPresenter chatDetailsPresenter;
    private TextView friendName_tx;
    private String friendName;

    private ArrayList<MyData> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_details);

        /** EventBus 注册 */
        EventBus.getDefault().register(this);

        myFindViewById();

        myOnClickListener();

        init();

        friendName = getIntent().getStringExtra("friendName");
        friendName_tx.setText(friendName);

        chatDetailsPresenter.startGetMessageData(friendName);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {

        application = (BaseApplication) this.getApplication();

        chatDetailsPresenter = new ChatDetailsPresenter(this);

    }


    private void myFindViewById() {

        listView = (ListView) findViewById(R.id.list_view_chat_details);
        goBackImage = (ImageView) findViewById(R.id.go_back_chat_details);
        voiceChatImg = (ImageView) findViewById(R.id.voice_chat_details);
        face = (ImageView) findViewById(R.id.face_chat_details);
        inputEt = (EditText) findViewById(R.id.edit_text_chat_details);
        friendName_tx = (TextView) findViewById(R.id.friend_name_chat_details);
        voiceChatButton = (Button) findViewById(R.id.voice_button_chat_details);
        textChatImg = (ImageView) findViewById(R.id.text_chat_details);
    }

    private void myOnClickListener() {

        goBackImage.setOnClickListener(this);
        face.setOnClickListener(this);

        listView.setOnItemClickListener(this);
        inputEt.setOnClickListener(this);
        inputEt.setOnEditorActionListener(this);

        voiceChatImg.setOnClickListener(this);
        textChatImg.setOnClickListener(this);
        voiceChatButton.setOnTouchListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.go_back_chat_details:
                finish();
                jumpAnimationActivity();
                break;

            case R.id.face_chat_details:


                break;

            case R.id.edit_text_chat_details:

                Toast.makeText(ChatDetailsActivity.this, "1", Toast.LENGTH_SHORT).show();


                break;

            case R.id.voice_chat_details:

                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow
                                (ChatDetailsActivity.this.getCurrentFocus().getWindowToken()
                                        , InputMethodManager.HIDE_NOT_ALWAYS);

                voiceChatImg.setVisibility(View.GONE);
                inputEt.setVisibility(View.GONE);
                voiceChatButton.setVisibility(View.VISIBLE);
                textChatImg.setVisibility(View.VISIBLE);

                break;

            case R.id.text_chat_details:

                textChatImg.setVisibility(View.GONE);
                voiceChatButton.setVisibility(View.GONE);
                inputEt.setVisibility(View.VISIBLE);
                voiceChatImg.setVisibility(View.VISIBLE);

                break;

            case R.id.voice_button_chat_details:

                break;

            default:

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
     * ET 绑定键盘换行键
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


        if (actionId == EditorInfo.IME_ACTION_SEND)

        {
            /** 发送文字消息 */
            chatDetailsPresenter.sendTextMessage(inputEt.getText().toString());

            /** 更新列表 */
            upList();

            listView.setSelection(listView.getBottom());



        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void upList() {

        MyData myData = new MyData();
        myData.setBody(inputEt.getText().toString());
        myData.setType("send");
        data.add(myData);
        DBHelper.getInstance().saveMessage(EMChatManager.getInstance().getCurrentUser(), friendName, inputEt.getText().toString(), "send", DBHelper.formatTime(System.currentTimeMillis()));
        inputEt.setText("");

        adapter.notifyDataSetChanged();
        listView.setSelection(data.size());


    }

    @Override
    public void getData(ArrayList<MyData> data) {

        this.data = data;
        adapter = new ChatDetailsAdapter(this, this.data);
        listView.setAdapter(adapter);
        listView.setSelection(adapter.getCount());

    }


    /**
     * EventBus 接收者
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MyData myData) {

        data.add(myData);
        adapter.notifyDataSetChanged();
        listView.setSelection(listView.getBottom());

    }


    VoiceRecorder voiceRecorder = new VoiceRecorder();

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                LogUtils.e("onTouch", "点击");

                voiceRecorder.startRecording(this);

                break;

            case MotionEvent.ACTION_UP:

                LogUtils.e("onTouch", "抬起");

                chatDetailsPresenter.sendVoiceMessage(voiceRecorder.getVoiceFilePath(), voiceRecorder.stopRecoding());

                break;

            case MotionEvent.ACTION_MOVE:

                LogUtils.e("onTouch", "移动");
                break;
        }

        return false;
    }


}
