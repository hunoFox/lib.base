package com.hunofox.baseFramework.utils;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import com.hunofox.baseFramework.BuildConfig;
import com.hunofox.baseFramework.base.BaseApp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        if(BuildConfig.DEBUG){
            Log.e(tag, msg);
            saveLog("E", tag, msg);
        }

    }

    public static void d(String tag, String msg) {
        if(BuildConfig.DEBUG){
            Log.d(tag, msg);
            saveLog("D", tag, msg);
        }

    }

    /**
     * 打开日志文件并写入日志
     * @param logType
     * @param tag
     * @param text
     */
    public static void saveLog(final String logType, final String tag, final String text) {// 新建或打开日志文件
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            ExecutorService newThread = Executors.newCachedThreadPool();
            newThread.execute(new Runnable() {
                @Override
                public void run() {
                    String message = DateUtils.formatDate2String(new Date()) + " " + logType + "/" + tag + "：" + text;
                    String dirPath = BaseApp.instance().getExternalFilesDir("Log").getAbsolutePath();
                    File dirsFile = new File(dirPath);
                    if (!dirsFile.exists()){
                        dirsFile.mkdirs();
                    }

                    String fileName;
                    try {
                        fileName = DateUtils.formatDate2String(new Date(), false) + " V" + BaseApp.instance().getPackageManager().getPackageInfo(BaseApp.instance().getPackageName(),
                                PackageManager.GET_ACTIVITIES).versionName + ".log";
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        fileName = DateUtils.formatDate2String(new Date(), false) + ".log";
                    }
                    File file = new File(dirsFile.toString(), fileName);// MYLOG_PATH_SDCARD_DIR
                    if (!file.exists()) {
                        try {
                            //在指定的文件夹中创建文件
                            file.createNewFile();
                        } catch (Exception e) {
                        }
                    }else{
                        message = "\n" +message;
                    }

                    try {
                        FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                        BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                        bufWriter.write(message);
                        bufWriter.newLine();
                        bufWriter.close();
                        filerWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            newThread.shutdown();
        }
    }


}
