package org.cn.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by chenning on 2015/10/13.
 */
public class Screen {

    static Screen SCREEN = new Screen();

    public int widthPixels;
    public int heightPixels;
    public float density;
    public float scaledDensity;
    public int densityDpi;
    public float xdpi;
    public float ydpi;
    public int statusBarHeight;
    public int navigationBarHeight;

    private Screen() {
    }

    public static void initScreen(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        SCREEN.widthPixels = metrics.widthPixels;
        SCREEN.heightPixels = metrics.heightPixels;
        SCREEN.density = metrics.density;
        SCREEN.scaledDensity = metrics.scaledDensity;
        SCREEN.densityDpi = metrics.densityDpi;
        SCREEN.xdpi = metrics.xdpi;
        SCREEN.ydpi = metrics.ydpi;
        try {
            SCREEN.statusBarHeight = getInternalDimensionSizeByKey(context, "status_bar_height");
            SCREEN.navigationBarHeight = getInternalDimensionSizeByKey(context, "navigation_bar_height");
        } catch (Exception e) {
            // ignore
        }

        // DisplayMetrics metrics = new DisplayMetrics();
        // getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    /**
     * @param ctx
     * @param key status_bar_height
     *            navigation_bar_height
     *            navigation_bar_width
     * @return
     */
    private static int getInternalDimensionSizeByKey(Context ctx, String key) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getInternalByName(Context ctx, String name, String defType, String defPackage) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier(name, defType, defPackage);
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static Screen newInstance() {
        return SCREEN;
    }

}
