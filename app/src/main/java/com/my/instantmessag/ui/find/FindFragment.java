package com.my.instantmessag.ui.find;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.my.instantmessag.R;
import com.my.instantmessag.activity.MainActivity;
import com.my.instantmessag.base.BaseFragment;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.mydb.PersonInfoCallBack;

import java.util.List;

/**
 * Created by dllo onDetailClick 16/2/29.
 */
public class FindFragment extends BaseFragment implements View.OnClickListener {

    private MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, null);
        view.findViewById(R.id.friend_circle).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.friend_circle:
                Intent intent=new Intent(context,FriendCircleActivity.class);
                startActivity(intent);
                mainActivity.jumpAnimationActivity();
                break;
        }
    }

}
