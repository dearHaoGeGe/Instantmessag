package com.my.instantmessag.ui.chat.v;

import com.my.instantmessag.entity.ChatItemEntity;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.mydb.MyData;

import java.util.ArrayList;

/**
 * Created by dllo onDetailClick 16/3/4.
 */
public interface IChatFragment {

    void showMessageList(ArrayList<MyData> data);

    void showToast();


}
