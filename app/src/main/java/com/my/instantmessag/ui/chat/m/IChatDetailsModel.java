package com.my.instantmessag.ui.chat.m;

import com.my.instantmessag.entity.ChatItemEntity;
import com.my.instantmessag.ui.chat.p.IGetMessageListener;

import java.util.ArrayList;

/**
 * Created by dllo onDetailClick 16/3/4.
 */
public interface IChatDetailsModel {

    void getMessageData(String friendName,IGetMessageListener getMessageListener);



}
