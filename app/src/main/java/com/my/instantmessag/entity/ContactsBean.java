package com.my.instantmessag.entity;

import android.graphics.Bitmap;

import java.io.Serializable;


/**
 * 通讯录中的好友的Bean
 * <p>
 * Created by YJH onDetailClick 16/3/3.
 */
public class ContactsBean {
    private String userName;    //用户名
    private String firstLetter; //首字母,在索引条组件中使用
    private Bitmap headImage;   //头像url
    private String sex;            //0女, 1男
    private String nickName;    //昵称
    private String time;        //最后一条的消息的时间
    private String body;        //最后一条消息
    private String name;        //联系人名
    private long id;
    private Bitmap cover;    //封面

    public ContactsBean() {
    }

    public ContactsBean(String userName, Bitmap headImage, String sex, String nickName, Bitmap cover) {
        this.userName = userName;
        this.headImage = headImage;
        this.sex = sex;
        this.nickName = nickName;
        this.cover = cover;
    }

    public long getId() {
        return id;

    }
    public void setId(long id) {
        this.id = id;
    }


    public ContactsBean(String userName) {
        this.userName = userName;
    }

    public ContactsBean(String name, String massage, String time) {
        this.name = name;
        this.body = massage;
        this.time = time;
    }

    public ContactsBean(String userName, String firstLetter, Bitmap headImage, String sex, String nickName) {
        this.userName = userName;
        this.firstLetter = firstLetter;
        this.headImage = headImage;
        this.sex = sex;
        this.nickName = nickName;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public Bitmap getHeadImage() {
        return headImage;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public void setHeadImage(Bitmap headImage) {
        this.headImage = headImage;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
