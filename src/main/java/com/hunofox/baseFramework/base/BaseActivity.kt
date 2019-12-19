package com.hunofox.baseFramework.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.hunofox.baseFramework.mvp.BasePresenter
import com.hunofox.baseFramework.mvp.BaseView
import com.hunofox.baseFramework.utils.CheckUtils
import com.hunofox.baseFramework.utils.Logger
import com.hunofox.baseFramework.utils.StatusBarUtils
import com.hunofox.baseFramework.widget.dialog.HintDialog
import com.hunofox.baseFramework.widget.dialog.LoadingProgress
import com.hunofox.baseFramework.widget.dialog.SelectDialog
import org.greenrobot.eventbus.EventBus


/**
 * 项目名称：OpenEyeVideo
 * 项目作者：胡玉君
 * 创建日期：2017/8/29 13:55.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 *
 * Kotlin默认类是final不可继承的，若要子类继承则需要使用open关键字
 * ----------------------------------------------------------------------------------------------------
 */
@SuppressLint("Registered")
open class BaseActivity : FragmentActivity(), BaseView, View.OnClickListener {

    private var hintDialog: HintDialog? = null
    private var selectDialog: SelectDialog? = null
    private var loading: LoadingProgress? = null
    private val presenters = ArrayList<BasePresenter<out BaseView>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d("当前Activity", "${this.javaClass.simpleName}(BaseActivity.kt:41)")
        BaseApp.instance().activities.add(0, this)
        try{
            if (isPortrait()) requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }catch (e:Exception){}

        if(getInputSoftMode() != -1){
            window.setSoftInputMode(getInputSoftMode())
        }
    }

    override fun onStart() {
        val statusBar = StatusBarUtils(this)
        setStatusBars(statusBar)
        statusBar.release()
        super.onStart()
    }


    //设置状态栏颜色，子类可重写该方法设置为其他颜色
    open fun setStatusBars(statusBar: StatusBarUtils) {
        statusBar.setColorBar(Color.WHITE)
        statusBar.setAndroidNativeLightStatusBar(true, false)
    }

    override fun onStop() {
        super.onStop()

        if(isFinishing){
            release()
        }
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }

    open fun release(){
        presenters.forEach { it.onDetach() }
        presenters.clear()
        showProgress(false)
        loading = null
        hintDialog?.dismiss()
        selectDialog?.dismiss()
        hintDialog?.release()
        selectDialog?.release()

        EventBus.getDefault().unregister(this)

        BaseApp.instance().activities.remove(this)
    }

    /**
     * 子类可重写该方法返回false使屏幕不再保持竖屏锁定
     *
     * open关键字表示该方法可以被重写
     */
    open fun isPortrait(): Boolean {
        return true
    }

    open fun getInputSoftMode():Int{
        return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
    }

    /**
     * 展示一个提示dialog，该dialog只有一个按钮，默认点击知道了：消失
     *
     * @param msg           提示信息，非空
     * @param onDialogListener  回调监听，可为null
     */
    override fun showDialog(msg: String, onDialogListener: HintDialog.OnDialogListener?) {
        try {
            if (hintDialog == null) {
                hintDialog = HintDialog.showDialog(this, msg, onDialogListener)
            } else {
                hintDialog?.show(msg, onDialogListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 展示一个Dialog，该dialog带有两个按钮
     *
     * @param msg               提示信息，非空
     * @param onDialogListener    回调监听，非空
     */
    override fun showDialog(msg: String, onDialogListener: SelectDialog.OnDialogListener) {
        try {
            if (selectDialog == null) {
                selectDialog = SelectDialog.showDialog(this, msg, onDialogListener)
            } else {
                selectDialog?.show(msg, onDialogListener)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 展示一个耗时操作的等待弹框
     *
     * @param show  true为展示;false收起
     * @param msg   提示文字，可为空
     */
    override fun showProgress(show: Boolean, msg: String?) {
        try{
            if (show) {
                if (loading == null) {
                    loading = LoadingProgress.show(this, msg, true) { this@BaseActivity.onBackPressed() }
                } else {
                    loading?.setMsg(msg)
                    loading?.show()
                }
            } else {
                loading?.dismiss()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {}

    override fun getPresenterList(): ArrayList<BasePresenter<out BaseView>> = presenters

    override fun onSuccess(json: String, flag: String) {}

    override fun onErr(retFlag: String, retMsg: String, json: String?, flag: String?) {
        showProgress(false)
        showDialog(retMsg)
    }

    override fun getViewName(): String? {
        return this.javaClass.simpleName
    }

    override fun inProgress(progress: Float, flag: String) {

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        CheckUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}