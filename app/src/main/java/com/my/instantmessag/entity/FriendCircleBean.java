package com.my.instantmessag.entity;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by dllo onDetailClick 16/3/9.
 */
public class FriendCircleBean {
    private String username;
    private List<String> friendName;
    private String content;
    private long time;
    private Bitmap img;
    private List<List<String>> comment;
    private int good;
    public FriendCircleBean() {
    }

    public FriendCircleBean(String content, int good, List<List<String>> comment, Bitmap img, long time, String username,List<String> friendName) {
        this.content = content;
        this.good = good;
        this.comment = comment;
        this.img = img;
        this.time = time;
        this.username = username;
        this.friendName=friendName;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getFriendName() {
        return friendName;
    }

    public void setFriendName(List<String> friendName) {
        this.friendName = friendName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public List<List<String>> getComment() {
        return comment;
    }

    public void setComment(List<List<String>> comment) {
        this.comment = comment;
    }
}
