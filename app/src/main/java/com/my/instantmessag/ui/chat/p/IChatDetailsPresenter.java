package com.my.instantmessag.ui.chat.p;

import com.my.instantmessag.entity.ChatItemEntity;

import java.util.ArrayList;

/**
 * Created by dllo onDetailClick 16/3/4.
 */
public interface IChatDetailsPresenter {

    void startGetMessageData(String friendName);

    void sendTextMessage(String testMessage);

    void sendVoiceMessage(String voiceFilePath,int voiceTimeLength);

}
