package org.cn.explorer;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, ExpFragment.OnOpenFolderListener {

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private LayoutFragment currentFragment;

    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private View loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        this.mNavigationView = navigationView;
        this.mToolbar = toolbar;
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();

        initView();
        initData();
    }

    private void initView() {
        loading = this.findViewById(R.id.loading);
    }

    private void initData() {

        if (AppConfig.currentNavigationId == 0) {
            AppConfig.currentNavigationId = R.id.nav_disk;
        }
        mNavigationView.setCheckedItem(AppConfig.currentNavigationId);
        onNavigationChanged(AppConfig.currentNavigationId);

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

        AppConfig.currentNavigationId = item.getItemId();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onNavigationChanged(AppConfig.currentNavigationId);
            }
        }, 500);

        return true;
    }

    private boolean onNavigationChanged(int id) {
        String tag = "";
        // Handle navigation view item clicks here.
        switch (id) {
            case R.id.nav_store:
                tag = "DIRECTORY_STORE";
                AppConfig.currentFilePath = new File("/");
                break;
            case R.id.nav_disk:
                tag = "DIRECTORY_DISK";
                AppConfig.currentFilePath = Environment.getExternalStoragePublicDirectory(File.separator);
                break;
            case R.id.nav_camera:
                tag = "DIRECTORY_CAMERA";
                AppConfig.currentFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                break;
            case R.id.nav_download:
                tag = "DIRECTORY_DOWNLOADS";
                AppConfig.currentFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                break;
            case R.id.nav_pictures:
                tag = "DIRECTORY_PICTURES";
                AppConfig.currentFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                break;
            case R.id.nav_music:
                tag = "DIRECTORY_MUSIC";
                AppConfig.currentFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                break;
            case R.id.nav_movies:
                tag = "DIRECTORY_MOVIES";
                AppConfig.currentFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_feedback:
                break;
            default:
                // ignore
                break;
        }
        if (TextUtils.isEmpty(tag)) {
            return true;
        }
        Log.d("MA", "" + AppConfig.currentFilePath.getPath());
        Bundle bundle = new Bundle();
        bundle.putString(Const.ROOT_PATH, AppConfig.currentFilePath.getPath());
        onChangeFragment(tag, bundle);
        return true;
    }

    private void onChangeFragment(String key, Bundle bundle) {
        if (key == null) {
            return;
        }
        LayoutFragment tmp = (LayoutFragment) mFragmentManager.findFragmentByTag(key);
        if (tmp == null) {
            tmp = new LayoutFragment();
            add(tmp, key, bundle);
        } else {
            List<Fragment> list = mFragmentManager.getFragments();
            if (list != null && list.size() > 0) {
                mFragmentTransaction = mFragmentManager.beginTransaction();
                for (int i = 0; i < list.size(); i++) {
                    mFragmentTransaction.detach(list.get(i));
                }
                mFragmentTransaction.commit();
            }
            mFragmentTransaction = mFragmentManager.beginTransaction();
            if (currentFragment != null) {
                mFragmentTransaction.detach(currentFragment);
            }
            mFragmentTransaction.attach(tmp);
            mFragmentTransaction.commit();
        }
        currentFragment = tmp;
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
        if (data != null) {
            fragment.setArguments(data);
        }
        mFragmentTransaction.add(R.id.contentPanel, fragment, tag);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.commit();
        // mFragmentManager.executePendingTransactions();
        return mFragmentTransaction;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MA", "onDestroy");
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
