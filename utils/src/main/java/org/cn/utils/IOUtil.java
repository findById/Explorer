package org.cn.utils;

/**
 * Created by chenning on 2015/10/14.
 */
public class IOUtil {

    private IOUtil() {
    }

    public static void closeQuietly(AutoCloseable... closeables) {
        if (closeables != null && closeables.length > 0) {
            for (AutoCloseable ac : closeables) {
                if (ac != null) {
                    try {
                        ac.close();
                        ac = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
