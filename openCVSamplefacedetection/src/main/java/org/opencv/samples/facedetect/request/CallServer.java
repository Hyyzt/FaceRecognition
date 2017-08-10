package org.opencv.samples.facedetect.request;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;

/**
 * 网络请求队列类
 * Created by OlAy on 2017/7/31.
 */

public class CallServer {
    private static CallServer instance;

    public static CallServer getInstance() {
        if (instance == null) {
            synchronized (CallServer.class) {
                if (instance == null) {
                    instance = new CallServer();
                }
            }
        }
        return instance;
    }

    private RequestQueue queue;

    private CallServer() {
        queue = NoHttp.newRequestQueue(1);
    }

    public <T> void request(int what, Request<T> request, SimpleResponseListener<T> listener) {
        queue.add(what, request, listener);
    }

    public void stop() {
        queue.stop();
    }

    public boolean IsEnd() {
        if (queue.unFinishSize() == 0)
            return true;
        else
            return false;
    }

}
