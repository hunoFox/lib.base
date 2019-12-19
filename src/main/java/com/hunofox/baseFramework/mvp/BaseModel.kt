package com.hunofox.baseFramework.mvp

import com.hunofox.baseFramework.okHttp.NetResultCallback
import java.lang.ref.WeakReference

/**
 * 项目名称：OpenEyeVideo
 * 项目作者：胡玉君
 * 创建日期：2017/8/30 15:51.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
abstract class BaseModel(callback: NetResultCallback? = null){

    private val reference = WeakReference<NetResultCallback>(callback)

    //获取回调
    fun callback(): NetResultCallback?{
        return reference.get()
    }

    //进行解析(解析的同时要对数据进行合法化处理)
    abstract fun parseResponse(json:String):Any


}