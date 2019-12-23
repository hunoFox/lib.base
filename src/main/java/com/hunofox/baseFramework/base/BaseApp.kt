package com.hunofox.baseFramework.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.hunofox.baseFramework.downLoad.DownLoadHelper
import com.hunofox.baseFramework.utils.Logger
import kotlin.system.exitProcess

/**
 * 项目名称：
 * 项目作者：胡玉君
 * 创建日期：2019/8/20 13:16.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
open class BaseApp : Application() {

    companion object{

        @JvmStatic
        private lateinit var instance: BaseApp

        @JvmStatic
        fun instance():BaseApp = instance

        @JvmStatic
        private var toast:Toast? = null

        @SuppressLint("ShowToast")
        @JvmStatic
        fun toast(msg:String){
            if (toast == null) {
                toast = Toast.makeText(BaseApp.instance().applicationContext, "", Toast.LENGTH_LONG)
            }
            toast?.setText(msg)
            toast?.show()
        }
    }

    //管理Activity
    val activities = ArrayList<Activity>()

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


    /** 退出APP */
    fun exitApp(){
        DownLoadHelper.instance.release()

        for(bean in activities){
            bean.finish()
        }
        activities.clear()

        //根据进程ID，杀死该进程
        android.os.Process.killProcess(android.os.Process.myPid())
        //退出真个应用程序
        exitProcess(0)
    }

    /** 获取Sharepreference */
    fun getSharePreference(key:String):SharedPreferences{
        return applicationContext.getSharedPreferences(key, Context.MODE_PRIVATE)
    }
}