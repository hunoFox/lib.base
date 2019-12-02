package com.hunofox.baseFramework.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.FrameLayout;
import androidx.annotation.ColorInt;
import com.hunofox.baseFramework.base.BaseApp;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 项目名称：状态栏工具
 * 项目作者：胡玉君
 * 创建日期：2017/8/3 10:30.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 *
 * 1. 可以设置状态栏颜色及透明度，使用时需要将他进行实例化
 *
 * 2. 实例化时应用局部变量，在使用完毕后立即release()释放
 * ----------------------------------------------------------------------------------------------------
 */
public class StatusBarUtils {

    private final WeakReference<Activity> reference;
    public StatusBarUtils(Activity activity) {
        this.reference = new WeakReference<>(activity);
    }

    private int navigationBarColor = Color.parseColor("#e5000000");
    public void setNavigationBarColor(@ColorInt int navigationBarColor){
        this.navigationBarColor = navigationBarColor;
    }

    //该方法必须在Activity的onDestroy()中释放
    public void release(){
        reference.clear();
    }

    //设置状态栏文字颜色
    public void setAndroidNativeLightStatusBar(boolean dark, boolean isTop) {

        //设置状态栏，不让ui顶到状态栏上去
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isTop) {
            View content = ((ViewGroup) reference.get().findViewById(android.R.id.content)).getChildAt(0);
            if (content != null) {
                content.setFitsSystemWindows(true);
            }
        }

        int type = RomUtils.getLightStatusBarAvailableRomType();

        if(type == RomUtils.AvailableRomType.ANDROID_NATIVE){
            View decor = reference.get().getWindow().getDecorView();
            if (dark) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }else if(type == RomUtils.AvailableRomType.MIUI){
            //小米手机设置状态栏文字
            Window window = reference.get().getWindow();
            if (window != null) {
                Class clazz = window.getClass();
                try {
                    int darkModeFlag = 0;
                    Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                    Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                    darkModeFlag = field.getInt(layoutParams);
                    Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                    if (dark) {
                        extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                    } else {
                        extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && RomUtils.isMiUIV7OrAbove()) {
                        //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                        if (dark) {
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        } else {
                            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }else if(type == RomUtils.AvailableRomType.FLYME){
            //魅族手机
            Window window = reference.get().getWindow();
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 设置指定颜色及透明度的状态栏
     *
     * @param color 颜色
     * @param alpha 透明度(在颜色上加一层蒙层),传入0则为全透明(无蒙层);数值越大透明度越小
     *
     * 使用示例：setColorBar(ContextCompat.getColor(this, R.color.DeepSkyBlue), 50);
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setColorBar(@ColorInt int color, int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = reference.get().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            int alphaColor = alpha == 0 ? color : calculateColor(color, alpha);
            window.setStatusBarColor(alphaColor);

            window.setNavigationBarColor(navigationBarColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = reference.get().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int alphaColor = alpha == 0 ? color : calculateColor(color, alpha);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            decorView.addView(createStatusBarView(reference.get(), alphaColor));
            if (navigationBarExist(reference.get())) {
                decorView.addView(createNavBarView(reference.get(), navigationBarColor));
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            setRootView(reference.get(), true);
        }
    }

    /**
     * 设置指定颜色的状态栏
     *
     * @param color 颜色
     *
     * 使用示例：setColorBar(ContextCompat.getColor(this, R.color.DeepSkyBlue));
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setColorBar(@ColorInt int color) {
        setColorBar(color, 0);
    }

    /**
     * 设置指定颜色及透明度的状态栏(为 android.support.v4.widget.DrawerLayout 布局专门设置的方法)
     *
     * @param color 颜色
     * @param alpha 透明度(在颜色上加一层蒙层),传入0则为全透明(无蒙层);数值越大透明度越小
     *
     * 使用示例：setColorBarForDrawer(ContextCompat.getColor(this, R.color.DeepSkyBlue), 50);
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setColorBarForDrawer(@ColorInt int color, int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = reference.get().getWindow();
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (navigationBarExist(reference.get())) {
                option = option | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            }
            decorView.setSystemUiVisibility(option);
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(Color.TRANSPARENT);
            int alphaColor = alpha == 0 ? color : calculateColor(color, alpha);
            decorView.addView(createStatusBarView(reference.get(), alphaColor), 0);
            if (navigationBarExist(reference.get())) {
                decorView.addView(createNavBarView(reference.get(), navigationBarColor), 1);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = reference.get().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int alphaColor = alpha == 0 ? color : calculateColor(color, alpha);
            decorView.addView(createStatusBarView(reference.get(), alphaColor), 0);
            if (navigationBarExist(reference.get())) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                decorView.addView(createNavBarView(reference.get(), navigationBarColor), 1);
            }
        }
    }

    /**
     * 设置指定颜色的状态栏(为 android.support.v4.widget.DrawerLayout 布局专门设置的方法)
     *
     * @param color 颜色
     *
     * 使用示例：setColorBarForDrawer(ContextCompat.getColor(this, R.color.DeepSkyBlue));
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setColorBarForDrawer(@ColorInt int color) {
        setColorBarForDrawer(color, 0);
    }

    /**
     * 设置透明状态栏(在标题栏上加一层蒙层展示状态栏)
     *
     * @param color 透明度颜色
     * @param alpha 透明度(传入0则为全透明状态栏，数值越大越不透明)
     *
     * 使用示例：setTransparentBar(ContextCompat.getColor(this, R.color.DeepSkyBlue), 50);
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setTransparentBar(@ColorInt int color, int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = reference.get().getWindow();
            View decorView = window.getDecorView();
            int option =
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);

            int finalColor = alpha == 0 ? Color.TRANSPARENT :
                    Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));

            window.setStatusBarColor(finalColor);
            window.setNavigationBarColor(navigationBarColor);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = reference.get().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            int finalColor = alpha == 0 ? Color.TRANSPARENT :
                    Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
            decorView.addView(createStatusBarView(reference.get(), finalColor));
            if (navigationBarExist(reference.get())) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                decorView.addView(createNavBarView(reference.get(), navigationBarColor));
            }
        }

    }

    /**
     * 设置全透明状态栏
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setTransparentBar() {
        setTransparentBar(Color.TRANSPARENT, 0);
    }

    /**
     * 隐藏状态栏
     *
     * 必须在onWindowFocusChanged()方法中hasFocus == true调用
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setHideBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = reference.get().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(){
        int resourceId = BaseApp.instance().getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0){
            return BaseApp.instance().getApplicationContext().getResources().getDimensionPixelOffset(resourceId);
        }
        return 0;
    }

/*---------------------------------------以下为辅助方法----------------------------------------*/
    private View createStatusBarView(Context context, @ColorInt int color) {
        View mStatusBarTintView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(context));
        params.gravity = Gravity.TOP;
        mStatusBarTintView.setLayoutParams(params);
        mStatusBarTintView.setBackgroundColor(color);
        return mStatusBarTintView;
    }

    private View createNavBarView(Context context, @ColorInt int color) {
        View mNavBarTintView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, getNavigationHeight(context));
        params.gravity = Gravity.BOTTOM;
        mNavBarTintView.setLayoutParams(params);
        mNavBarTintView.setBackgroundColor(color);
        return mNavBarTintView;
    }

    private boolean navigationBarExist(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    @ColorInt
    private int calculateColor(@ColorInt int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    private void setRootView(Activity activity, boolean fit) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(fit);
                ((ViewGroup) childView).setClipToPadding(fit);
            }
        }
    }

    private int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private int getNavigationHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

}
