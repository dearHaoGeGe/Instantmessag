package com.my.instantmessag.ui.find;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;
import com.my.instantmessag.R;
import com.my.instantmessag.base.BaseActivity;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.entity.FriendCircleBean;
import com.my.instantmessag.mydb.DBHelper;
import com.my.instantmessag.mydb.FriendCircleInfo;
import com.my.instantmessag.mydb.FriendCircleInfoCallBack;
import com.my.instantmessag.mydb.PersonInfo;
import com.my.instantmessag.mydb.PersonInfoCallBack;
import com.my.instantmessag.service.AppService;
import com.my.instantmessag.widget.RefreshListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.wq.photo.widget.PickConfig;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.sax.TemplatesHandler;

/**
 * Created by dllo onDetailClick 16/3/7.
 */
public class FriendCircleActivity extends BaseActivity implements View.OnClickListener, FriendCircleInfo, PersonInfo, View.OnLongClickListener {
    private RefreshListView refresh;
    private List<String> list = new ArrayList();
    private FriendCircleAdapter adapter;
    private ImageView back, camera;
    private Handler h = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            refresh.completeRefresh();
            return false;
        }
    });
    private ParseObject parseObject;
    private List<List<String>> comments;
    private List<String> friends;
    private List<String> myFriends;
    private List<FriendCircleBean> data;
    private String myName;
    private int good = 0;
    private Activity activity;
    private View headView;
    private HeadViewHolder holder;
    private Map<String, ContactsBean> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendcircle);
        //注册eventBus
        EventBus.getDefault().register(this);

        map = AppService.beans;

        headView = View.inflate(context, R.layout.item_friend_head, null);
        holder = new HeadViewHolder(headView);
        activity = this;
        myName = EMChatManager.getInstance().getCurrentUser();
        findViewById(R.id.go_back_friendCircle).setOnClickListener(this);
        camera = (ImageView) findViewById(R.id.camera_friendCircle);
        camera.setOnClickListener(this);
        camera.setOnLongClickListener(this);

        refresh = (RefreshListView) findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new RefreshListView.OnRefreshListener() {

            @Override
            public void onPullRefresh() {

                DBHelper.getFriendCircleInfo(friends, new FriendCircleInfoCallBack() {

                    @Override
                    public void getBean(List<FriendCircleBean> beans) {
                        adapter.pullRefresh(beans);
                        refresh.completeRefresh();
                        Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onLoadingMore() {
                requestDataFromServer(true);
            }
        });


        //获取好友列表
        try {
            friends = EMChatManager.getInstance().getContactUserNames();
            friends.add(myName);
        } catch (EaseMobException e) {
            e.printStackTrace();
        }
        if (null != friends && friends.size() > 0) {
            DBHelper.getPersonInfo(EMChatManager.getInstance().getCurrentUser(), new PersonInfoCallBack() {
                @Override
                public void getBean(ContactsBean bean) {

                    holder.username.setText(bean.getNickName());
                    if (null != bean.getCover()) {
                        holder.cover.setImageBitmap(bean.getCover());
                    }
                    holder.cover.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int chose_mode = PickConfig.MODE_SINGLE_PICK;
                            UCrop.Options options = new UCrop.Options();
                            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                            options.setCompressionQuality(80);
                            new PickConfig.Builder(activity)
                                    .isneedcrop(true)
                                    .isneedcamera(true)
                                    .isSqureCrop(false)
                                    .setUropOptions(options)
                                    .maxPickSize(1)
                                    .spanCount(3)
                                    .pickMode(chose_mode)
                                    .setPickRequestCode(10086)
                                    .build();
                            Toast.makeText(context, "点击了", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (null != bean.getHeadImage()) {
                        holder.head.setImageBitmap(bean.getHeadImage());
                    }
                    refresh.addHeaderView(headView);
                    //从服务器拿到数据
                    DBHelper.getFriendCircleInfo(friends, new FriendCircleInfoCallBack() {

                        @Override
                        public void getBean(List<FriendCircleBean> beans) {
                            data = beans;
                            adapter = new FriendCircleAdapter(context, beans, map);
                            adapter.setListener(new FriendCircleAdapter.onCommentListener() {

                                @Override
                                public void onGoodClick(String username, long time, int pos) {
                                    sendGood(username, time, pos);
                                }

                                @Override
                                public void onCommentClick(final String username, final long time, final int pos) {
                                    final Dialog dialog = new AlertDialog.Builder(context).create();
                                    View dialogView = LayoutInflater.from(context).inflate(R.layout.item_dialog_friendcircle, null);
                                    final EditText et = (EditText) dialogView.findViewById(R.id.dialog_friendCircle_et);
                                    dialogView.findViewById(R.id.dialog_friendCircle_btn).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sendComment(username, et.getText().toString(), time, pos);
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                    Window win = dialog.getWindow();
                                    win.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                                    win.setGravity(Gravity.BOTTOM);
                                    WindowManager.LayoutParams lp = win.getAttributes();
                                    lp.width = WindowManager.LayoutParams.FILL_PARENT;
                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                    win.setAttributes(lp);
                                    dialog.setContentView(dialogView);

                                }

                                @Override
                                public void onDetailClick(String username) {
                                    // TODO: 16/3/14 聊天详情

                                }
                            });
                            refresh.setAdapter(adapter);

                        }
                    });
                }

                @Override
                public void getBeans(List<ContactsBean> beans) {

                }
            });
        }


    }

    //eventBus接收
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行

    public void onUserEvent( Map<String,ContactsBean> beans) {

        adapter.setHeadImg(beans);
    }

    private void requestDataFromServer(final boolean isLoading) {
        new Thread() {
            public void run() {
                SystemClock.sleep(3000);
                if (isLoading) {

                } else {
                    list.add(0, "更新到最新的数据");
                }
                //更新UI
                h.sendEmptyMessage(0);
            }

            ;
        }.start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back_friendCircle:
                finish();
                jumpAnimationActivity();
                break;
            case R.id.camera_friendCircle:
                int chose_mode = PickConfig.MODE_SINGLE_PICK;
                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                options.setCompressionQuality(80);
                new PickConfig.Builder(this)
                        .isneedcrop(true)
                        .isneedcamera(true)
                        .isSqureCrop(true)
                        .setUropOptions(options)
                        .maxPickSize(1)
                        .spanCount(3)
                        .setPickRequestCode(10607)
                        .pickMode(chose_mode).build();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        jumpAnimationActivity();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 10607) {
            //在data中返回 选择的图片列表
            ArrayList<String> paths = data.getStringArrayListExtra("data");
            Intent intent = new Intent(context, FindSendActivity.class);
            intent.putStringArrayListExtra("data", paths);
            intent.putExtra("name", myName);
            startActivity(intent);

        } else if (resultCode == RESULT_OK && requestCode == 10086) {
            ArrayList<String> paths = data.getStringArrayListExtra("data");
            for (int i = 0; i < paths.size(); i++) {
                Bitmap bitmap = getDiskBitmap(paths.get(i));
                sendCover(myName, bitmap);
            }

        }
    }

    //把字节转化成图片
    private Bitmap bytesToBitmap(byte[] b) {
        if (b.length != 0) {

            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public void sendCover(String user, final Bitmap bitmap) {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(PERSON_TABLE_NAME);
        parseQuery.whereEqualTo(USER, user);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (0 != objects.size()) {
                        Toast.makeText(FriendCircleActivity.this, "" + objects.size(), Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < objects.size(); i++) {
                            parseObject = objects.get(i);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] data = stream.toByteArray();
                            long time = System.currentTimeMillis();
                            ParseFile pf = new ParseFile("IMG_" + time + ".png", data);
                            parseObject.put(COVER_IMG, pf);
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        holder.cover.setImageBitmap(bitmap);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(context, "e:" + e, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                }
            }
        });

    }

    //从CD卡读取图片
    private Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {

        }
        return bitmap;
    }

    public void sendGood(final String user, final long time, final int pos) {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(FRIEND_TABLE_NAME);
        parseQuery.whereEqualTo(USER_NAME, user);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (0 != objects.size()) {
                        for (int i = 0; i < objects.size(); i++) {
                            if (objects.get(i).getLong(SUBMIT_TIME) == time) {
                                parseObject = objects.get(i);
                                good = parseObject.getInt(GOOD) + 1;
                                List<String> goodName = parseObject.getList(FRIEND_NAME);
                                if (goodName.contains(myName)) {
                                    Toast.makeText(context, "你已经赞过了", Toast.LENGTH_SHORT).show();
                                } else {
                                    goodName.add(myName);
                                    parseObject.put(GOOD, good);
                                    parseObject.put(FRIEND_NAME, goodName);
                                    parseObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                adapter.addGood(pos, myName);
                                            } else {
                                                Toast.makeText(context, "e:" + e, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            }
                        }
                    }
                }

            }
        });

    }


    public void sendComment(String user, String comment, final long time, final int pos) {
        System.currentTimeMillis();
        final List<String> myComment = new ArrayList<>();
        myComment.add(myName);
        myComment.add(comment);
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(FRIEND_TABLE_NAME);
        parseQuery.addDescendingOrder(SUBMIT_TIME);
        parseQuery.whereEqualTo(USER_NAME, user);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (0 != objects.size()) {
                        for (int i = 0; i < objects.size(); i++) {
                            if (objects.get(i).getLong(SUBMIT_TIME) == time) {
                                parseObject = objects.get(i);
                                comments = parseObject.getList(COMMENT);
                                comments.add(myComment);
                                parseObject.put(COMMENT, comments);
                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            adapter.addComment(myComment, pos);
                                        } else {
                                            Toast.makeText(context, "e:" + e, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }

            }
        });

    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view=LayoutInflater.from(context).inflate(R.layout.dialog_find_send_text,null);
        final TextView textView= (TextView) view.findViewById(R.id.find_send_text_ed);
        builder.setView(view);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content=textView.getText().toString();
                if (null!=content&&content.length()>0){
                    sendFirstData(myName,content);
                }

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
        return false;
    }

    class HeadViewHolder {
        ImageView cover, head;
        TextView username;

        public HeadViewHolder(View v) {
            cover = (ImageView) v.findViewById(R.id.friend_circle_cover_iv);
            head = (ImageView) v.findViewById(R.id.friend_circle_head_iv);
            username = (TextView) v.findViewById(R.id.friend_circle_head_tv);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void sendFirstData(String user, String content) {
        parseObject = new ParseObject(FRIEND_TABLE_NAME);
        parseObject.put(USER_NAME, user);
        long time = System.currentTimeMillis();
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
                } else {
                    Toast.makeText(context, "e:" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
