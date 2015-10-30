package org.cn.explorer.service.main;

import org.cn.explorer.vo.ExpItem;

import java.util.List;

/**
 * Created by chenning on 2015/10/12.
 */
public class Response {

    public interface Listener {
        void onResponse(List<ExpItem> items);
    }

    public List<ExpItem> data;

    public Response(int statusCode, List<ExpItem> data) {
        this.data = data;
    }

    public static Response success(int statusCode, List<ExpItem> data) {
        return new Response(statusCode, data);
    }

}
