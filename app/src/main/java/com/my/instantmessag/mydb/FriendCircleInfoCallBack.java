package com.my.instantmessag.mydb;

import com.my.instantmessag.entity.FriendCircleBean;

import java.util.List;
import java.util.Map;

/**
 * Created by dllo onDetailClick 16/3/8.
 */
public interface FriendCircleInfoCallBack extends FriendCircleInfo{
    void getBean(List<FriendCircleBean> beans);
}
