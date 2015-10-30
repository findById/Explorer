package org.cn.explorer.service.main;

import android.os.Handler;

import java.util.concurrent.Executor;

/**
 * Created by chenning on 2015/10/12.
 */
public class ExecutorDelivery {

    private final Executor mResponsePoster;

    public ExecutorDelivery(final Handler handler) {
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    public void execute(Request request, Response response) {
        mResponsePoster.execute(new ExecutorRunnable(request, response));
    }

    private class ExecutorRunnable implements Runnable {
        private Request request;
        private Response response;

        public ExecutorRunnable(Request request, Response response) {
            this.request = request;
            this.response = response;
        }

        @Override
        public void run() {
            request.deliverResponse(response.data);
        }

    }

}
