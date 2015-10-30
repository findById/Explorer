package org.cn.explorer.utils;

import java.text.SimpleDateFormat;

/**
 * Created by chenning on 2015/10/12.
 */
public class DateUtil {

    public static String timestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }

}
