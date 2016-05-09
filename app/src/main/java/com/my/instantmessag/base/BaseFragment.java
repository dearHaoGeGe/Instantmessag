package com.my.instantmessag.base;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by dllo onDetailClick 16/2/29.
 */
public class BaseFragment extends Fragment {
    public Context context;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }
}
