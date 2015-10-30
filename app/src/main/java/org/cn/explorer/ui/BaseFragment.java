package org.cn.explorer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.cn.explorer.common.DataManager;

/**
 * Created by chenning on 2015/10/10.
 */
public class BaseFragment extends Fragment {

    protected DataManager dataManager;

    public BaseFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataManager = DataManager.getInstance(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public synchronized boolean onBackPressed() {
        return false;
    }

}
