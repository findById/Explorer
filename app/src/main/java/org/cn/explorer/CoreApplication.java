package org.cn.explorer;

import android.app.Application;

/**
 * Created by chenning on 2015/10/13.
 */
public class CoreApplication extends Application {
    private static final String TAG = "CoreApplication";

    private static CoreApplication instance = null;

    public CoreApplication() {
    }

    public static CoreApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    protected void init() {

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        System.gc();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
