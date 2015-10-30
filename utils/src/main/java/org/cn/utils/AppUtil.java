package org.cn.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chenning on 2015/10/21.
 */
public class AppUtil {

    private AppUtil() {
    }

    public static void setOptionalIconsVisible(Menu menu, boolean visible) {
        try {
            Class<?> clazz = null;
            if ("android.support.v7.internal.view.menu.MenuBuilder".equals(menu.getClass().getName())) {
                clazz = Class.forName("android.support.v7.internal.view.menu.MenuBuilder");
            }
            if (clazz != null) {
                Method method = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                method.setAccessible(true);
                method.invoke(menu, visible);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Return true if any view is currently active in the input method.
     */
    public static boolean keyboardIsActive(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

    public static void showKeyboard(final Context ctx, final View view) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager imm = ((InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE));
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        }, 500);
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager imm = ((InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE));
        View view = ((Activity) ctx).getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
