package org.cn.explorer.model.lazyloader;

import android.widget.ImageView;

import java.io.File;

/**
 * Created by chenning on 2015/10/13.
 */
public class Item {
    public String url;
    public ImageView view;
    public File file;
    public String contentType;
    public int defaultResId;

    public Item(String url, ImageView view, File file, String contentType, int defaultResId) {
        this.url = url;
        this.view = view;
        this.file = file;
        this.contentType = contentType;
        this.defaultResId = defaultResId;
    }
}
