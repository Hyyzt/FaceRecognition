package org.opencv.samples.facedetect.global;


/**
 * 网络请求的接口
 * Created by OlAy on 2017/8/7.
 */

public class Global {

    private static final String URL = "http://192.168.43.94:8080";
    public static final String IsLogin = URL + "/HelloWorld/GetData";
    public static final String Sumbit_Info = URL + "/HelloWorld/GetJson";
    public static final String Is_Pass = URL + "/HelloWorld/VerifyLogin";
}
