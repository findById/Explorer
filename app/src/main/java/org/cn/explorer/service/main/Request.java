package org.cn.explorer.service.main;

import org.cn.explorer.vo.ExpItem;

import java.io.File;
import java.util.List;

/**
 * Created by chenning on 2015/10/13.
 */
public class Request implements Comparable<Request> {

    public final File file;
    public final Response.Listener listener;

    public Request(File file, Response.Listener listener) {
        this.file = file;
        this.listener = listener;
    }

    protected void deliverResponse(List<ExpItem> data) {
        if (listener != null) {
            listener.onResponse(data);
        }
    }

    @Override
    public int compareTo(Request another) {
        return 0;
    }
}
