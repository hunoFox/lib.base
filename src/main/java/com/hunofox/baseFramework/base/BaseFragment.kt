package com.hunofox.baseFramework.base

import android.view.View
import androidx.fragment.app.Fragment
import com.hunofox.baseFramework.mvp.BasePresenter
import com.hunofox.baseFramework.mvp.BaseView
import com.hunofox.baseFramework.utils.CheckUtils
import com.hunofox.baseFramework.widget.dialog.EditDialog
import com.hunofox.baseFramework.widget.dialog.HintDialog
import com.hunofox.baseFramework.widget.dialog.SelectDialog
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

/**
 * 项目名称：OpenEyeVideo
 * 项目作者：胡玉君
 * 创建日期：2017/8/29 14:32.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
open class BaseFragment : Fragment(), BaseView, View.OnClickListener{

    private val presenters = ArrayList<BasePresenter<out BaseView>>()

    override fun onDestroyView() {
        super.onDestroyView()
        showProgress(false)
    }

    override fun onDestroy() {
        presenters.forEach { it.onDetach() }
        presenters.clear()
        activityCallBackReference?.clear()
        EventBus.getDefault().unregister(this)

        super.onDestroy()
    }

    /**
     * 展示一个提示dialog，该dialog只有一个按钮，默认点击知道了：消失
     *
     * @param msg           提示信息，非空
     * @param onDialogListener  回调监听，可为null
     */
    override fun showDialog(msg:String, onDialogListener: HintDialog.OnDialogListener?){
        if(activity is BaseActivity){
            (activity as BaseActivity).showDialog(msg, onDialogListener)
        }
    }

    /**
     * 展示一个Dialog，该dialog带有两个按钮
     *
     * @param msg               提示信息，非空
     * @param onDialogListener    回调监听，非空
     */
    override fun showDialog(msg:String, onDialogListener: SelectDialog.OnDialogListener){
        if(activity is BaseActivity){
            (activity as BaseActivity).showDialog(msg, onDialogListener)
        }
    }

    /**
     * 展示一个带文本编辑框的dialog
     */
    fun showDialog(hintMsg:String?, onDialogListener: EditDialog.OnDialogListener){
        if(activity is BaseActivity){
            (activity as BaseActivity).showDialog(hintMsg, onDialogListener)
        }
    }

    /**
     * 展示一个耗时操作的等待弹框
     *
     * @param show  true为展示;false收起
     * @param msg   提示文字，可为空
     */
    override fun showProgress(show:Boolean, msg:String?){
        if(activity == null || activity !is BaseActivity) return
        (activity as BaseActivity).showProgress(show, msg)
    }

    override fun getPresenterList(): ArrayList<BasePresenter<out BaseView>> = presenters

    override fun onClick(v: View?) {}

    override fun onSuccess(json: String, flag: String) {}

    override fun onErr(retFlag: String, retMsg: String, json: String?, flag: String?) {
        showProgress(false)
        showDialog(retMsg)
    }

    override fun getViewName(): String {
        return this.javaClass.simpleName
    }

    override fun inProgress(progress: Float, flag: String) {
        //文件上传进度回调
    }

    //activity回调
    protected var activityCallBackReference:WeakReference<ActivityCallBack?>? = null
    fun setOnActivityCallBack(activityCallBack:ActivityCallBack?){
        this.activityCallBackReference = WeakReference(activityCallBack)
    }
    interface ActivityCallBack{
        fun onActivityCallBack(params:Map<String, Any>)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        CheckUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}