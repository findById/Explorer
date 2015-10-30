package org.cn.explorer.service.main;

import android.os.Handler;
import android.os.Looper;

import org.cn.explorer.common.AppConfig;
import org.cn.explorer.utils.DateUtil;
import org.cn.explorer.utils.FileUtil;
import org.cn.explorer.vo.ExpItem;

import java.io.File;
import java.io.FileFilter;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by chenning on 2015/10/13.
 */
public class Dispatcher extends Thread {
    /**
     * The queue of requests to service.
     */
    private final BlockingQueue<Request> queue;

    private volatile boolean quit = false;

    private final ExecutorDelivery delivery;

    public Dispatcher(BlockingQueue<Request> queue) {
        this(queue, new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    public Dispatcher(BlockingQueue<Request> queue, ExecutorDelivery delivery) {
        this.queue = queue;
        this.delivery = delivery;
    }

    public void quit() {
        quit = true;
        interrupt();
    }

    @Override
    public void run() {

        while (true) {
            long startTimeMs = System.currentTimeMillis();
            Request request;
            try {
                request = queue.take();
            } catch (InterruptedException e) {
                if (quit) {
                    return;
                }
                continue;
            }

            execute(request);

            System.out.println("request used: " + (System.currentTimeMillis() - startTimeMs) + " ms.");
        }

    }

    public void execute(Request request) {
        FileFilter fileFilter;
        if (!AppConfig.showHideFile) {
            fileFilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname != null) {
                        if (!pathname.isHidden()) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        } else {
            fileFilter = null;
        }
        File[] files = request.file.listFiles(fileFilter);
        List<ExpItem> items = new ArrayList<>();

        if (request.file.canRead() && (files != null || files.length > 0)) {
            for (File f : files) {
                String permission = "" + (f.isDirectory() ? "d" : "-") + (f.canRead() ? "r" : "-") + (f.canWrite() ? "w" : "-") + (f.canExecute() ? "x" : "-");
                ExpItem item = new ExpItem(f, f.getName(), FileUtil.getFileSize(f) + "", DateUtil.timestamp(f.lastModified()) + "", "", permission, "", false, FileUtil.getFileChildrenCount(f));
                if (f.isFile()) {
                    item.setContentType(URLConnection.getFileNameMap().getContentTypeFor(f.getPath()));
                }
                items.add(item);
            }
            Collections.sort(items, new Comparator<ExpItem>() {
                @Override
                public int compare(ExpItem lhs, ExpItem rhs) {
                    return sortByMode(lhs, rhs, AppConfig.sortMode);
                }
            });
        }

        Response response = Response.success(200, items);
        delivery.execute(request, response);
    }

    private int sortByMode(ExpItem lhs, ExpItem rhs, int sortMode) {
        switch (sortMode) {
            case 0: { // by name
                if (lhs.getFile().isDirectory() && rhs.getFile().isFile()) {
                    return -1;
                } else if (lhs.getFile().isFile() && rhs.getFile().isDirectory()) {
                    return 1;
                }
                return lhs.getFile().getName().compareToIgnoreCase(rhs.getFile().getName());
            }
            case 1: { // by date
                if (lhs.getFile().lastModified() == rhs.getFile().lastModified()) {
                    return lhs.getFile().getName().compareToIgnoreCase(rhs.getFile().getName());
                } else if (lhs.getFile().lastModified() > 0 && rhs.getFile().lastModified() > 0) {
                    return (lhs.getFile().lastModified() - rhs.getFile().lastModified()) >= 1 ? -1 : 1;
                } else if (lhs.getFile().lastModified() > 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
            case 2: { // by size
                if (lhs.getFile().isDirectory() && rhs.getFile().isFile()) {
                    return -1;
                } else if (lhs.getFile().isFile() && rhs.getFile().isDirectory()) {
                    return 1;
                } else if (lhs.getFile().isDirectory() && rhs.getFile().isDirectory()) {
                    return lhs.getFile().getName().compareToIgnoreCase(rhs.getFile().getName());
                }
                return (lhs.getFile().length() - rhs.getFile().length()) >= 1 ? -1 : 1;
            }
            case 3: { // by type
                if (lhs.getFile().isDirectory() && rhs.getFile().isFile()) {
                    return -1;
                } else if (lhs.getFile().isFile() && rhs.getFile().isDirectory()) {
                    return 1;
                }
                if (lhs.getFile().isDirectory() && rhs.getFile().isDirectory()) {
                    return lhs.getFile().getName().compareToIgnoreCase(rhs.getFile().getName());
                }
                String lt = (lhs.getContentType() != null && lhs.getContentType().length() >= 1) ? lhs.getContentType() : "unknown";
                String rt = (rhs.getContentType() != null && rhs.getContentType().length() >= 1) ? rhs.getContentType() : "unknown";
                if (lt.equalsIgnoreCase(rt)) {
                    return lhs.getFile().getName().compareToIgnoreCase(rhs.getFile().getName());
                } else if (lhs.getContentType() != null && rhs.getContentType() != null) {
                    return lhs.getContentType().equalsIgnoreCase(rhs.getContentType()) ? -1 : 1;
                } else if (lhs.getContentType() != null) {
                    return -1;
                } else {
                    return 1;
                }
            }
            default: { // by name
                if (lhs.getFile().isDirectory() && rhs.getFile().isFile()) {
                    return -1;
                } else if (lhs.getFile().isFile() && rhs.getFile().isDirectory()) {
                    return 1;
                }
                return lhs.getFile().getName().compareToIgnoreCase(rhs.getFile().getName());
            }

        }
    }

}
