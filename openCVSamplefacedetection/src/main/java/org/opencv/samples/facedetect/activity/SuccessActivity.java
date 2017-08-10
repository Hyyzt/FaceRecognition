package org.opencv.samples.facedetect.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.AsyncRequestExecutor;
import com.yanzhenjie.nohttp.rest.ImageRequest;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;

import org.opencv.samples.facedetect.Domain.UserInfo;
import org.opencv.samples.facedetect.R;
import org.opencv.samples.facedetect.Utils.FaceUtils;
import org.opencv.samples.facedetect.Utils.GsonUtils;
import org.opencv.samples.facedetect.Utils.ToastHelper;
import org.opencv.samples.facedetect.request.HttpUtils;

/**
 * 检测成功后的信息展示类
 */
public class SuccessActivity extends Activity {
    private ImageView success_iv;
    private TextView success_name, success_age, success_sex, success_birthday;
    private Bitmap bitmap;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            success_iv.setImageBitmap(bitmap);
        }
    };
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        success_iv = (ImageView) findViewById(R.id.success_iv);
        success_name = (TextView) findViewById(R.id.success_name);
        success_age = (TextView) findViewById(R.id.success_age);
        success_sex = (TextView) findViewById(R.id.success_sex);
        success_birthday = (TextView) findViewById(R.id.success_birthday);

        //拿到json字符串并解析
        Intent intent = getIntent();
        String action = intent.getStringExtra("Info");
        Log.e("TAG", "onCreate: " + action);
        try {
            userInfo = GsonUtils.Json2Class(action, UserInfo.class);
        } catch (Exception e) {
            ToastHelper.show(getApplication(), "服务器出错");
        }
        //设置数据

        success_name.setText(userInfo.name);
        success_age.setText(userInfo.age);
        success_birthday.setText(userInfo.birthday);
        success_sex.setText(userInfo.sex == 0 ? "男" : "女");
        Request<Bitmap> request = NoHttp.createImageRequest(userInfo.url, RequestMethod.GET);
        AsyncRequestExecutor.INSTANCE.execute(0, request, new SimpleResponseListener<Bitmap>() {
            @Override
            public void onSucceed(int what, Response<Bitmap> response) {
                bitmap = response.get();
                handler.sendEmptyMessage(0);
            }
        });
    }
}
