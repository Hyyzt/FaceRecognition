package org.opencv.samples.facedetect.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.samples.facedetect.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 人脸识别工具类
 * 图片的裁剪和转化
 * Created by Luke on 2017/7/18.
 */

public class FaceUtils {
    /**
     * 数组转图片
     *
     * @param bytes
     * @return
     */
    public static Bitmap Byte2Bitmap(byte[] bytes) {

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    /**
     * 图片转数组
     *
     * @param bitmap
     * @return
     */
    public static byte[] Bitmap2Byte(Bitmap bitmap) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        return os.toByteArray();
    }

    public static InputStream getBAIS(Bitmap bitmap) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        InputStream isBm = new ByteArrayInputStream(os.toByteArray());
        return isBm;
    }

    public static Bitmap scaleBitmap(Bitmap l) {
        int width = 320;
        int height = 320;
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int nwidth = l.getWidth();
        int nheight = l.getHeight();

        float scalewidth = (float) nwidth / width;
        float scaleheight = (float) nheight / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scalewidth, scaleheight);

        Bitmap newBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newBitmap;
    }

    // 彩色图片转灰度矩阵
    public static Mat getGrayMatFromImg(Bitmap bitmap) {
        Mat mat = new Mat();
        Mat grayMat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY);

        return grayMat;
    }

    // 获取 lbpcascade_frontalface.xml 的绝对路径，用来实例化 CascadeClassifier类
    public static String getXMLFilePath(Context context) {
        InputStream is = context.getResources().
                openRawResource(R.raw.lbpcascade_frontalface);
        File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
        File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCascadeFile.getAbsolutePath();
    }


    // 裁剪识别出的人脸区域
    public static Bitmap cutDownFaceROI(Mat mat, Rect rect) {
        Log.d("TAG", "cutDownFaceROI: " + rect.toString());
        Mat faceMat = new Mat(mat, rect);
        Imgproc.resize(faceMat, faceMat, new Size(320, 320));
        Bitmap faceBitmap = Bitmap.createBitmap(320, 320, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(faceMat, faceBitmap);
        return faceBitmap;
    }
}
