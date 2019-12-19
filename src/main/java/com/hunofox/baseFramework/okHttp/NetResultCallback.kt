package com.hunofox.baseFramework.okHttp

/**
 * 项目名称：
 * 项目作者：胡玉君
 * 创建日期：2017/8/30 15:52.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
interface NetResultCallback {

    fun onSuccess(json:String, flag:String)
    fun onErr(retFlag:String, retMsg:String, json:String? = null, flag:String? = null)

    fun getViewName():String?

    fun inProgress(progress:Float, flag: String)

}