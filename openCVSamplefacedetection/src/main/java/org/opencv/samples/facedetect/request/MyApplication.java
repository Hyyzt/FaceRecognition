package org.opencv.samples.facedetect.request;

import android.app.Application;

import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;

/**
 * 初始化本地OpenCV库，初始化NoHttp
 * Created by OlAy on 2017/7/31.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        Logger.setTag("Request"); // 设置NoHttp打印Log的TAG。
        Logger.setDebug(true);// 开启NoHttp调试模式。
        NoHttp.initialize(this, new NoHttp.Config()
                .setConnectTimeout(5000)
                .setReadTimeout(5000));
        System.loadLibrary("opencv_java");
        super.onCreate();
    }
}
