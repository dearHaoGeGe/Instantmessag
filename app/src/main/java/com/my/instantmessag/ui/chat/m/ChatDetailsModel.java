package com.my.instantmessag.ui.chat.m;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.my.instantmessag.entity.ChatItemEntity;
import com.my.instantmessag.mydb.DBHelper;
import com.my.instantmessag.mydb.MyData;
import com.my.instantmessag.ui.chat.p.IChatDetailsPresenter;
import com.my.instantmessag.ui.chat.p.IGetMessageListener;

import java.util.ArrayList;

/**
 * Created by dllo onDetailClick 16/3/4.
 */
public class ChatDetailsModel implements IChatDetailsModel{

    private ArrayList<MyData> messageData;

    private IChatDetailsPresenter chatDetailsPresenter;

    @Override
    public void getMessageData(String friendName,IGetMessageListener getMessageListener) {

        //EMConversation conversation = EMChatManager.getInstance().getConversation("admin10");

        DBHelper dbHelper  = DBHelper.getInstance();
        ArrayList<MyData> messageData =  dbHelper.getMessage(friendName);

        if (null != messageData){

            getMessageListener.onGetMessageSucceed(messageData);

        }else {

            getMessageListener.onGetMessageError();
        }


    }




}
