package com.my.instantmessag.activity;

import com.my.instantmessag.entity.ContactsBean;

import java.util.List;

/**
 * Created by dllo onDetailClick 16/3/12.
 */
public interface GetPersonMessage {
    void getUserMessage(ContactsBean bean);
    void getFriendMessage(List<ContactsBean>beans);
}
