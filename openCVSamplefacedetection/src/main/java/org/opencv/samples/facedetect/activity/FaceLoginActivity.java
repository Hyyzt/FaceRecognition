package org.opencv.samples.facedetect.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.samples.facedetect.DetectionBasedTracker;
import org.opencv.samples.facedetect.R;
import org.opencv.samples.facedetect.Utils.FaceUtils;
import org.opencv.samples.facedetect.Utils.ToastHelper;
import org.opencv.samples.facedetect.global.Global;
import org.opencv.samples.facedetect.request.HttpHelper;
import org.opencv.samples.facedetect.request.HttpUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

/**
 * 用户注册时需要的验证类
 */
public class FaceLoginActivity extends Activity implements CvCameraViewListener2 {

    private static final String TAG = "Activity";
    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;

    private Mat mRgba;
    private Mat mGray;
    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;

    private int mDetectorType = JAVA_DETECTOR;
    private String[] mDetectorName;

    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;
    //视频流控件
    private CameraBridgeViewBase mOpenCvCameraView;
    public Bitmap bitmap;

    public FaceLoginActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";
    }

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_frecognition);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        LoadNative();
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);

        handler.sendEmptyMessageDelayed(1, 1500);
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
                Log.e(TAG, "Failed to ControlActivity cascade classifier");
                mJavaDetector = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

            mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
        mOpenCvCameraView.enableView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    //视频流开始
    public void onCameraViewStarted(int width, int height) {
        Log.e("TAG", "onCameraViewStarted");
        mGray = new Mat();
        mRgba = new Mat();
    }

    //视频流结束
    public void onCameraViewStopped() {
        Log.e("TAG", "onCameraViewStopped");
        mGray.release();
        mRgba.release();
    }

    //使用视频流时的回调
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        //倒转镜像的摄像头
        Core.flip(mRgba, mRgba, 1);
        Core.flip(mGray, mGray, 1);

        //将视频流控制住，只在一定区域内可以检测人脸
        Point point = new Point(mGray.width() / 2 - 375, mGray.height() / 2 - 375);
        Rect rect = new Rect(point, new Size(750, 750));
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
                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        } else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        } else {
            Log.e(TAG, "Detection method is not selected!");
        }
        //小哥，椰子皮，你他妈给我一个梨，i
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0) {
            for (int i = 0; i < facesArray.length; i++){
                Point point1 = new Point(facesArray[i].x + point.x, facesArray[i].y + point.y);
                facesArray[i] = new Rect(point1, facesArray[i].size());
                if(facesArray[i].width > 350) {
                    Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
                    bitmap = FaceUtils.cutDownFaceROI(mRgba, facesArray[i]);
                }
            }
        }
        return mRgba;
    }

    private int index = 0;
    private boolean is_Login = false;

    private void SendImage2Servlet() {
        HttpUtils.requestImage(Global.IsLogin, bitmap, new HttpHelper() {
            @Override
            public void Success(String result) {
                Log.e(TAG, "Success: " + result);
                if (result.equals("NoLogin")) {//没注册
                    index++;
                    is_Login = false;
                } else if (result.equals("Login")) {//注册过
                    Intent intent = new Intent(FaceLoginActivity.this, ControlActivity.class);
                    mOpenCvCameraView.disableView();
                    is_Login = true;
                    intent.putExtra("result", "你已经进行过注册了");
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void Failed() {
                ToastHelper.show(getApplication(), "网络连接超时，请稍后再试");
            }
            @Override
            public void Finish() {
                if (!is_Login) {
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    private int control = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (null != bitmap && index < 10) {
                        SendImage2Servlet();
                    } else if (null == bitmap) {
                        handler.sendEmptyMessage(1);
                    } else if (index > 9) {
                        handler.sendEmptyMessage(2);
                    }
                    Log.e(TAG, "handleMessage: " + index);
                    break;
                case 2:
                    if (control == 0) {
                        control++;
                        Intent intent = new Intent(FaceLoginActivity.this, InfoActivity.class);
                        intent.putExtra("Bitmap", FaceUtils.Bitmap2Byte(bitmap));
                        mOpenCvCameraView.disableView();
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    };

}
