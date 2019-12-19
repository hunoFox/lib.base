package com.hunofox.baseFramework.mvp

import com.hunofox.baseFramework.okHttp.NetResultCallback
import com.hunofox.baseFramework.widget.dialog.HintDialog
import com.hunofox.baseFramework.widget.dialog.SelectDialog


/**
 * 项目名称：OpenEyeVideo
 * 项目作者：胡玉君
 * 创建日期：2017/8/30 10:37.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
interface BaseView: NetResultCallback {

    fun showDialog(msg:String, onDialogListener: SelectDialog.OnDialogListener)
    fun showDialog(msg: String, onDialogListener: HintDialog.OnDialogListener? = null)
    fun showProgress(show: Boolean, msg: String? = null)

    fun getPresenterList():ArrayList<BasePresenter<out BaseView>>//用于presenter与view解绑
}