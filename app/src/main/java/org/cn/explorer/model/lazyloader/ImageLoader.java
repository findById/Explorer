package org.cn.explorer.model.lazyloader;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import org.cn.explorer.R;
import org.cn.utils.BitmapUtil;

/**
 * Created by chenning on 2015/10/10.
 */
public class ImageLoader {

    private Context ctx;
    private static ImageLoader instance;

    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;
    private DiskCache diskCache;

    public ImageLoader(Context ctx, File cacheDir) {
        this.ctx = ctx;
        executorService = Executors.newFixedThreadPool(10);
        diskCache = new DiskCache(ctx, cacheDir);
    }

    public static ImageLoader newInstance(Context ctx, File cacheDir) {
        if (instance == null) {
            instance = new ImageLoader(ctx, cacheDir);
        }
        return instance;
    }

    // handler to display images in UI thread
    private Handler handler = new Handler();

    public void display(Item item) {
        imageViews.put(item.view, item.url);
        queueRequest(item);
        item.view.setImageResource(item.defaultResId);
    }

    private void queueRequest(Item item) {
        PhotoToLoad p = new PhotoToLoad(item);
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(5);
        }
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(Item item) {
        Bitmap bitmap = diskCache.getBitmap(item.url);
        if (bitmap != null) {
            return bitmap;
        }

        try {
            if (!TextUtils.isEmpty(item.contentType)) {
                if (item.contentType.contains("image")) {
                    bitmap = BitmapUtil.getImageThumbnail(item.url, 200, 200);
                } else if (item.contentType.contains("video")) {
                    bitmap = BitmapUtil.getVideoThumbnail(item.url, 200, 200, MediaStore.Images.Thumbnails.MINI_KIND);
                } else if (item.contentType.contains("android.package")) {
                    Drawable drawable = BitmapUtil.getApkIcon(ctx, item.file.getPath());
                    if (drawable != null) {
                        bitmap = BitmapUtil.drawable2Bitmap(drawable);
                    }
                }
                if (bitmap != null) {
                    diskCache.putBitmap(item.url, bitmap);
                }
            }
            return bitmap;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    // Task for the queue
    private class PhotoToLoad {
        public Item item;

        public PhotoToLoad(Item item) {
            this.item = item;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            try {
                if (imageViewReused(photoToLoad)) {
                    return;
                }
                Bitmap bmp = getBitmap(photoToLoad.item);
                if (imageViewReused(photoToLoad)) {
                    return;
                }
                BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.item.view);
        if (tag == null || !tag.equals(photoToLoad.item.url)) {
            return true;
        }
        return false;
    }

    // Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            if (bitmap != null) {
                photoToLoad.item.view.setImageBitmap(bitmap);
            } else {
                photoToLoad.item.view.setImageResource(photoToLoad.item.defaultResId);
            }
        }
    }

}
