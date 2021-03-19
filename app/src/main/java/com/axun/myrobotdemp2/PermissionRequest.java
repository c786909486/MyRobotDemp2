package com.axun.myrobotdemp2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

/**
 * Created by hz-java on 2018/8/18.
 */

public class PermissionRequest {



    public static String[] permissions = new String[]{   Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static void requestAll(Context context, final OnPermissionCallback callback){

        request(context,permissions,callback);

    }



    @SuppressLint("CheckResult")
    public static void request(Context context, String[] permissions, final OnPermissionCallback callback){
        new RxPermissions((FragmentActivity) context).request(permissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean){
                            callback.onPermissionSuccess();
                        }else {
                            callback.onPermissionFailed();
                        }
                    }
                });
    }



    /**
     * 判断权限集合
     * permissions 权限数组
     * return true-表示没有改权限  false-表示权限已开启
     */
    public static boolean lacksPermissions(Context mContexts, String[] permissions) {
        for (String permission : permissions) {
            if (lacksPermission(mContexts,permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否缺少权限
     */
    private static boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    public interface OnPermissionCallback{
        void onPermissionSuccess();
        void onPermissionFailed();
    }
}
