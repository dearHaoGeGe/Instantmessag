package com.my.instantmessag.ui.chat.p;

import com.my.instantmessag.entity.ChatItemEntity;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.mydb.MyData;
import com.my.instantmessag.ui.chat.m.ChatModel;
import com.my.instantmessag.ui.chat.m.IChatMode;
import com.my.instantmessag.ui.chat.v.ChatFragment;
import com.my.instantmessag.ui.chat.v.IChatFragment;

import java.util.ArrayList;

/**
 * Created by dllo onDetailClick 16/3/1.
 */
public class ChatPresenter implements IChatPresenter {

    private IChatFragment chat;
    private IChatMode chatModel;

    public ChatPresenter(){

    }

    public ChatPresenter(IChatFragment chat){

        this.chat = chat;
        chatModel = new ChatModel();
    }

    @Override
    public void setNativeMessageList(){

        chatModel.getNativeMessageList(new IGetMessageListener() {
            @Override
            public void onGetMessageSucceed(ArrayList<MyData> data) {

                chat.showMessageList(data);

            }

            @Override
            public void onGetMessageError() {

                chat.showToast();
            }
        });
    }




}
