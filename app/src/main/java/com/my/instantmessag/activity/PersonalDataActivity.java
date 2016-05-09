package com.my.instantmessag.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.my.instantmessag.R;
import com.my.instantmessag.base.BaseActivity;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.mydb.DBHelper;
import com.my.instantmessag.mydb.PersonInfo;
import com.my.instantmessag.mydb.PersonInfoCallBack;
import com.my.instantmessag.utils.CreateQRCode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.wq.photo.widget.PickConfig;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 个人资料页面
 *
 * Created by dllo on 16/3/11.
 */
public class PersonalDataActivity extends BaseActivity implements View.OnClickListener, PersonInfo {

    private TextView tv_personal_data_username, tv_personal_data_nickname, tv_personal_data_sex;
    private ImageView iv_personal_data_head, iv_personal_data_QRcode, iv_personal_data_nickname, iv_back_personal_data, iv_personal_data_sex;
    private Button btn_personal_data_OK;
    private String nickname = null;
    private String sex = "其他";
    private String currentUser = EMChatManager.getInstance().getCurrentUser();  //获取当前用户名
    private ParseObject parseObject;
    private ContactsBean data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        DBHelper.getPersonInfo(currentUser, new PersonInfoCallBack() {
            @Override
            public void getBean(ContactsBean bean) {
                data = bean;
                initFindViewById();
            }

            @Override
            public void getBeans(List<ContactsBean> beans) {

            }
        });

    }

    private void initFindViewById() {
        tv_personal_data_username = (TextView) findViewById(R.id.tv_personal_data_username);
        tv_personal_data_nickname = (TextView) findViewById(R.id.tv_personal_data_nickname);
        iv_personal_data_head = (ImageView) findViewById(R.id.iv_personal_data_head);
        iv_personal_data_QRcode = (ImageView) findViewById(R.id.iv_personal_data_QRcode);
        iv_personal_data_nickname = (ImageView) findViewById(R.id.iv_personal_data_nickname);
        iv_back_personal_data = (ImageView) findViewById(R.id.iv_back_personal_data);
        tv_personal_data_sex = (TextView) findViewById(R.id.tv_personal_data_sex);
        iv_personal_data_sex = (ImageView) findViewById(R.id.iv_personal_data_sex);
        btn_personal_data_OK = (Button) findViewById(R.id.btn_personal_data_OK);

        iv_personal_data_head.setOnClickListener(this);
        iv_personal_data_QRcode.setOnClickListener(this);
        tv_personal_data_nickname.setOnClickListener(this);
        iv_personal_data_nickname.setOnClickListener(this);
        iv_back_personal_data.setOnClickListener(this);
        tv_personal_data_sex.setOnClickListener(this);
        iv_personal_data_sex.setOnClickListener(this);
        btn_personal_data_OK.setOnClickListener(this);

        tv_personal_data_username.setText(currentUser);
        tv_personal_data_nickname.setText(data.getNickName());
        tv_personal_data_sex.setText(data.getSex());
        if (null!=data.getHeadImage()){
            iv_personal_data_head.setImageBitmap(data.getHeadImage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_personal_data:    //左上角的返回键
                finish();
                jumpAnimationActivity();
                break;

            case R.id.iv_personal_data_head://头像
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
                        .setPickRequestCode(10086)
                        .pickMode(chose_mode).build();
                break;

            case R.id.iv_personal_data_QRcode:  //生成二维码
                showQRCodeDialog();
                break;

            case R.id.tv_personal_data_nickname:    //设置昵称
                showNickNameDialog();
                break;

            case R.id.iv_personal_data_nickname:    //设置昵称
                showNickNameDialog();
                break;

            case R.id.tv_personal_data_sex:      //设置性别
                showSexSingleDialog();
                break;

            case R.id.iv_personal_data_sex:      //设置性别
                showSexSingleDialog();
                break;

            case R.id.btn_personal_data_OK:
                savePersonInfo(currentUser, data.getHeadImage(), data.getNickName(), data.getSex());
                //确认
                Toast.makeText(PersonalDataActivity.this, "已经确认", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    //选取图片后的回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 10086) {
            ArrayList<String> paths = data.getStringArrayListExtra("data");
            for (int i = 0; i < paths.size(); i++) {
                Bitmap bitmap = getDiskBitmap(paths.get(i));
                // TODO: 16/3/15 读到图片
                iv_personal_data_head.setImageBitmap(bitmap);
                this.data.setHeadImage(bitmap);
            }

        }
    }

    public void savePersonInfo(String user, final Bitmap bitmap, final String nickname, final String sex) {
        final ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(PERSON_TABLE_NAME);
        parseQuery.whereEqualTo(USER, user);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (0 != objects.size()) {
                        for (int i = 0; i < objects.size(); i++) {
                            parseObject = objects.get(i);
                            if (null != bitmap) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] data = stream.toByteArray();
                                long time = System.currentTimeMillis();
                                ParseFile pf = new ParseFile("IMG_" + time + ".png", data);
                                parseObject.put(HEAD_IMG, pf);
                            }
                            if (null != nickname) {
                                parseObject.put(NICK_NAME, nickname);
                            }
                            if (null != sex) {
                                parseObject.put(SEX, sex);
                            }
                            parseObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // TODO: 16/3/15 成功的回调
                                        Toast.makeText(PersonalDataActivity.this, "成功了", Toast.LENGTH_SHORT).show();
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        jumpAnimationActivity();
        return super.onKeyDown(keyCode, event);

    }

    /**
     * 显示设置昵称的Dialog
     */
    private void showNickNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.weixin);
        builder.setTitle("设置昵称");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_personal_data_activity, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_dialog);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nickname = editText.getText().toString();
                if (!nickname.equals("")) {     //如果输入的不是空的就设置昵称
                    tv_personal_data_nickname.setText(nickname);
                    data.setNickName(nickname);
                    nickname = "";
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    /**
     * 生成二维码的Dialog
     */
    private void showQRCodeDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_create_qrcode_personal_data_activity, null);
        ImageView iv_head_QRCode_Dialog = (ImageView) view.findViewById(R.id.iv_head_QRCode_Dialog);
        final TextView tv_username_QRCode_Dialog = (TextView) view.findViewById(R.id.tv_username_QRCode_Dialog);
        final AppCompatImageView iv_QRCode_Dialog = (AppCompatImageView) view.findViewById(R.id.iv_QRCode_Dialog);
        CreateQRCode.createQRImage(currentUser, "test", new CreateQRCode.OnCreateQRListener() {
            @Override
            public void onSuccess(Bitmap qrImage) {
                iv_QRCode_Dialog.setImageBitmap(qrImage);
                tv_username_QRCode_Dialog.setText(currentUser);
                builder.setView(view);
                builder.show();
            }
        });
    }

    /**
     * 性别选择的单选框Dialog
     */
    private void showSexSingleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.weixin);
        builder.setTitle("选择性别");
        final String sex[] = {"男", "女", "其他"};
        final int choice[] = {0};
        builder.setSingleChoiceItems(sex, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choice[0] = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(PersonalDataActivity.this, "" + sex[choice[0]], Toast.LENGTH_SHORT).show();
                tv_personal_data_sex.setText(sex[choice[0]]);
                data.setSex(sex[choice[0]]);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
