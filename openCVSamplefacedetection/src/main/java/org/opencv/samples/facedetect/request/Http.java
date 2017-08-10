package org.opencv.samples.facedetect.request;


/**
 * 网络请求结果的接口
 * Created by OlAy on 2017/8/7.
 */

public interface Http {
    //请求成功后的逻辑
    void Success(String result);

    //请求失败后的逻辑
    void Failed();

    //请求完成后
    void Finish();
}
