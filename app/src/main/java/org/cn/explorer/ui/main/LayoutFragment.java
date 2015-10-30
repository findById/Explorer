package org.cn.explorer.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        if (context instanceof AppCompatActivity) {
            childFragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
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
        outState.putSerializable("stack", stack);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, null, false);
        initView(view);
        return view;
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
        if (args != null) {
            path = args.getString(Const.ROOT_PATH);
        }
    }

    private void initView(View view) {

    }

    private void initData() {

    }

    private void parseUri(String path) {
        Bundle bundle = new Bundle();
        bundle.putString("current.path", path);
        switchFragment(path, bundle);
        childFragment.setOnOpenFolderListener(this);
        stack.push(path);
    }

    private void switchFragment(String tag, Bundle data) {
        childFragmentTransaction = childFragmentManager.beginTransaction();
        Fragment tmp = childFragmentManager.findFragmentByTag(tag);
        if (childFragment == null) {
            childFragment = new ExpFragment();
            if (data != null) {
                childFragment.setArguments(data);
            }
            childFragmentTransaction.add(R.id.contentPanel, childFragment, tag);
            childFragmentTransaction.addToBackStack(null);
            childFragmentTransaction.commit();
        } else if (childFragment != tmp) {
            if (!childFragment.isDetached()) {
                childFragmentTransaction.detach(childFragment);
            }
            if (tmp == null) {
                childFragment = new ExpFragment();
            } else {
                childFragment = (ExpFragment) tmp;
            }
            if (childFragment.isDetached()) {
                childFragmentTransaction.attach(childFragment);
            } else if (!childFragment.isAdded()) {
                if (data != null) {
                    childFragment.setArguments(data);
                }
                childFragmentTransaction.add(R.id.contentPanel, childFragment, tag);
                childFragmentTransaction.addToBackStack(null);
            }
            childFragmentTransaction.commit();
        }
    }

    public synchronized boolean onBackPressed() {
        if (stack.size() > 1) {
            childFragmentTransaction = childFragmentManager.beginTransaction();
            if (childFragment != null) {
                childFragmentTransaction.detach(childFragment);
                // childFragmentTransaction.remove(childFragment);
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
