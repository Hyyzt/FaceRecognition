package org.opencv.samples.facedetect.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.opencv.samples.facedetect.R;

/**
 * 视频流启动后的扫描类
 * Created by OlAy on 2017/8/3.
 */

public class LineView extends RelativeLayout {
    private ImageView h;
    private View view;
    private Animation verAnimation;
    private int left;
    private int right;
    private int top;
    private int bottom;
    private static final String TAG = "TAG";

    public LineView(Context context) {
        this(context, null);
    }

    public LineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_scan, this);
        view = inflate.findViewById(R.id.previewView);
        h = (ImageView) inflate.findViewById(R.id.scanHorizontalLineImageView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        left = view.getLeft();
        right = view.getRight();
        top = view.getTop();
        bottom = view.getBottom();
        Log.e(TAG, "init: " + left);
        Log.e(TAG, "init: " + right);
        Log.e(TAG, "init: " + top);
        Log.e(TAG, "init: " + bottom);
        verAnimation = new TranslateAnimation(left, left, top, bottom);
        verAnimation.setDuration(3000);
        verAnimation.setRepeatCount(Animation.INFINITE);
        h.setAnimation(verAnimation);
    }

    public void start() {
        //开始检测
        verAnimation.startNow();
    }

    public void end() {
        //结束检测
        verAnimation.cancel();
    }

}
