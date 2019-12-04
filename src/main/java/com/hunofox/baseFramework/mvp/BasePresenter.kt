package com.hunofox.baseFramework.mvp

import com.hunofox.baseFramework.okHttp.utils.OkHttpUtils
import com.yibenanfu.tongtong.base.NetResultCallback
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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

    //弱引用
    private val reference = WeakReference<T>(view)
    var view:T by Delegates.notNull<T>()

    //RxAndroid
    private val rxSubscriptions = CompositeDisposable()
    //注册
    fun registerSubscribe(subscribe: Disposable){
        rxSubscriptions.add(subscribe)
    }

    init {
        onAttach()
    }

    private fun onAttach(){
        if(reference.get() != null){
            this.view = reference.get()!!
            view.getPresenterList().add(this)
        }
    }

    open fun onDetach(){
        view.showProgress(false)
        OkHttpUtils.getInstance().cancelTag(this)
        if(!rxSubscriptions.isDisposed){
            rxSubscriptions.dispose()
        }
        reference.clear()
    }

    override fun onSuccess(response: String, flag: String) {}

    override fun onErr(retFlag: String, retMsg: String, response: String?, flag: String?) {
        view.showProgress(false)
        view.onErr(retFlag, retMsg, response, flag)
    }

    override fun getViewName(): String? {
        return view.javaClass.simpleName
    }

}