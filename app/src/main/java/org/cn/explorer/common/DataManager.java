package org.cn.explorer.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import java.util.UUID;

/**
 * Created by chenning on 2015/10/13.
 */
public class DataManager {
    private static final String TAG = "DataManager";

    private Context ctx;

    private static DataManager instance;

    private SharedPreferences sp;

    private SharedPreferences.Editor spe;

    private TelephonyManager tm;

    private String rootDirectory = Environment.getExternalStorageState();

    public static DataManager getInstance(Context ctx) {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager(ctx);
                }
            }
        }
        return instance;
    }

    public DataManager(Context ctx) {
        this.ctx = ctx;
        tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        sp = ctx.getSharedPreferences("appConfig", Context.MODE_PRIVATE);
        spe = sp.edit();
        init();
    }

    private void init() {

    }

    public void put(String key, String value) {
        spe.putString(key, value);
        spe.commit();
    }

    public String get(String key) {
        return sp.getString(key, "");
    }

    public String getDeviceId() {
        String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = get("deviceId");
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = "" + UUID.randomUUID().toString();
                put("deviceId", deviceId);
            }
        }
        return deviceId;
    }

    public boolean storageState() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

}
