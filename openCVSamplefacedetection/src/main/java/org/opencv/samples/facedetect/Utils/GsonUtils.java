package org.opencv.samples.facedetect.Utils;

import com.google.gson.Gson;

/**
 * Json处理类
 * Created by OlAy on 2017/8/8.
 */

public class GsonUtils {

    public static String Class2Json(Object o) {
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return json;
    }

    public static <T> T Json2Class(String json, Class<T> Class) {
        Gson gson = new Gson();
        T t = gson.fromJson(json, Class);
        return t;
    }
}
