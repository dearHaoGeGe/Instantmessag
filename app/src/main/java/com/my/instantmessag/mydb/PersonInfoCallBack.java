package com.my.instantmessag.mydb;

import com.my.instantmessag.entity.ContactsBean;

import java.util.List;

/**
 * Created by dllo onDetailClick 16/3/8.
 */
public interface PersonInfoCallBack extends PersonInfo{
    void getBean(ContactsBean bean);
    void getBeans(List<ContactsBean> beans);
}
