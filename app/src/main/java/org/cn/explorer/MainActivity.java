package org.cn.explorer;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.cn.explorer.common.AppConfig;
import org.cn.explorer.common.Const;
import org.cn.explorer.listener.AppDialogListener;
import org.cn.explorer.ui.BaseActivity;
import org.cn.explorer.ui.main.ExpFragment;
import org.cn.explorer.ui.main.LayoutFragment;
import org.cn.explorer.utils.DialogUtil;
import org.cn.explorer.utils.FileUtil;
import org.cn.utils.AppUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, ExpFragment.OnOpenFolderListener {

    private LayoutFragment currentFragment;
    private Map<String, LayoutFragment> fragmentMap = new HashMap<>();

    private Toolbar mToolbar;
    private View loading;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.mToolbar = toolbar;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        init();
        initView();
        initData();
    }

    private void init() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
    }

    private void initView() {
        loading = this.findViewById(R.id.loading);

        AppConfig.currentFilePath = new File(Environment.getExternalStorageDirectory() + File.separator);

        Bundle bundle = new Bundle();
        bundle.putString(Const.ROOT_PATH, "" + AppConfig.currentFilePath);
        currentFragment = getFragment("EXPLORER_DISK", bundle);
        onChangeFragment("EXPLORER_DISK");

    }

    private void initData() {
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment.onBackPressed()) {
                return;
            }
            super.supportFinishAfterTransition();
            // super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //
        AppUtil.setOptionalIconsVisible(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_new_folder:
                final View view = View.inflate(this, R.layout.layout_edit_text, null);
                final EditText editText = (EditText) view.findViewById(R.id.edit_text_view);
                editText.setText(getString(R.string.action_menu_new_folder));
                editText.selectAll();
                AppUtil.showKeyboard(this, editText);
                DialogUtil.alertDialog(this, getString(R.string.action_menu_new_folder), "", getString(R.string.action_cancel), getString(R.string.action_confirm), view, new AppDialogListener() {
                    @Override
                    public void onPositiveButtonClickListener() {
                        super.onPositiveButtonClickListener();
                        try {
                            String result = editText.getText().toString();
                            if (TextUtils.isEmpty(result)) {
                                Snackbar.make(loading, "input content please.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                return;
                            }
                            if (FileUtil.createFolder(AppConfig.currentFilePath + File.separator + result)) {
                                currentFragment.reload();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.action_sort:
                item.getSubMenu().getItem(0).setChecked(false);
                item.getSubMenu().getItem(1).setChecked(false);
                item.getSubMenu().getItem(2).setChecked(false);
                item.getSubMenu().getItem(3).setChecked(false);
                switch (AppConfig.sortMode) {
                    case 0:
                        item.getSubMenu().getItem(0).setChecked(true);
                        break;
                    case 1:
                        item.getSubMenu().getItem(1).setChecked(true);
                        break;
                    case 2:
                        item.getSubMenu().getItem(2).setChecked(true);
                        break;
                    case 3:
                        item.getSubMenu().getItem(3).setChecked(true);
                        break;
                    default:
                        break;
                }
                break;
            case R.id.action_sort_zero:
                AppConfig.sortMode = 0;
                item.setChecked(true);
                currentFragment.reload();
                break;
            case R.id.action_sort_one:
                AppConfig.sortMode = 1;
                item.setChecked(true);
                currentFragment.reload();
                break;
            case R.id.action_sort_two:
                AppConfig.sortMode = 2;
                item.setChecked(true);
                currentFragment.reload();
                break;
            case R.id.action_sort_three:
                AppConfig.sortMode = 3;
                item.setChecked(true);
                currentFragment.reload();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // close drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (currentFragment != null) {
            currentFragment.clearSelected();
        }

        String tag = "";
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_store:
                tag = "EXPLORER_STORE";
                AppConfig.currentFilePath = new File("/");
                break;
            case R.id.nav_disk:
                tag = "EXPLORER_DISK";
                AppConfig.currentFilePath = new File(Environment.getExternalStorageDirectory() + File.separator);
                break;
            case R.id.nav_camera:
                tag = "EXPLORER_CAMERA";
                AppConfig.currentFilePath = new File(Environment.getExternalStorageDirectory() + File.separator, "DCIM");
                break;
            case R.id.nav_download:
                tag = "EXPLORER_DOWNLOAD";
                AppConfig.currentFilePath = new File(Environment.getExternalStorageDirectory() + File.separator, "Download");
                break;
            case R.id.nav_music:
                tag = "EXPLORER_MUSIC";
                AppConfig.currentFilePath = new File(Environment.getExternalStorageDirectory() + File.separator, "Music");
                break;
            case R.id.nav_pictures:
                tag = "EXPLORER_PICTURE";
                AppConfig.currentFilePath = new File(Environment.getExternalStorageDirectory() + File.separator, "Pictures");
                break;
            case R.id.nav_movies:
                tag = "EXPLORER_MOVIES";
                AppConfig.currentFilePath = new File(Environment.getExternalStorageDirectory() + File.separator, "Movies");
                break;
            case R.id.nav_manage:
                return true;
            case R.id.nav_feedback:
                return true;
            default:
                // ignore
                return true;
        }

        Bundle bundle = new Bundle();
        bundle.putString(Const.ROOT_PATH, AppConfig.currentFilePath.getPath());
        currentFragment = getFragment(tag, bundle);
        onChangeFragment(tag);

        return true;
    }

    private LayoutFragment getFragment(String key, Bundle bundle) {
        LayoutFragment fragment = fragmentMap.get(key);
        if (fragment == null) {
            fragment = new LayoutFragment();
            add(fragment, key, bundle);
            fragmentMap.put(key, fragment);
        }
        return fragment;
    }

    String mCurrentTag;

    private void onChangeFragment(String tag) {
        if (tag == null) {
            return;
        }
        if (mCurrentTag != null && mCurrentTag.equals(tag)) {
            return;
        }
        mCurrentTag = tag;
        Fragment tmp = mFragmentManager.findFragmentByTag(tag);
        if (tmp == null) {
            return;
        }
        List<Fragment> list = mFragmentManager.getFragments();
        if (list != null && list.size() > 0) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                mFragmentTransaction.detach(list.get(i));
            }
            mFragmentTransaction.commit();
        }
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.attach(tmp).commit();
    }

    private FragmentTransaction add(Fragment fragment, String tag, Bundle data) {
        List<Fragment> list = mFragmentManager.getFragments();
        if (list != null && list.size() > 0) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
            for (int i = 0; i < list.size(); i++) {
                mFragmentTransaction.detach(list.get(i));
            }
            mFragmentTransaction.commit();
        }
        Fragment tmp = mFragmentManager.findFragmentByTag(tag);
        mFragmentTransaction = mFragmentManager.beginTransaction();
        if (tmp != null) {
            mFragmentTransaction.remove(tmp);
        }
        fragment.setArguments(data);
        mFragmentTransaction.add(R.id.contentPanel, fragment, tag);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
        mFragmentManager.executePendingTransactions();
        return mFragmentTransaction;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onOpenFolder(String path) {
        if (mToolbar != null) {
            mToolbar.setSubtitle(path);
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public View getLoading() {
        return loading;
    }
}
