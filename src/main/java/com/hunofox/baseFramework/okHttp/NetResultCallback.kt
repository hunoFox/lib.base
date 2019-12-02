package com.yibenanfu.tongtong.base

/**
 * 项目名称：
 * 项目作者：胡玉君
 * 创建日期：2017/8/30 15:52.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
interface NetResultCallback {

    fun onSuccess(response:String, flag:String)
    fun onErr(retFlag:String, retMsg:String, response:String? = null, flag:String? = null)

    fun getViewName():String?

}