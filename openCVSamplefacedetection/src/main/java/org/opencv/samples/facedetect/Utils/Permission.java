package org.opencv.samples.facedetect.Utils;

import android.app.Activity;
import android.content.Context;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;


/**
 * 权限许可类
 * Created by OlAy on 2017/7/26.
 */

public class Permission {
    /**
     * 申请权限
     *
     * @param mActivity  上下文
     * @param permission 权限，可以是单个权限，也可以是权限组
     */
    public static void ApplyPermission(Activity mActivity, String... permission) {
        AndPermission.with(mActivity)
                .requestCode(200)
                .permission(permission)
                .callback(mActivity)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {

                    }
                })
                .start();
    }
}
