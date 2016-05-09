package com.my.instantmessag.ui.setting;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.my.instantmessag.R;
import com.my.instantmessag.activity.ContactsBlackListActivity;
import com.my.instantmessag.activity.LoginActivity;
import com.my.instantmessag.activity.MainActivity;
import com.my.instantmessag.activity.PersonalDataActivity;
import com.my.instantmessag.base.BaseFragment;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.widget.CheckSwitchButton;

import org.jetbrains.annotations.Nullable;

/**
 * 设置页面
 * <p/>
 * Created by YJH onDetailClick 16/3/4.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private MainActivity mainActivity;
    private CheckSwitchButton checkSwitchButton_TiXing, checkSwitchButton_voice, checkSwitchButton_shock,
            checkSwitchButton_UseSpeaker, checkSwitchButton_Leave, checkSwitchButton_DelData, checkSwitchButton_Agree;
    private LinearLayout linearLayout_BlackList, linearLayout_PersonData, linearLayout_doctor, linearLayout_ios, linearLayout_logout;
    private EMChatOptions chatOptions;
    private ContactsBean currentUser = new ContactsBean(EMChatManager.getInstance().getCurrentUser());     //记录当前登录账号的用户名
    private OnSettingListener mListener;

    public interface OnSettingListener {
        void onSetting(int a);
    }

//    /**
//     * 先执行onAttach(Activity activity)
//     * 后执行onAttach(Context context)
//     *
//     * @param activity
//     */
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        mListener = (OnSettingListener) activity;
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
        mListener = (OnSettingListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.fragment_setting, null);
        initFindViewById(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListener.onSetting(10);

    }

    private void initFindViewById(View view) {
        checkSwitchButton_TiXing = (CheckSwitchButton) view.findViewById(R.id.checkSwitchButton_TiXing);
        checkSwitchButton_voice = (CheckSwitchButton) view.findViewById(R.id.checkSwitchButton_voice);
        checkSwitchButton_shock = (CheckSwitchButton) view.findViewById(R.id.checkSwitchButton_shock);
        checkSwitchButton_UseSpeaker = (CheckSwitchButton) view.findViewById(R.id.checkSwitchButton_UseSpeaker);
        checkSwitchButton_Leave = (CheckSwitchButton) view.findViewById(R.id.checkSwitchButton_Leave);
        checkSwitchButton_DelData = (CheckSwitchButton) view.findViewById(R.id.checkSwitchButton_DelData);
        checkSwitchButton_Agree = (CheckSwitchButton) view.findViewById(R.id.checkSwitchButton_Agree);
        linearLayout_BlackList = (LinearLayout) view.findViewById(R.id.linearLayout_BlackList);
        linearLayout_PersonData = (LinearLayout) view.findViewById(R.id.linearLayout_PersonData);
        linearLayout_doctor = (LinearLayout) view.findViewById(R.id.linearLayout_doctor);
        linearLayout_ios = (LinearLayout) view.findViewById(R.id.linearLayout_ios);
        linearLayout_logout = (LinearLayout) view.findViewById(R.id.linearLayout_logout);

        linearLayout_BlackList.setOnClickListener(this);
        linearLayout_PersonData.setOnClickListener(this);
        linearLayout_doctor.setOnClickListener(this);
        linearLayout_ios.setOnClickListener(this);
        linearLayout_logout.setOnClickListener(this);

        // 首先获取EMChatOptions
        chatOptions = EMChatManager.getInstance().getChatOptions();

        readSettingInfo(currentUser.getUserName());    //进来的时候首先读取上次存进来的设置信息

        checkSwitchButton_TiXing.setOnCheckedChangeListener(this);
        checkSwitchButton_voice.setOnCheckedChangeListener(this);
        checkSwitchButton_shock.setOnCheckedChangeListener(this);
        checkSwitchButton_UseSpeaker.setOnCheckedChangeListener(this);
        checkSwitchButton_Leave.setOnCheckedChangeListener(this);
        checkSwitchButton_DelData.setOnCheckedChangeListener(this);
        checkSwitchButton_Agree.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayout_BlackList:
                //Toast.makeText(mainActivity, "通讯录黑名单", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mainActivity, ContactsBlackListActivity.class));
                mainActivity.jumpAnimationActivity();
                break;

            case R.id.linearLayout_PersonData:
                //Toast.makeText(mainActivity, "个人资料", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mainActivity, PersonalDataActivity.class));
                mainActivity.jumpAnimationActivity();
                break;

            case R.id.linearLayout_doctor:
                Toast.makeText(mainActivity, "诊断", Toast.LENGTH_SHORT).show();
                break;

            case R.id.linearLayout_ios:
                Toast.makeText(mainActivity, "IOS离线推送昵称", Toast.LENGTH_SHORT).show();
                break;

            case R.id.linearLayout_logout:
                Toast.makeText(mainActivity, "退出当前账号", Toast.LENGTH_SHORT).show();
                final Dialog dialog = new ProgressDialog(context);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("正在退出");
                dialog.show();
                //此方法为异步方法
                EMChatManager.getInstance().logout(new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        mainActivity.finish();

                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(int code, String message) {
                        // TODO Auto-generated method stub
                        Toast.makeText(mainActivity, "" + message, Toast.LENGTH_SHORT).show();

                    }
                });
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.checkSwitchButton_TiXing:
                if (!isChecked) {   //如果开关是关,把声音和震动都关了并且这两个开关锁上,
                    checkSwitchButton_voice.setChecked(false);
                    checkSwitchButton_shock.setChecked(false);
                    checkSwitchButton_voice.setEnabled(false);
                    checkSwitchButton_shock.setEnabled(false);
                    saveSettingInfo(currentUser.getUserName());
                } else {
                    checkSwitchButton_voice.setEnabled(true);
                    checkSwitchButton_shock.setEnabled(true);
                    checkSwitchButton_voice.setChecked(true);
                    checkSwitchButton_shock.setChecked(true);
                    saveSettingInfo(currentUser.getUserName());
                }
                break;

            case R.id.checkSwitchButton_voice:
                saveSettingInfo(currentUser.getUserName());
                break;

            case R.id.checkSwitchButton_shock:
                saveSettingInfo(currentUser.getUserName());
                break;

            case R.id.checkSwitchButton_UseSpeaker:
                saveSettingInfo(currentUser.getUserName());
                break;

            case R.id.checkSwitchButton_Leave:
                saveSettingInfo(currentUser.getUserName());
                break;

            case R.id.checkSwitchButton_DelData:
                saveSettingInfo(currentUser.getUserName());
                break;

            case R.id.checkSwitchButton_Agree:
                saveSettingInfo(currentUser.getUserName());
                break;
        }
    }

    /**
     * 保存账号设置信息
     *
     * @param userName
     */
    private void saveSettingInfo(String userName) {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("SettingInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(userName + "_接收新消息通知", checkSwitchButton_TiXing.isChecked());
        chatOptions.setNotifyBySoundAndVibrate(checkSwitchButton_TiXing.isChecked());

        editor.putBoolean(userName + "_声音", checkSwitchButton_voice.isChecked());
        chatOptions.setNoticeBySound(checkSwitchButton_voice.isChecked());

        editor.putBoolean(userName + "_震动", checkSwitchButton_shock.isChecked());
        chatOptions.setNoticedByVibrate(checkSwitchButton_shock.isChecked());

        editor.putBoolean(userName + "_使用扬声器播放语音", checkSwitchButton_UseSpeaker.isChecked());
        chatOptions.setUseSpeaker(checkSwitchButton_UseSpeaker.isChecked());

        editor.putBoolean(userName + "_允许聊天室群主离开", checkSwitchButton_Leave.isChecked());
        editor.putBoolean(userName + "_退出群组时删除聊天数据", checkSwitchButton_DelData.isChecked());
        editor.putBoolean(userName + "_自动同意群组加群邀请", checkSwitchButton_Agree.isChecked());
        editor.commit();

        mainActivity.saveSettingInfo(userName, checkSwitchButton_TiXing.isChecked(), checkSwitchButton_voice.isChecked(),
                checkSwitchButton_shock.isChecked(), checkSwitchButton_UseSpeaker.isChecked());
    }

    /**
     * 读取账号设置信息
     *
     * @param userName
     */
    private void readSettingInfo(String userName) {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("SettingInfo", Context.MODE_PRIVATE);
        checkSwitchButton_TiXing.setChecked(sharedPreferences.getBoolean(userName + "_接收新消息通知", true));
        /**设置是否启用新消息提醒(打开或者关闭消息声音和震动提示)*/
        chatOptions.setNotifyBySoundAndVibrate(sharedPreferences.getBoolean(userName + "_接收新消息通知", true));

        if (!checkSwitchButton_TiXing.isChecked()) {
            checkSwitchButton_voice.setChecked(false);
            /**设置是否启用新消息声音提醒*/
            chatOptions.setNoticeBySound(false);

            checkSwitchButton_shock.setChecked(false);
            /**设置是否启用新消息震动提醒*/
            chatOptions.setNoticedByVibrate(false);

            checkSwitchButton_voice.setEnabled(false);
            checkSwitchButton_shock.setEnabled(false);
        } else {
            checkSwitchButton_voice.setChecked(sharedPreferences.getBoolean(userName + "_声音", true));
            /**设置是否启用新消息声音提醒*/
            chatOptions.setNoticeBySound(sharedPreferences.getBoolean(userName + "_声音", true));

            checkSwitchButton_shock.setChecked(sharedPreferences.getBoolean(userName + "_震动", true));
            /**设置是否启用新消息震动提醒*/
            chatOptions.setNoticedByVibrate(sharedPreferences.getBoolean(userName + "_震动", true));
        }

        checkSwitchButton_UseSpeaker.setChecked(sharedPreferences.getBoolean(userName + "_使用扬声器播放语音", true));
        /**设置语音消息播放是否设置为扬声器播放*/
        chatOptions.setUseSpeaker(sharedPreferences.getBoolean(userName + "_使用扬声器播放语音", true));

        checkSwitchButton_Leave.setChecked(sharedPreferences.getBoolean(userName + "_允许聊天室群主离开", true));
        checkSwitchButton_DelData.setChecked(sharedPreferences.getBoolean(userName + "_退出群组时删除聊天数据", true));

        checkSwitchButton_Agree.setChecked(sharedPreferences.getBoolean(userName + "_自动同意群组加群邀请", true));
    }

}
