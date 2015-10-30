package org.cn.explorer.service.main;

import org.cn.explorer.vo.ExpItem;

import java.util.List;

/**
 * Created by chenning on 2015/10/13.
 */
public interface ExpListener {

    void onResponse(List<ExpItem> items);

}
