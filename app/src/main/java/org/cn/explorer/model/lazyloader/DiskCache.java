package org.cn.explorer.model.lazyloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by chenning on 2015/10/13.
 */
public class DiskCache {

    private File cacheDir;

    public DiskCache(Context ctx, File cacheDir) {
        // Find the dir to save cached images
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
            this.cacheDir = cacheDir;
        } else {
            this.cacheDir = ctx.getCacheDir();
        }
    }

    public File getDiskFile(String url) {
        String filename;
        if (url.contains("/")) {
            filename = url.substring(url.lastIndexOf("/"), url.length());
        } else {
            filename = url;
        }
        return new File(cacheDir, filename);
    }

    public Bitmap getBitmap(String key) {
        File file = getDiskFile(key);
        if (file.exists() && file.canRead()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return null;
    }

    public void putBitmap(String key, Bitmap value) {
        File file = getDiskFile(key);
//        createFolder(file.getPath());
//        if (file.exists()) {
//            return;
//        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            value.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(fos);
        }
    }

    private File createFolder(String path) {
        try {
            File file = new File(path);
            File parent = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separator)));
            if (!parent.exists()) {
                createFolder(parent.getPath());
                parent.mkdirs();
            }
            return file;
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null || files.length <= 0) {
            return;
        }
        for (File f : files) {
            f.delete();
        }
    }

    private static void closeQuietly(AutoCloseable... closeables) {
        if (closeables != null) {
            for (AutoCloseable ac : closeables) {
                if (ac != null) {
                    try {
                        ac.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
