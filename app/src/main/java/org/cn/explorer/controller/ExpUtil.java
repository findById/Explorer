package org.cn.explorer.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.net.URLConnection;

/**
 * Created by chenning on 2015/10/19.
 */
public class ExpUtil {

    public static boolean platform(final Context ctx, File file) {
        return false;
    }

    public static Intent parseUri(final Context ctx, File file) {
        long start = System.currentTimeMillis();
        try {
            if (ctx != null && file != null) {
                if (file.isDirectory()) {
                    Intent intent = new Intent();

                } else if (file.isFile()) {
                    String contentType = URLConnection.getFileNameMap().getContentTypeFor(file.getPath());
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), (TextUtils.isEmpty(contentType) ? "*/*" : contentType));
                    ctx.startActivity(intent);
                }
            }
            return null;
        } finally {
            Log.d("ExpUtil", "Parse URI:" + (System.currentTimeMillis() - start));
        }
    }

}
