package org.opencv.samples.facedetect.request;


import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.widget.ImageView;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.AsyncRequestExecutor;
import com.yanzhenjie.nohttp.rest.ImageRequest;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;


import org.opencv.samples.facedetect.Utils.FaceUtils;

import java.io.InputStream;
import java.util.Map;

/**
 * 网络请求类
 * Created by OlAy on 2017/8/7.
 */

public class HttpUtils {

    public static void request(String url, Map<String, Object> map, final Http http) {
        StringRequest request = new StringRequest(url, RequestMethod.POST);
        request.add(map);
        AsyncRequestExecutor.INSTANCE.execute(0, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                String result = response.get();
                http.Success(result);
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                http.Failed();
            }
        });
    }

    public static void requestImage(String url, Bitmap bitmap, final Http http) {
        InputStream input = FaceUtils.getBAIS(bitmap);
        StringRequest request = new StringRequest(url, RequestMethod.POST);
        request.setDefineRequestBody(input, "image/*");
        AsyncRequestExecutor.INSTANCE.execute(0, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                String result = response.get();
                http.Success(result);
            }

            @Override
            public void onFinish(int what) {
                http.Finish();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                http.Failed();
            }
        });
    }

    public static void requestJsonAndImage(String url, Map<String, Object> map, Bitmap bitmap, final Http http) {
        StringRequest request = new StringRequest(url, RequestMethod.POST);
        request.add(map);
        InputStream input = FaceUtils.getBAIS(bitmap);
        request.setDefineRequestBody(input, "image/*");
        AsyncRequestExecutor.INSTANCE.execute(0, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                String result = response.get();
                http.Success(result);
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                http.Failed();
            }
        });
    }
}
