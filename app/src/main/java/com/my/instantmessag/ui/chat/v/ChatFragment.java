package com.my.instantmessag.ui.chat.v;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.my.instantmessag.R;
import com.my.instantmessag.base.BaseFragment;
import com.my.instantmessag.mydb.MyData;
import com.my.instantmessag.ui.chat.p.ChatPresenter;
import com.my.instantmessag.ui.chat.p.IChatPresenter;

import com.my.instantmessag.ui.chat.v.adapter.ChatAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


/**
 * Created by dllo onDetailClick 16/2/29.
 */
public class ChatFragment extends BaseFragment implements IChatFragment, AdapterView.OnItemClickListener {


    private ChatAdapter adapter;
    private ListView listView;

    private IChatPresenter chatPresenter;
    private ArrayList<Bitmap> headImageData;

    private ArrayList<MyData> data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        data = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.list_view_chat_fragment);

        listView.setOnItemClickListener(this);

        chatPresenter = new ChatPresenter(this);

        chatPresenter.setNativeMessageList();


        adapter = new ChatAdapter(this.data, headImageData, context);
        listView.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();

        /** EventBus 注册 */
        EventBus.getDefault().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void showMessageList(ArrayList<MyData> data) {

        this.data = data;

    }

    @Override
    public void showToast() {

        Toast.makeText(getActivity(), "还没有最近联系人", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getActivity(), ChatDetailsActivity.class);
        intent.putExtra("friendName", data.get(position).getName());
        startActivity(intent);

    }


    /**
     * EventBus 接收者
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MyData myData) {

        if (this.data.size() == 0) {

            data.add(myData);
            adapter.notifyDataSetChanged();

        } else {

            for (int i = 0; i < data.size(); i++) {

                if (data.get(i).getName().equals(myData.getName())) {

                    data.get(i).setName(myData.getName());
                    data.get(i).setBody(myData.getBody());

                    if (i != 0){

                        data.remove(i);
                        data.add(0, myData);
                    }

                    adapter.notifyDataSetChanged();

                    return;

                }

                if (i == data.size() - 1){

                    data.add(0, myData);

                    adapter.notifyDataSetChanged();

                }

            }

        }

    }
}
