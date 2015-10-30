package org.cn.explorer.service.main;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by chenning on 2015/10/13.
 */
public class RequestQueue {

    private Set<Request> requests = new HashSet<>();

    private PriorityBlockingQueue<Request> queue = new PriorityBlockingQueue<>();

    public RequestQueue() {
    }

    public Request add(Request request) {
        synchronized (requests) {
            requests.add(request);
        }
        queue.add(request);
        return request;
    }

    public void start() {
        Dispatcher dispatcher = new Dispatcher(queue);
        dispatcher.start();
    }

    public void stop() {

    }

    public void finish(File request) {
        synchronized (requests) {
            queue.remove(request);
        }
    }

}
