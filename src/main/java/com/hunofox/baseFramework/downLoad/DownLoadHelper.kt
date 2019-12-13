package com.hunofox.baseFramework.downLoad

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.hunofox.baseFramework.base.BaseApp
import com.hunofox.baseFramework.utils.CheckUtils
import java.io.File
import java.util.HashMap

/**
 * 项目名称：libproject
 * 项目作者：胡玉君
 * 创建日期：2019/12/2 16:30.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
class DownLoadHelper private constructor(){

    companion object {
        val instance: DownLoadHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { DownLoadHelper() }
    }

    private val REQUEST_CODE_UNKNOWN_APP = 80//8.0需要请求未知来源权限

    private var filePath: String? = null
    private var serviceMap: HashMap<String, Any>? = null

    /** 下载前开启服务，避免因为APP关闭而下载失败  */
    fun startDownLoad(
        url: String,
        folderName: String?,
        fileName: String?,
        listener: DownLoadListener?
    ) {
        release()
        if(CheckUtils.isEmpty(fileName)){
            listener?.onFailed("请指定保存文件名")
            return
        }

        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val downLoadService = (service as DownLoadService.DownLoadBinder).service
                downLoadService.setDownLoadListener(listener)
            }

            override fun onServiceDisconnected(name: ComponentName) {}
        }
        val intent = Intent(BaseApp.instance(), DownLoadService::class.java)
        intent.putExtra("folder", if (folderName!!.endsWith("/")) folderName else "$folderName/")
        intent.putExtra("url", url)
        intent.putExtra("fileName", fileName)
        BaseApp.instance().startService(intent)
        BaseApp.instance().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        serviceMap = HashMap()
        serviceMap!!["intent"] = intent
        serviceMap!!["conn"] = connection
    }

    fun release() {
        if (serviceMap != null) {
            try{
                if (serviceMap!!["conn"] != null && serviceMap!!["conn"] is ServiceConnection) {
                    val conn = serviceMap!!.remove("conn") as ServiceConnection
                    BaseApp.instance().unbindService(conn)
                }
            }catch (e:Exception){}

            try{
                if (serviceMap!!["intent"] != null && serviceMap!!["intent"] is Intent) {
                    val intent = serviceMap!!.remove("intent") as Intent
                    BaseApp.instance().stopService(intent)
                }
            }catch (e:Exception){}

            serviceMap!!.clear()
        }
        serviceMap = null
        filePath = null
    }

    /**
     * 安装apk文件，适配7.0/8.0
     *
     * 需要在清单文件中添加权限：android.permission.REQUEST_INSTALL_PACKAGES
     *
     * Activity需要写onActivityResult回调才行
     */
    fun installAPK(context: Activity, filePath: String): Boolean {
        val file = File(filePath)
        if (CheckUtils.isFileExists(file)) {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val pm = context.applicationContext.packageManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //适配8.0-未知来源权限
                    if (!pm.canRequestPackageInstalls()) {
                        this.filePath = filePath
                        val selfPackageUri = Uri.parse("package:" + context.applicationContext.packageName)
                        val permission = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, selfPackageUri)
                        context.startActivityForResult(permission, REQUEST_CODE_UNKNOWN_APP)
                        return false
                    }
                }

                //适配7.0 & 8.0
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                val packageName: String
                try {
                    packageName = pm.getPackageInfo(context.applicationContext.packageName, 0).packageName
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }

                val contentUri =
                    FileProvider.getUriForFile(context.applicationContext, "$packageName.fileProvider", file)
                intent.setDataAndType(
                    contentUri,
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk")
                )//"application/vnd.android.package-archive"
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.action = "android.intent.action.VIEW"
                intent.setDataAndType(Uri.fromFile(file), MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk"))
            }
            context.applicationContext.startActivity(intent)
            return true
        } else {
            return false
        }
    }

    fun onActivityResultInstall(requestCode: Int, resultCode: Int, context: Context) {
        if (requestCode == REQUEST_CODE_UNKNOWN_APP && !CheckUtils.isEmpty(filePath)) {
            if (CheckUtils.isEmpty(filePath)) {
                return
            }
            val file = File(filePath!!)
            if (!CheckUtils.isFileExists(file)) {
                return
            }
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent()
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                val packageName: String
                try {
                    val pm = context.applicationContext.packageManager
                    packageName = pm.getPackageInfo(context.applicationContext.packageName, 0).packageName
                } catch (e: Exception) {
                    e.printStackTrace()
                    return
                }

                val contentUri =
                    FileProvider.getUriForFile(context.applicationContext, "$packageName.fileProvider", file)
                intent.setDataAndType(
                    contentUri,
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk")
                )//"application/vnd.android.package-archive"
                context.applicationContext.startActivity(intent)
                filePath = null
            }
        }
    }


    /** 下载文件的回调  */
    interface DownLoadListener {
        //下载成功
        fun onSuccess(filePath: String)

        //下载失败
        fun onFailed(errorReason: String)

        //下载进度
        fun onProgress(currentSize: Long, maxSize: Long)
    }
}