package org.opencv.samples.facedetect.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yanzhenjie.permission.AndPermission;

import org.opencv.samples.facedetect.R;
import org.opencv.samples.facedetect.Utils.Permission;

/**
 * 主界面，控制整个操作流程和返回结果
 */
public class ControlActivity extends Activity {
    private Button caiji, shibie;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        init();

        caiji = (Button) findViewById(R.id.caiji);
        shibie = (Button) findViewById(R.id.shibie);
        result = (TextView) findViewById(R.id.result);
        Intent intent = getIntent();
        String result1 = intent.getStringExtra("result");
        result.setText(result1);
        caiji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ControlActivity.this, FaceLoginActivity.class));
                finish();
            }
        });

        shibie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ControlActivity.this, FaceRecognitionActivity.class));
                finish();
            }
        });
    }

    private void init() {
        //权限添加
        //android6.0以后，一些危险权限需要去动态添加，不能只在Manifest中添加
        //可以多个权限添加，也可以单个权限添加
        //判断是否拥有权限，否则每次打开上放的状态栏都会出现从左往右慢慢消退的情况。
        if (!AndPermission.hasPermission(ControlActivity.this, Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)) {
            Permission.ApplyPermission(ControlActivity.this, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
        }
    }
}
