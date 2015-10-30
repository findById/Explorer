package org.cn.utils;

/**
 * Created by chenning on 2015/10/13.
 */
public class DpUtil {
    private DpUtil() {
    }

    public static int dp2Pixels(float dp) {
        return Math.round(dp * Screen.newInstance().density + 0.5f);
    }

    public static int pixels2Dp(float pixels) {
        return Math.round(pixels / Screen.newInstance().density + 0.5f);
    }
}
