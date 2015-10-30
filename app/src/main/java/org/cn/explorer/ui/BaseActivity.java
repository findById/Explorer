package org.cn.explorer.ui;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.cn.explorer.common.DataManager;

import java.util.Locale;

/**
 * Created by chenning on 2015/10/21.
 */
public class BaseActivity extends AppCompatActivity {

    protected DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = Locale.getDefault();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        dataManager = DataManager.getInstance(getApplicationContext());
    }

}
