package org.opencv.samples.facedetect.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.samples.facedetect.DetectionBasedTracker;
import org.opencv.samples.facedetect.R;
import org.opencv.samples.facedetect.Utils.FaceUtils;
import org.opencv.samples.facedetect.Utils.ToastHelper;
import org.opencv.samples.facedetect.global.Global;
import org.opencv.samples.facedetect.request.Http;
import org.opencv.samples.facedetect.request.HttpHelper;
import org.opencv.samples.facedetect.request.HttpUtils;
import org.opencv.samples.facedetect.view.LineView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 登录时的人脸识别类
 **/
public class FaceRecognitionActivity extends Activity
        implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase java;
    private static final String TAG = "FaceRecognitionActivity";
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;
    private Mat mRgba;
    private Mat mGray;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;
    private int mDetectorType = JAVA_DETECTOR;
    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    private Bitmap shibiebitmap;
    private int index = 0;
    private LineView lineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flogin);

        lineView = (LineView) findViewById(R.id.lineView);
        lineView.onWindowFocusChanged(true);
        lineView.start();

        java = (CameraBridgeViewBase) findViewById(R.id.java);
        java.setCvCameraViewListener(this);
        java.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);

        LoadNative();

        handler.sendEmptyMessageDelayed(1, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        java.disableView();
    }

    private void LoadNative() {
        System.loadLibrary("detection_based_tracker");
        try {
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (mJavaDetector.empty()) {
                mJavaDetector = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

            mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
        java.enableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.e("TAG", "onCameraViewStarted");
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        Core.flip(mRgba, mRgba, 1);
        Core.flip(mGray, mGray, 1);
        Point point = new Point(mGray.width() / 2 - 375, mGray.height() / 2 - 375);

        Rect rect = new Rect(point, new Size(750, 750));
        //Core.rectangle(mRgba, rect.tl(), rect.br(), FACE_RECT_COLOR, 4);
        mGray = new Mat(mGray, rect);
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }

            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }
        MatOfRect faces = new MatOfRect();
        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2,
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        } else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        } else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();
        /**
         * 画出个框显示寻找到的面部
         */
        if (facesArray.length > 0) {
            for (int i = 0; i < facesArray.length; i++) {
                Point point1 = new Point(facesArray[i].x + point.x, facesArray[i].y + point.y);
                facesArray[i] = new Rect(point1, facesArray[i].size());
                if (facesArray[i].width > 350) {
                    Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
                    shibiebitmap = FaceUtils.cutDownFaceROI(mRgba, facesArray[i]);
                }
            }
        }
        return mRgba;
    }

    private boolean is_success;

    private void SendImage2Servlet() {
        //发送图片
        HttpUtils.requestImage(Global.Is_Pass, shibiebitmap, new HttpHelper() {
            @Override
            public void Success(String result) {
                Log.e(TAG, "Success: " + result);
                if (result.equals("Fail")) {
                    index++;
                    is_success = false;
                } else {
                    Intent intent = new Intent(FaceRecognitionActivity.this, SuccessActivity.class);
                    intent.putExtra("Info", result);
                    startActivity(intent);
                    finish();
                    is_success = true;
                }
            }

            @Override
            public void Failed() {
                ToastHelper.show(getApplication(), "网络连接超时，请稍后再试");
            }

            @Override
            public void Finish() {
                if (!is_success)
                    handler.sendEmptyMessage(1);
            }
        });
    }

    private int control = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (null != shibiebitmap && index < 10)
                        SendImage2Servlet();
                    else if (index > 9) {
                        java.disableView();
                        handler.sendEmptyMessage(2);
                    } else if (null == shibiebitmap) {
                        handler.sendEmptyMessage(1);
                    }
                    Log.e(TAG, "handleMessage: " + index);
                    break;
                case 2:
                    if (control == 0) {
                        control++;
                        Intent intent = new Intent(FaceRecognitionActivity.this, ControlActivity.class);
                        intent.putExtra("result", "识别失败,请先注册或者重试");
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    };
}
