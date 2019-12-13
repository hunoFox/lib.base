package com.hunofox.baseFramework.downLoad

import android.app.DownloadManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.*
import com.hunofox.baseFramework.R
import com.hunofox.baseFramework.base.BaseApp
import com.hunofox.baseFramework.utils.CheckUtils
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

/**
 * 项目名称：libproject
 * 项目作者：胡玉君
 * 创建日期：2019/12/2 16:41.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
class DownLoadService: Service() {

    private var listener: DownLoadHelper.DownLoadListener? = null
    private var downloadManager: DownloadManager? = null
    private var task: ProgressTask? = null
    private var query: DownloadManager.Query? = null
    private var handler: ProgressHandler? = null

    private var receiver: DownLoadReceiver? = null//设置为全局，因为在销毁时需要反注册;
    private var downId: Long = 0

    fun setDownLoadListener(listener: DownLoadHelper.DownLoadListener?) {
        this.listener = listener
        if (receiver != null) {
            receiver!!.setDownLoadListener(listener, downId)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return DownLoadBinder()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val url = intent.getStringExtra("url")
        var fileName = intent.getStringExtra("fileName")
        val path = intent.getStringExtra("folder")
        val filePath = baseContext.getExternalFilesDir(if(CheckUtils.isEmpty(path)) "Download" else "Download/$path")?.absolutePath + "/"
        if(Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()){
            if(!CheckUtils.isFileExists(filePath)){
                if(!File(filePath).mkdirs()){
                    DownLoadHelper.instance.release()
                    listener?.onFailed("文件创建失败")
                    return START_NOT_STICKY
                }
            }
        }else{
            DownLoadHelper.instance.release()
            listener?.onFailed("文件创建失败")
            return START_NOT_STICKY
        }

        val file = File(filePath + fileName!!)
        if (CheckUtils.isFileExists(file)) {
            val isDeleted = file.delete()
            if (!isDeleted) {
                fileName = System.currentTimeMillis().toString() + "_" + fileName
            }
        }
        initDownLoadManager(url, path, fileName)
        return START_NOT_STICKY
    }

    /** 初始化下载管理器  */
    private fun initDownLoadManager(url: String?, path: String?, fileName: String) {
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        receiver = DownLoadReceiver()

        val downLoadRequest = DownloadManager.Request(Uri.parse(url))
        downLoadRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        downLoadRequest.setAllowedOverRoaming(false)
        downLoadRequest.setMimeType("application/vnd.android.package-archive")
        downLoadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

        downLoadRequest.setVisibleInDownloadsUi(true)

        downLoadRequest.setDestinationInExternalFilesDir(baseContext, path, fileName)
        downLoadRequest.setTitle("正在下载")
        downId = downloadManager!!.enqueue(downLoadRequest)

        handler = ProgressHandler(this)
        query = DownloadManager.Query().setFilterById(downId)
        val timer = Timer()
        task = ProgressTask(this)
        timer.schedule(task, 0, 1000)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    inner class DownLoadBinder : Binder() {
        /**
         * 获取当前Service的实例
         * @return
         */
        val service: DownLoadService
            get() = this@DownLoadService
    }

    override fun onDestroy() {
        try {
            task!!.cancel()
        } catch (e: Exception) {
        }

        try {
            handler!!.reference!!.clear()
            handler!!.reference = null
            handler!!.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
        }

        handler = null
        task = null
        downloadManager = null
        query = null
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        this.stopSelf()
        listener = null
        downloadManager = null
        super.onDestroy()
    }

    /** 子线程定时任务  */
    private class ProgressTask(service: DownLoadService) : TimerTask() {
        internal var reference: WeakReference<DownLoadService> = WeakReference(service)

        override fun run() {
            val service = reference.get()
            if (service != null) {
                val cursor = service.downloadManager!!.query(service.query!!.setFilterById(service.downId))
                if (cursor != null && cursor.moveToFirst()) {
                    val msg = Message.obtain()
                    val bundle = Bundle()
                    val maxSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    bundle.putLong("maxSize", maxSize)
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        //下载已完成
                        bundle.putLong("currentSize", maxSize)
                        bundle.putLong("maxSize", maxSize)
                        msg.data = bundle
                        service.handler!!.sendMessage(msg)
                        cursor.close()
                        service.task!!.cancel()
                        return
                    }
                    val currentSize =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    bundle.putLong("currentSize", currentSize.toLong())
                    msg.data = bundle
                    service.handler!!.sendMessage(msg)
                }
                cursor?.close()
            }
        }
    }

    /** 主线程刷新ui  */
    private class ProgressHandler(service: DownLoadService) : Handler() {

        internal var reference: WeakReference<DownLoadService>? = null

        init {
            reference = WeakReference(service)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val service = reference!!.get()
            if (service != null) {
                val bundle = msg.data
                if (service.listener != null) {
                    service.listener!!.onProgress(bundle.getLong("currentSize"), bundle.getLong("maxSize"))
                }
            }
        }
    }

}