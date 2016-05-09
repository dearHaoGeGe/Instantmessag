package com.my.instantmessag.entity;

import com.easemob.chat.MessageBody;
import com.easemob.chat.TextMessageBody;

/**
 * Created by dllo onDetailClick 16/3/1.
 */
public class ChatItemEntity {

    private String nikName, time,from,to,messageType,message;

    private TextMessageBody textMessageBody;

    public String getNikName() {
        return nikName;
    }

    public void setNikName(String nikName) {
        this.nikName = nikName;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public TextMessageBody getTextMessageBody() {
        return textMessageBody;
    }

    public void setTextMessageBody(TextMessageBody textMessageBody) {
        this.textMessageBody = textMessageBody;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
