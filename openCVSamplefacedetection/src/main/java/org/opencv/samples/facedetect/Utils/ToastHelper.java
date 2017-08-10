package org.opencv.samples.facedetect.Utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Toast
 * Created by OlAy on 2017/7/26.
 */

public class ToastHelper {

    public static void show(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
