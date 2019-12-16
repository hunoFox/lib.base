package com.hunofox.baseFramework.handleException

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import com.hunofox.baseFramework.base.BaseApp
import com.hunofox.baseFramework.utils.CheckUtils
import com.hunofox.baseFramework.utils.DateUtils
import com.hunofox.baseFramework.utils.UiUtils
import java.io.*
import java.util.*
import kotlin.collections.LinkedHashMap


/**
 * 项目名称：libproject
 * 项目作者：胡玉君
 * 创建日期：2019/12/10 16:12.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 *
 * AppExceptionHandler.instance.init {
        filePath ->

        Logger.e("onCreate", "$filePath(BaseApp.kt:58)")
    }
 * ----------------------------------------------------------------------------------------------------
 */
class AppExceptionHandler private constructor() : Thread.UncaughtExceptionHandler {

    companion object {
        //双重锁校验的单例
        val instance: AppExceptionHandler by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { AppExceptionHandler() }
    }

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val path = BaseApp.instance().getExternalFilesDir("Crash")?.absolutePath + "/"
    private val info = LinkedHashMap<String, String>()

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /** 设置log日志保存回调 */
    fun init(handleException:(String) -> Unit){
        this.handleException = handleException
    }

    override fun uncaughtException(t: Thread, e: Throwable?) {
        val fileName = handleException(e)
        if(handleException != null && !CheckUtils.isEmpty(fileName) && CheckUtils.isFileExists(path + fileName)){
            handleException!!.invoke(path+fileName)
        }
        defaultHandler?.uncaughtException(t, e)
    }

    private fun handleException(e: Throwable?): String? {
        if (e == null) {
            return null
        }

        collectDeviceInfo()
        return saveCrashInfo(e)
    }

    /**
     * 收集设备参数信息
     */
    private fun collectDeviceInfo() {
        try {
            val pm: PackageManager = BaseApp.instance().packageManager // 获得包管理器
            val pi: PackageInfo = pm.getPackageInfo(
                BaseApp.instance().packageName,
                PackageManager.GET_ACTIVITIES
            ) // 得到该应用的信息，即主Activity
            val versionName = if (pi.versionName == null) "null" else pi.versionName
            val versionCode = pi.versionCode.toString() + ""
            info["packageName"] = BaseApp.instance().packageName
            info["versionCode"] = versionCode
            info["versionName"] = versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        try{
            info["设备品牌"] = Build.BRAND
            info["设备型号"] = Build.MODEL
            info["SDK版本"] = Build.VERSION.SDK_INT.toString()
            info["设备标识码"] = UiUtils.getDeviceID()

            var cpu = StringBuilder()
            for(abi in Build.SUPPORTED_ABIS){
                cpu.append("$abi、")
            }
            info["CPU架构"] = cpu.toString().substring(0, cpu.toString().length-1)

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private fun saveCrashInfo(ex: Throwable): String? {
        val sb = StringBuffer()
        for ((key, value) in info) {
            sb.append("$key=$value\r\n")

            if(key == "versionName"){
                sb.append("==========================================\r\n")
            }
        }
        sb.append("==========================================\n")

        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        sb.append(writer.toString())
        try {
            val fileName = "Crash-${DateUtils.formatDate2String(Date())} V${info["versionName"]}.log"
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val dir = File(path)
                if (!CheckUtils.isFileExists(dir)) {
                    dir.mkdirs()
                }
                val log = File(path + fileName)
                if (!CheckUtils.isFileExists(log)) {
                    log.createNewFile()
                }

                val fos = FileOutputStream(log)
                fos.write(sb.toString().toByteArray())
                fos.close()
                return fileName
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private var handleException:((String) -> Unit)? = null
}