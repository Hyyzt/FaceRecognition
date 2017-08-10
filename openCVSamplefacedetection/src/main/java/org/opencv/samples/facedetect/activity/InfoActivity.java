package org.opencv.samples.facedetect.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.AsyncRequestExecutor;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;
import com.yanzhenjie.nohttp.rest.StringRequest;

import org.opencv.samples.facedetect.Domain.UserInfo;
import org.opencv.samples.facedetect.R;
import org.opencv.samples.facedetect.Utils.FaceUtils;
import org.opencv.samples.facedetect.Utils.GsonUtils;
import org.opencv.samples.facedetect.Utils.ToastHelper;
import org.opencv.samples.facedetect.global.Global;
import org.opencv.samples.facedetect.request.Http;
import org.opencv.samples.facedetect.request.HttpHelper;
import org.opencv.samples.facedetect.request.HttpUtils;
import org.opencv.samples.facedetect.request.MyApplication;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册时录入信息,并提交给服务器，服务器存库
 */
public class InfoActivity extends Activity {
    private EditText register_name, register_age;
    private RadioButton register_male, register_female;
    private DatePicker register_birth;
    private ImageView register_iv;
    private Button register_submit;
    private boolean isMale = true;

    private int day, month, year;
    private String birthday;
    private byte[] os;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        register_name = (EditText) findViewById(R.id.register_name);
        register_age = (EditText) findViewById(R.id.register_age);
        register_male = (RadioButton) findViewById(R.id.register_sex_male);
        register_female = (RadioButton) findViewById(R.id.register_sex_female);
        register_birth = (DatePicker) findViewById(R.id.register_birthdate);
        register_submit = (Button) findViewById(R.id.register_btn_submit);
        register_iv = (ImageView) findViewById(R.id.register_iv);
        ToastHelper.show(getApplication(), "您可以进行注册，请填写信息");
        Intent intent = getIntent();
        os = intent.getByteArrayExtra("Bitmap");
        bitmap = FaceUtils.Byte2Bitmap(os);
        register_iv.setImageBitmap(bitmap);
        Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        initListener();

    }

    private void initListener() {
        register_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_female.setChecked(false);
                register_male.setChecked(true);
                isMale = true;
            }
        });

        register_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_female.setChecked(true);
                register_male.setChecked(false);
                isMale = false;
            }
        });

        register_birth.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                birthday = year + "-" + monthOfYear + "-" + dayOfMonth;
            }
        });

        register_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交数据至服务器
                if (register_name.getText().length() == 0
                        || register_age.getText().length() == 0) {
                    ToastHelper.show(InfoActivity.this, "请输入您的名字和年龄");
                } else {
                    submit();
                }
            }
        });
    }

    private void submit() {
        //提交数据至服务器
        UserInfo userInfo = new UserInfo();
        userInfo.name = register_name.getText().toString();
        userInfo.age = register_age.getText().toString();
        userInfo.birthday = birthday;
        if (isMale) {
            userInfo.sex = 0;
        } else {
            userInfo.sex = 1;
        }

        //通过json将信息传送至服务器
        Gson gson = new Gson();
        String json = gson.toJson(userInfo);
        Map<String, Object> map = new HashMap<>();
        map.put("Info", json);
        Log.e("TAG", json);
        HttpUtils.requestJsonAndImage(Global.Sumbit_Info, map, bitmap, new HttpHelper() {
            @Override
            public void Success(String result) {
                Intent intent = new Intent(new Intent(InfoActivity.this, ControlActivity.class));
                if (result.equals("Success")) {
                    intent.putExtra("result", "注册成功");
                    startActivity(intent);
                    finish();
                } else if (result.equals("Failed")) {
                    intent.putExtra("result", "注册成功");
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

            }
        });
    }
}
