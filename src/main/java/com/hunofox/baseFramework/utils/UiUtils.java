package com.hunofox.baseFramework.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import com.hunofox.baseFramework.R;
import com.hunofox.baseFramework.base.BaseApp;


/**
 * 项目名称：kcwxApp
 * 项目作者：胡玉君
 * 创建日期：2017/9/13 10:35.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class UiUtils {

    /** dip 转换成 px */
    public static float dp2px(float dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, BaseApp.instance().getApplicationContext().getResources().getDisplayMetrics());
    }

    /** sp转px的方法。*/
    public static float sp2px(float spValue) {
        final float fontScale = BaseApp.instance().getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    /** 通过colorId获取颜色 */
    @ColorInt
    public static int getColor(@ColorRes int colorId){
        return BaseApp.instance().getApplicationContext().getResources().getColor(colorId);
    }

    /** 通过strId获取文字 */
    public static String getString(@StringRes int strId){
        return BaseApp.instance().getApplicationContext().getResources().getString(strId);
    }

    /**
     * 根据滑动距离设置透明度的方法
     * 单位均为px
     *
     * @param oldY          滑动前的距离
     * @param dy            滑动后的距离
     * @param maxDistance   最大滑动距离
     * @return              返回-1表示透明度维持当前状态;
     */
    public static int getAlphaByDistance(int oldY, int dy, float maxDistance){
        if(dy <= maxDistance){
            return (int) (Math.abs(dy/maxDistance)*255);
        }else if(oldY < dy && dy > maxDistance){
            return 255;
        }
        return -1;
    }

    /**
     * 关闭软键盘
     *
     * @param activity  当前页面
     */
    public static void hideKeyBord(Activity activity){
        if(activity != null && activity.getCurrentFocus() != null){
            ((InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }
    public static void showKeyBord(View view){
        if(view != null){
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public static void toggleKeyBord(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    /** 获取设备标识码 */
    public static String getDeviceID(){
        String deviceId = Settings.Secure.getString(BaseApp.instance().getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID) + Build.SERIAL;
        if(deviceId != null && deviceId.length() > 0){
            BaseApp.instance().getApplicationContext().getSharedPreferences("hunoFoxSp", Context.MODE_PRIVATE).edit().putString("hunoFoxSp_DeviceId", deviceId).apply();
            return deviceId;
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && BaseApp.instance().getApplicationContext().getPackageManager().checkPermission(Manifest.permission.READ_PHONE_STATE, BaseApp.instance().getApplicationContext().getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            try {
                TelephonyManager mTelephonyMgr = (TelephonyManager) BaseApp.instance().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                deviceId = mTelephonyMgr.getDeviceId();
                BaseApp.instance().getApplicationContext().getSharedPreferences("hunoFoxSp", Context.MODE_PRIVATE).edit().putString("hunoFoxSp_DeviceId", deviceId).apply();
            } catch (Exception e) {
                e.printStackTrace();
                deviceId = BaseApp.instance().getApplicationContext().getSharedPreferences("hunoFoxSp", Context.MODE_PRIVATE).getString("hunoFoxSp_DeviceId", "");
            }
        }else{
            deviceId = BaseApp.instance().getApplicationContext().getSharedPreferences("hunoFoxSp", Context.MODE_PRIVATE).getString("hunoFoxSp_DeviceId", "000000");
        }
        return deviceId;
    }

    /** 获取屏幕宽度 */
    public static int getDeviceWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)BaseApp.instance().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
    /** 获取屏幕高度 */
    public static int getDeviceHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)BaseApp.instance().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 创建圆角矩形，根据传入的color设置背景
     * @param color int型的color
     * @param px int型的像素值，可以借助MyUtils中的dp和px相互转换
     * @return drawable背景图
     */
    public static GradientDrawable createShape(@ColorInt int color, int px){
        GradientDrawable drawable = new GradientDrawable();//圆角矩形
        drawable.setCornerRadius(px);//圆角矩形的圆角，这里指5像素(可以借助UiUtils中的dp和px相互转换)
        drawable.setColor(color);
        return drawable;
    }


    /**
     * 通过代码设置selector,可以通过setBackGroundDrawable()方法设置该selector
     * @param pressedDrawable 按下时的背景图
     * @param normalDrawable 平时的背景图
     * @return selector文件配置效果
     */
    public static StateListDrawable createSelectorDrawable(Drawable pressedDrawable, Drawable normalDrawable){
        /**
         <selector xmlns:android="http://schemas.android.com/apk/res/android"  android:enterFadeDuration="200">
         <item  android:state_pressed="true" android:drawable="@drawable/detail_btn_pressed"></item>
         <item  android:drawable="@drawable/detail_btn_normal"></item>
         </selector>
         */
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        stateListDrawable.addState(new int[]{}, normalDrawable);
        return stateListDrawable;
    }

    public static Bitmap getImageBitmap(ImageView imageView){
        return ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    }

    /**
     * 获取应用图标
     *
     * @return
     */
    public static Drawable getAppIcon(){
        return BaseApp.instance().getResources().getDrawable(BaseApp.instance().getApplicationInfo().icon);
    }

    /**
     * 获取应用名称
     *
     * @return
     */
    public static String getAppName(){
        return BaseApp.instance().getString(BaseApp.instance().getApplicationInfo().labelRes);
    }


}
