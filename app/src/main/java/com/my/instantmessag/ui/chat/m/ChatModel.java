package com.my.instantmessag.ui.chat.m;

import android.graphics.Bitmap;
import android.util.Pair;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.exceptions.EaseMobException;
import com.my.instantmessag.entity.ChatItemEntity;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.mydb.DBHelper;
import com.my.instantmessag.mydb.MyData;
import com.my.instantmessag.ui.chat.p.ChatPresenter;
import com.my.instantmessag.ui.chat.p.IChatPresenter;
import com.my.instantmessag.ui.chat.p.IGetMessageListener;
import com.my.instantmessag.ui.chat.v.IChatFragment;
import com.my.instantmessag.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by dllo onDetailClick 16/3/1.
 */
public class ChatModel implements IChatMode {

    private ArrayList<Bitmap> headImageData;

    private IChatPresenter chatPresenter;



    public ChatModel(){

    }

    @Override
    public void getNativeMessageList(IGetMessageListener getMessageListener) {

        DBHelper dbHelper = DBHelper.getInstance();
        ArrayList<MyData> data =  dbHelper.getContactsBeans();

        if (data.size() != 0){

            getMessageListener.onGetMessageSucceed(data);

        }else {

            getMessageListener.onGetMessageError();
        }


    }

    /**
     * 获取会话列表
     *
     * @return
     */
    public ArrayList<EMConversation> loadConversationList() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();

        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        // 同步
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            //sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

}
