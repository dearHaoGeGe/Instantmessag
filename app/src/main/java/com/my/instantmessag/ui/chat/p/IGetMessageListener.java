package com.my.instantmessag.ui.chat.p;

import com.my.instantmessag.entity.ChatItemEntity;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.mydb.MyData;

import java.util.ArrayList;

/**
 * Created by dllo onDetailClick 16/3/4.
 */
public interface IGetMessageListener {

    void onGetMessageSucceed(ArrayList<MyData> data);

    void onGetMessageError();
}
