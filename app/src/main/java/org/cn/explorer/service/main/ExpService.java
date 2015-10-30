package org.cn.explorer.service.main;

import org.cn.explorer.vo.ExpItem;

import java.io.File;
import java.util.List;

/**
 * Created by chenning on 2015/10/10.
 */
public class ExpService {

    private static RequestQueue queue;

    private synchronized static RequestQueue newRequestQueue() {
        if (queue == null) {
            queue = new RequestQueue();
            queue.start();
        }
        return queue;
    }

    public static void async(File url, final ExpListener listener) {
        Request request = new Request(url, new Response.Listener() {
            @Override
            public void onResponse(List<ExpItem> items) {
                if (listener != null) {
                    listener.onResponse(items);
                }
            }
        });
        RequestQueue queue = newRequestQueue();
        queue.add(request);
    }


}
