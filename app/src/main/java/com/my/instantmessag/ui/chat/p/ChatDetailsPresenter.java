package com.my.instantmessag.ui.chat.p;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.my.instantmessag.mydb.MyData;
import com.my.instantmessag.ui.chat.v.ChatDetailsActivity;
import com.my.instantmessag.ui.chat.m.ChatDetailsModel;
import com.my.instantmessag.ui.chat.m.IChatDetailsModel;

import com.my.instantmessag.ui.chat.v.IChatDetailsActivity;

import java.util.ArrayList;


/**
 * Created by dllo onDetailClick 16/3/4.
 */
public class ChatDetailsPresenter implements IChatDetailsPresenter{

    private IChatDetailsActivity chatDetailsActivity;
    private IChatDetailsModel chatDetailsModel;
    private String friendName;

    public ChatDetailsPresenter(ChatDetailsActivity chatDetailsActivity){

        this.chatDetailsActivity = chatDetailsActivity;

        chatDetailsModel = new ChatDetailsModel();

    }

    @Override
    public void startGetMessageData(String friendName) {

        this.friendName = friendName;

        chatDetailsModel.getMessageData(this.friendName,new IGetMessageListener() {

            @Override
            public void onGetMessageSucceed(ArrayList<MyData> data) {

                chatDetailsActivity.getData(data);
            }

            @Override
            public void onGetMessageError() {

            }
        });


    }

    @Override
    public void sendTextMessage(String testMessage) {

        EMMessage message = EMMessage.createTxtSendMessage(testMessage, this.friendName);
        sendMessage(message);
    }

    @Override
    public void sendVoiceMessage(String voiceFilePath,int voiceTimeLength) {

        EMMessage message = EMMessage.createVoiceSendMessage(voiceFilePath, voiceTimeLength,this.friendName);
        sendMessage(message);
    }


    private void sendMessage(EMMessage message){

        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }





}
