package com.hunofox.baseFramework.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 项目名称：WxApproval
 * 项目作者：胡玉君
 * 创建日期：2017/7/21 9:48.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class CheckUtils {

    public static boolean isEmpty(List list){
        return list == null || list.isEmpty();
    }

    public static boolean isEmpty(Object[] list){
        return list == null || list.length <= 0;
    }
    public static boolean isEmpty(String str){
        return str == null || str.trim().length() <= 0;
    }
    public static boolean isEmpty(CharSequence str){
        return str == null || str.length() <= 0;
    }

    public static void checkPermissions(Activity activity, int requestCode, OnPermissionListener callback, String... permission) {
        requestPermissions(activity, requestCode, callback, permission);
    }

    public static void checkPermissions(android.app.Fragment fragment, int requestCode, OnPermissionListener callback, String... permission) {
        requestPermissions(fragment, requestCode, callback, permission);
    }

    public static void checkPermissions(Fragment fragment, int requestCode, OnPermissionListener callback, String... permission) {
        requestPermissions(fragment, requestCode, callback, permission);
    }

    /** 启动当前应用设置页面 */
    public static void startAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    /** 请求权限结果，对应onRequestPermissionsResult()方法。 */
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == mRequestCode) {
            if (verifyPermissions(grantResults)) {
                if (mOnPermissionListener != null) mOnPermissionListener.onPermissionResult(true);
            } else {
                if (mOnPermissionListener != null) mOnPermissionListener.onPermissionResult(false);
            }
        }
    }

    /**
     * 判断权限有没有授权，无权限，不请求授权
     *
     * @param permissionName 权限名称
     * @return true 有此权限 false 没有此权限
     */
    public static boolean checkPermission(Context context, String permissionName) {
        PackageManager pkgManager = context.getApplicationContext().getPackageManager();
        return pkgManager.checkPermission(permissionName, context.getApplicationContext().getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

    public static String str2pinyin(String str){
        if(isEmpty(str)) return str;
        return Pinyin.toPinyin(str, "");
    }


    //--------------------------------------------------------------//



    private static int mRequestCode = -1;

    /**
     * 请求权限处理     * @param object        activity or fragment     * @param requestCode   请求码     * @param permissions   需要请求的权限     * @param callback      结果回调
     */
    @TargetApi(Build.VERSION_CODES.M)
    private static void requestPermissions(Object object, int requestCode, OnPermissionListener callback, String... permissions) {
        checkCallingObjectSuitability(object);
        mOnPermissionListener = callback;
        if (checkPermissions(getContext(object), permissions)) {
            if (mOnPermissionListener != null) mOnPermissionListener.onPermissionResult(true);
        } else {
            List<String> deniedPermissions = getDeniedPermissions(getContext(object), permissions);
            if (deniedPermissions.size() > 0) {
                mRequestCode = requestCode;
                if (object instanceof Activity) {
                    ((Activity) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
                } else if (object instanceof android.app.Fragment) {
                    ((android.app.Fragment) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
                } else if (object instanceof Fragment) {
                    ((Fragment) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
                }
            }
        }
    }

    /**
     * 获取上下文
     */
    private static Context getContext(Object object) {
        Context context;
        if (object instanceof android.app.Fragment) {
            context = ((android.app.Fragment) object).getActivity();
        } else if (object instanceof Fragment) {
            context = ((Fragment) object).getActivity();
        } else {
            context = (Activity) object;
        }
        return context;
    }

    /**
     * 验证权限是否都已经授权
     */
    private static boolean verifyPermissions(int[] grantResults) {        // 如果请求被取消，则结果数组为空
        if (grantResults.length <= 0) return false;         // 循环判断每个权限是否被拒绝
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取权限列表中所有需要授权的权限     * @param context       上下文     * @param permissions   权限列表     * @return
     */
    private static List<String> getDeniedPermissions(Context context, String... permissions) {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /**
     * 检查所传递对象的正确性     * @param object 必须为 activity or fragment
     */
    private static void checkCallingObjectSuitability(Object object) {
        if (object == null) {
            throw new NullPointerException("Activity or Fragment should not be null");
        }
        boolean isActivity = object instanceof Activity;
        boolean isSupportFragment = object instanceof Fragment;
        boolean isAppFragment = object instanceof android.app.Fragment;
        if (!(isActivity || isSupportFragment || isAppFragment)) {
            throw new IllegalArgumentException("Caller must be an Activity or a Fragment");
        }
    }

    /**
     * 检查所有的权限是否已经被授权     * @param permissions 权限列表     * @return
     */
    private static boolean checkPermissions(Context context, String... permissions) {
        if (isOverMarshmallow()) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断当前手机API版本是否 >= 6.0
     */
    private static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public interface OnPermissionListener {
        void onPermissionResult(boolean isGranted);
    }

    private static OnPermissionListener mOnPermissionListener;



}
