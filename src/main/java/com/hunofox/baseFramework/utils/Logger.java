package com.hunofox.baseFramework.utils;

import android.util.Log;
import com.hunofox.baseFramework.BuildConfig;

/**
 * 项目名称：HttpRequest
 * 项目作者：胡玉君
 * 创建日期：2017/7/13 16:15.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class Logger {

    public static void e(String tag, String msg) {
        if(BuildConfig.DEBUG)
            Log.e(tag, msg);
    }

    public static void d(String tag, String msg) {
        if(BuildConfig.DEBUG)
            Log.d(tag, msg);
    }


}
