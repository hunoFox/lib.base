package com.hunofox.baseFramework.mvp

import com.hunofox.baseFramework.okHttp.NetResultCallback
import com.hunofox.baseFramework.okHttp.utils.OkHttpUtils
import com.hunofox.baseFramework.utils.CheckUtils
import kotlinx.coroutines.Job
import java.lang.Exception
import java.lang.ref.WeakReference
import kotlin.properties.Delegates

/**
 * 项目名称：OpenEyeVideo
 * 项目作者：胡玉君
 * 创建日期：2017/8/30 10:22.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
open class BasePresenter<T: BaseView> (view:T): NetResultCallback {

    //弱引用View
    private val reference = WeakReference<T>(view)
    protected fun view() = reference.get()!!

    //Kotlin协程
    private val kotlinJobs = ArrayList<Job>()
    fun registerJob(job:Job){
        kotlinJobs.add(job)
    }

    init {
        onAttach()
    }

    private fun onAttach(){
        if(reference.get() != null){
            view().getPresenterList().add(this)
        }
    }

    open fun onDetach(){
        view().showProgress(false)
        OkHttpUtils.getInstance().cancelTag(getViewName())
        if(!CheckUtils.isEmpty(kotlinJobs)){
            for(job in kotlinJobs){
                try{
                    if(!job.isCancelled){
                        job.cancel()
                    }
                }catch (e:Exception){

                }
            }
        }
        kotlinJobs.clear()
        reference.clear()
    }

    override fun onSuccess(json: String, flag: String) {}

    override fun onErr(retFlag: String, retMsg: String, json: String?, flag: String?) {
        view().showProgress(false)
        view().onErr(retFlag, retMsg, json, flag)
    }

    override fun getViewName(): String? {
        return view().javaClass.simpleName
    }

    override fun inProgress(progress: Float, flag: String) {

    }

}