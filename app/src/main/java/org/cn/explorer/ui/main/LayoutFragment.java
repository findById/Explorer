package org.cn.explorer.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cn.explorer.MainActivity;
import org.cn.explorer.R;
import org.cn.explorer.common.Const;
import org.cn.explorer.ui.BaseFragment;

import java.util.Stack;

/**
 * Created by chenning on 2015/10/29.
 */
public class LayoutFragment extends BaseFragment implements ExpFragment.OnOpenFolderListener {

    private FragmentManager childFragmentManager;
    private FragmentTransaction childFragmentTransaction;

    private String path;

    private Stack<String> stack = new Stack<>();

    private ExpFragment childFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            childFragmentManager = ((MainActivity) context).getSupportFragmentManager();
            childFragmentTransaction = childFragmentManager.beginTransaction();
        }
    }

    @Override
    public void onDetach() {
        stack.clear();
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            savedInstanceState = getArguments();
        }
        if (savedInstanceState != null) {
            path = savedInstanceState.getString(Const.ROOT_PATH);
            Stack<String> tmp = (Stack<String>) savedInstanceState.getSerializable("stack");
            if (tmp != null && !tmp.isEmpty()) {
                stack = tmp;
            }
            if (stack.isEmpty()) {
                stack.push(path);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Const.ROOT_PATH, path);
        outState.putSerializable("stack", stack);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!stack.isEmpty()) {
            parseUri(stack.pop());
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    private void parseUri(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(Const.ROOT_PATH, path);
        switchFragment(path, bundle);
        childFragment.setOnOpenFolderListener(this);
        stack.push(path);
    }

    private synchronized void switchFragment(String tag, Bundle data) {
        childFragmentTransaction = childFragmentManager.beginTransaction();
        ExpFragment tmp = (ExpFragment) childFragmentManager.findFragmentByTag(tag);
        if (tmp == null) {
            tmp = new ExpFragment();
            if (data != null) {
                tmp.setArguments(data);
            }
            childFragmentTransaction.add(R.id.contentPanel, tmp, tag);
            childFragmentTransaction.addToBackStack(null);
            childFragmentTransaction.commit();
        } else {
            if (childFragment != null) {
                childFragmentTransaction.detach(childFragment);
            }
            childFragmentTransaction.attach(tmp);
            childFragmentTransaction.commit();
        }
        childFragment = tmp;
    }

    public synchronized boolean onBackPressed() {
        if (stack.size() > 1) {
            childFragmentTransaction = childFragmentManager.beginTransaction();
            if (childFragment != null) {
                childFragmentTransaction.detach(childFragment);
                childFragmentTransaction.remove(childFragment);
            }
            stack.pop();
            childFragment = (ExpFragment) childFragmentManager.findFragmentByTag(stack.peek());
            childFragmentTransaction.attach(childFragment);
            childFragmentTransaction.commit();
            return true;
        }
        return false;
    }

    @Override
    public void onOpenFolder(String path) {
        parseUri(path);
    }

    public void reload() {
        if (childFragment != null) {
            childFragment.reload();
        }
    }

    public void clearSelected() {
        if (childFragment != null) {
            childFragment.clearSelected();
        }
    }
}
